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

## Erros

Todas as respostas de erro seguem o formato RFC 7807 (`ProblemDetail`):

| Status | Quando                                                                   |
|--------|--------------------------------------------------------------------------|
| 400    | Corpo inválido ou campo fora das regras de validação (lista em `errors`) |
| 404    | Pauta ou sessão inexistente                                              |
| 409    | Sessão já aberta, ou associado que já votou nesta pauta                  |
| 422    | Voto em sessão encerrada                                                 |

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
| associado         | `memberId`                      |