# Voting API

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

## Endpoints

| Método | Rota                          | Descrição                                                        |
|--------|-------------------------------|------------------------------------------------------------------|
| POST   | `/api/v1/topics`              | Cadastra uma pauta                                               |
| GET    | `/api/v1/topics/{id}`         | Consulta uma pauta                                               |
| POST   | `/api/v1/topics/{id}/session` | Abre a sessão de votação (`durationMinutes` opcional, default 1) |
| GET    | `/api/v1/topics/{id}/session` | Consulta a sessão                                                |
| POST   | `/api/v1/topics/{id}/votes`   | Registra o voto (`memberId`, `choice`: `YES`/`NO`)               |
| GET    | `/api/v1/topics/{id}/result`  | Resultado da votação                                             |

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

## Glossário

| Enunciado         | Código                          |
|-------------------|---------------------------------|
| pauta             | `Topic`                         |
| sessão de votação | `VotingSession`                 |
| voto Sim / Não    | `Vote`, `VoteChoice.YES` / `NO` |
| associado         | `memberId` (CPF)                |