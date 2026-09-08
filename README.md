# Voting API

![CI](https://github.com/PimentaDevDocs/desafio-votacao/actions/workflows/ci.yml/badge.svg)

API REST para gerenciamento de pautas e sessões de votação em assembleias cooperativas.
Desafio entrege para processo seletivo da db completo em [docs/DESAFIO.md](docs/DESAFIO.md).

## Stack

- Java 21, Spring Boot 4.1
- Spring Data JPA, Bean Validation
- PostgreSQL 17 (Flyway para migrations)
- SpringDoc OpenAPI
- H2 apenas para perfil de teste

## Como rodar

Pré-requisitos: JDK 21 e Docker.

```
bash
docker compose up -d        # Postgres na porta 5433
./mvnw spring-boot:run      # aplicação na porta 8080
```

A porta 5433 evita conflito com instalações locais do Postgres.

Para apontar para outro banco,
use as variáveis `DB_URL`, `DB_USER` e `DB_PASSWORD`.

Swagger UI: http://localhost:8080/swagger-ui.html

## Testes

```bash
./mvnw test
```

Os testes de integração usam H2 em modo de compatibilidade com PostgreSQL as mesmas migrations Flyway não
dependem do Docker.

Cobertura pelo JaCoCo. Rodando `./mvnw verify` o relatório sai em `target/site/jacoco/index.html`
(97% das instruções). No Actions ele fica como artefato de cada build.

## Endpoints

| Método | Rota                          | Descrição                                                        |
|--------|-------------------------------|------------------------------------------------------------------|
| POST   | `/api/v1/topics`              | Cadastra uma pauta                                               |
| GET    | `/api/v1/topics/{id}`         | Consulta uma pauta                                               |
| POST   | `/api/v1/topics/{id}/session` | Abre a sessão de votação (`durationMinutes` opcional, default 1) |
| GET    | `/api/v1/topics/{id}/session` | Consulta a sessão                                                |
| POST   | `/api/v1/topics/{id}/votes`   | Registra o voto (`memberId`, `choice`: `YES`/`NO`)               |
| GET    | `/api/v1/topics/{id}/result`  | Resultado da votação                                             |

## Telas do cliente mobile (Anexo 1)

O app não tem tela fixa. Ele pede pro servidor a descrição da tela e desenha a partir do JSON.
O anexo descreve o comportamento (botão faz POST na `url` com o `body` mais os campos preenchidos)
mas não define o formato do JSON; confirmei com a recruiter e o formato ficou a meu critério.
Segui o que o anexo descreve: `tipo`, `titulo`, `itens`, e em cada botão ou opção `texto`, `url` e `body`.

| Rota                                              | Tipo       | O que monta                                       |
|---------------------------------------------------|------------|---------------------------------------------------|
| `GET /api/v1/screens/topics/new`                  | FORMULARIO | Cadastro de pauta (título e descrição)            |
| `GET /api/v1/screens/topics/{id}/session`         | FORMULARIO | Abertura de sessão (duração em minutos)           |
| `GET /api/v1/screens/topics/{id}/vote?memberId=`  | SELECAO    | Sim / Não, cada opção com a url e o body do voto  |

Exemplo da tela de voto:

```json
{
  "tipo": "SELECAO",
  "titulo": "Votar: Reforma do estatuto",
  "itens": [
    {"texto": "Sim", "url": "http://localhost:8080/api/v1/topics/1/votes", "body": {"memberId": "52998224725", "choice": "YES"}},
    {"texto": "Não", "url": "http://localhost:8080/api/v1/topics/1/votes", "body": {"memberId": "52998224725", "choice": "NO"}}
  ]
}
```

O `memberId` vai na query porque tela SELECAO não tem campo de entrada, então o `memberId` precisa vir de quem pede a tela

As urls são absolutas, montadas a partir de `APP_BASE_URL` (default `http://localhost:8080`). No emulador
Android usa `http://10.0.2.2:8080`; em aparelho físico, o IP da máquina na rede.

Os tipos de item são os três que o exemplo de POST do anexo mostra: `INPUT_TEXTO`, `INPUT_NUMERICO`,
`INPUT_DATA`, mais `TEXTO` pra rótulo. Tudo isso fica em `api/v1/dto/screen`, um pacote só.

## Elegibilidade do associado (bônus 1)

O `memberId` do voto passa a ser o CPF do associado, só dígitos. Antes de gravar o voto a API consulta
client (`EligibilityClient`), implementado aqui com o fake:

- CPF com dígitos verificadores inválidos → `404` (`CPF not found`)
- CPF válido → sorteio entre `ABLE_TO_VOTE` e `UNABLE_TO_VOTE`; `UNABLE` → `404` (`Member unable to vote`),
  como pede o enunciado. Fora dele, `422` seria mais preciso, por ser regra de negócio e não recurso inexistente.

O sorteio é 50/50 por padrão, então o mesmo CPF pode votar numa tentativa e ser recusado na
próxima. Para testar o fluxo sem o sorteio, suba a api com a taxa em 1 (1/1):

```
bash
ELIGIBILITY_ABLE_RATE=1 ./mvnw spring-boot:run
```

CPFs válidos para teste: `52998224725`, `11144477735`, `12345678909`.

A integração real entra como outra implementação de `EligibilityClient`, sem alterar o `VoteService`.

## Performance (bônus 2)

O ponto crítico é o registro do voto. Ele foi feito pra não ler nada antes de gravar:

- O voto é só um INSERT. Quem garante um voto por associado é a UNIQUE (voting_session_id, member_id),
  não um select antes. Select antes tem race condition e custa uma leitura por voto.
- A contagem usa esse mesmo índice com SUM(CASE) em uma query. Não carrega voto em memória.
- Não tem contador na sessão. Se tivesse, todo voto ia brigar pelo lock da mesma linha.
- Não precisei criar índice. As UNIQUE de vote e voting_session já cobrem as buscas.

O teste de carga fica em `perf/votes.js` (Grafana). Precisa da api rodando com o sorteio do bônus 1
desligado (`ELIGIBILITY_ABLE_RATE=1`) senão metade dos votos cai no 404:

```bash
k6 run perf/votes.js                            # 100 VUs, 30s
k6 run --vus 200 --duration 60s perf/votes.js   # cenário maior
```

Na minha máquina (Intel 10 gen, 16GB RAM, api e Postgres locais): 200 VUs por 60s deram 117.369 votos gravados,
1.939 req/s, p95 de 190ms e 0% de erro. O GET /result da pauta com 117 mil votos, com 218 mil na tabela,
respondeu em 20ms.

A mediana ficou em 97ms com pool de 20 conexões (`DB_POOL_SIZE`). Ou seja, o que sobe no pico é a fila
pela conexão, não o processamento. Em produção o ajuste seria pool e réplica, não código.

## Versionamento da API (bônus 3)

Versão no path, `/api/v1/...`. Pensei em header (`Accept: application/vnd.voting.v2+json`) e em query string,
mas path ganhou por três motivos:

- É app mobile. Versão antiga do app fica instalada por meses, e com a versão na URL qualquer log, proxy ou
  cache mostra na hora qual contrato aquela instalação usa. As telas do Anexo 1 já carregam URL absoluta,
  então a versão vai junto.
- Aparece no Swagger, no browser e no curl. Header versionado só funciona pra quem sabe que ele existe.
- Roteamento fácil. Gateway manda `/api/v2` pra outro serviço sem olhar header.

### O que muda de versão

Só quebra de contrato: tirar ou renomear campo, mudar tipo, mudar o significado, mudar status de erro.
Campo opcional novo, endpoint novo e valor novo em enum de resposta não quebram nada e ficam na mesma versão.

### Como entraria uma v2

`api.v1` tem só controller e dto. Service, repository, entity e o resto não têm versão e ficam em pacote
neutro. A `api.v2` nasce do lado, com os controllers e dtos dela, usando o mesmo domínio. Versiona o
contrato, não o negócio. No SpringDoc vira um grupo por versão.

Hoje os services recebem e devolvem os dtos da v1. Com uma versão só é o mais simples. Quando a v2 tiver
formato diferente, o mapeamento sobe pro controller (service devolve entidade, controller converte). É a
próxima refatoração na mesma linha da que tirou o domínio de dentro de `api.v1`.

A v1 continua no ar em paralelo, com data de desligamento avisada (headers `Deprecation` e `Sunset`), até
os apps instalados migrarem.

## Erros

Todas as respostas de erro seguem o formato RFC 7807 (`ProblemDetail`):

| Status | Quando                                                                          |
|--------|---------------------------------------------------------------------------------|
| 400    | Corpo inválido ou campo fora das regras de validação (lista em `errors`)        |
| 404    | Pauta ou sessão inexistente; CPF inválido ou associado sem permissão para votar |
| 409    | Sessão já aberta, ou associado que já votou nesta pauta                         |
| 422    | Voto em sessão encerrada                                                        |

## Logs

Todas as requisições recebem um `X-Correlation-Id` (gerado se o cliente não enviar), presente em
todas as linhas de log daquela requisição e devolvido no header da resposta. Para saída JSON
estruturada, basta `logging.structured.format.console=ecs`.

## Decisões de projeto

- `api.v1` só com controller e dto. Comecei com tudo dentro de `api.v1`. Quando fui escrever o
  versionamento notei que a estrutura contradizia o texto (service, entity e repository não têm versão) e
  movi o domínio pra pacote neutro antes de entregar.
- Sessão sem scheduler. Aberta ou fechada é o `closes_at` comparado com o relógio na hora da leitura.
  Sem job, sem coluna de status pra manter sincronizada.
- Voto único garantido no banco, não em Java. `saveAndFlush` + UNIQUE vira 409. Um `existsBy` antes do
  insert tem race condition; o teste de concorrência mostra isso.
- Resultado por agregação. `SUM(CASE)` em cima do índice da constraint. Contador na sessão ia serializar
  a escrita no lock de uma linha.
- Postgres pra rodar, H2 só nos testes, os dois com as mesmas migrations e `ddl-auto=validate`. Os testes
  validam o schema de verdade, não um schema que o Hibernate inventou.
- Erro em RFC 7807 com o `ProblemDetail` do próprio Spring. Uma exceção base por status (`NotFound`,
  `Conflict`, `BusinessRule`); o handler não sabe de regra de negócio.
- `BaseEntity` só com id e as datas de auditoria. Sem `createdBy` porque não tem usuário autenticado, sem
  soft delete porque nada é apagado. Voto é registro de assembleia.
- Pauta sem update nem delete. Alterar depois de votada muda o que foi votado. Apagar destrói o registro.
- `memberId` é o CPF (bônus 1). Um identificador só. `UNABLE_TO_VOTE` responde 404 porque o enunciado
  pede; 422 seria mais certo.
- Sem cache, fila nem Redis. Deu 117 mil votos por minuto num Postgres local sem nada disso. Colocar
  infra antes de medir seria over-engineering.
- CPF mascarado nos logs. Dado pessoal, só os três últimos dígitos.
- Java 21 onde ajuda: record pra dto, `sealed interface` no contrato das telas, virtual thread no teste de
  concorrência, text block nas queries e nos testes.
- Formato das telas do Anexo 1. O anexo não define o JSON e a empresa confirmou que era escolha minha.
  Fui pelo que o texto descreve e deixei tudo em `dto/screen` pra trocar fácil se o app usar outros nomes.

## Glossário

| Enunciado         | Código                          |
|-------------------|---------------------------------|
| pauta             | `Topic`                         |
| sessão de votação | `VotingSession`                 |
| voto Sim / Não    | `Vote`, `VoteChoice.YES` / `NO` |
| associado         | `memberId` (CPF)                |
