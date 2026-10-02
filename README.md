# SocialConnect API

> API RESTful de gestão para instituições sociais (ONGs, bancos de alimentos,
> CRAS, abrigos). Conecta doadores, voluntários e beneficiários.

**Disciplina:** Tópicos Especiais em Sistemas para Internet III
**Stack:** Java 21 · Spring Boot 4.1.1 · JPA · PostgreSQL · H2 (testes)

---

## Como Rodar

### Pré-requisitos

- JDK 21 LTS ([Adoptium](https://adoptium.net/))
- Maven 3.9+ (ou use o wrapper: `./mvnw`)
- PostgreSQL em execução e um banco chamado `socialconnect`
- IDE: IntelliJ IDEA (recomendado) ou VS Code

Na configuração local padrão, a aplicação conecta em
`jdbc:postgresql://localhost:5432/socialconnect`, com usuário `postgres` e
senha `123456`. Esses valores estão em `src/main/resources/application.properties`;
se o PostgreSQL local usar outras credenciais, ajuste essa configuração antes de
iniciar a aplicação. A variável `DB_URL` pode substituir a URL JDBC.

### Passos

```bash
# 1. Clone o repositório
git clone https://github.com/JOAOBRANCOO/SocialConnect.git
cd SocialConnect

# 2. Com o PostgreSQL iniciado, crie nele o banco socialconnect.
# 3. Compile e inicie (Windows PowerShell)
.\mvnw.cmd clean compile
.\mvnw.cmd spring-boot:run

# Linux/macOS, use ./mvnw clean compile e ./mvnw spring-boot:run
```

## Manual de Testes

Consulte o passo a passo completo para testar as Aulas 05 e 06:

[Manual de testes manuais das Aulas 05 e 06](docs/manual-testes-aulas-05-06.md)

## Módulo de Produtos (A1)

O módulo oferece cadastro, consulta paginada, atualização integral e remoção em
`/api/v1/produtos`. A listagem aceita os filtros opcionais `nome` (parcial) e
`categoria` (`ALIMENTO`, `ROUPA`, `HIGIENE` ou `OUTROS`), além dos parâmetros
Spring Data `page`, `size` e `sort`. Produtos usam estoque não negativo, nome
único sem distinção entre maiúsculas e minúsculas e expõem o indicador
`estoqueBaixo` quando `estoqueAtual < estoqueMinimo`.

### Endpoints principais

| Método | Caminho | Resultado |
|---|---|---|
| GET | `/api/v1/produtos` | Lista produtos paginados e filtrados |
| GET | `/api/v1/produtos/{idProduto}` | Consulta um produto |
| POST | `/api/v1/produtos` | Cria produto (201 e `Location`) |
| PUT | `/api/v1/produtos/{idProduto}` | Atualiza todos os dados editáveis |
| DELETE | `/api/v1/produtos/{idProduto}` | Remove produto (204) |

Erros de validação usam Problem Details: dados de formato inválido retornam
400, estoque negativo retorna 422, nome duplicado retorna 409 e recurso
inexistente retorna 404. A documentação interativa fica em
`http://localhost:8080/swagger-ui.html`.

### Testes

`./mvnw test` executa os testes unitários e os testes de integração. Os testes
de integração de produtos iniciam PostgreSQL 17 via Testcontainers e requerem o
Docker Desktop em execução, com suporte à virtualização habilitado. Quando o
Docker não está disponível, esses testes são ignorados pela configuração da
suíte; os demais testes continuam sendo executados. Os testes preexistentes dos
beneficiários usam H2.

## Declaração de Uso de IA (A1)

### Ferramentas utilizadas

- [x] OpenAI Codex
- [ ] ChatGPT / Claude / Gemini
- [ ] Copilot / Codeium
- [ ] Nenhuma

### Como utilizei

- Pedi ao Codex para analisar os requisitos de `avaliacao1.html` e comparar com o projeto existente.
- Usei a IA como apoio na implementação dos DTOs, service, controller, documentação OpenAPI e migration do módulo de produtos.
- Usei a IA para criar e revisar testes unitários com Mockito e testes de integração com Testcontainers.
- Usei a IA para investigar problemas encontrados durante a execução, incluindo configuração Flyway no perfil de testes e disponibilidade do Docker.

### O que entendo 100%

> Confirme cada item somente depois de revisar o código e conseguir explicar a implementação.

- [x] A lógica de validação de estoque não negativo e do alerta de estoque baixo.
- [x] A verificação de nome único e o tratamento de erros HTTP com Problem Details.
- [x] O padrão AAA e o uso do Mockito nos testes unitários.
- [x] A configuração do Testcontainers com PostgreSQL 17.

### O que precisei estudar mais

- [ ] Como evitar condições de corrida em atualizações concorrentes de estoque, usando controle otimista com `@Version` ou bloqueio no banco.
- [x] Como organizar transações com `@Transactional` e garantir consistência quando uma operação envolve mais de uma alteração.
- [ ] Como escrever consultas paginadas e filtros eficientes com Spring Data JPA, incluindo índices no PostgreSQL.
- [x] Como evoluir o esquema do banco com migrations Flyway sem perder ou corromper dados existentes.
- [ ] Como proteger a API com autenticação e autorização e armazenar credenciais de configuração fora do código-fonte.
- [x] Como ampliar os testes de integração para cobrir concorrência, falhas de banco e execução reproduzível em CI.
