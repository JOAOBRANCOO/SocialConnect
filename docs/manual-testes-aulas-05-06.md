# Manual de Testes Manuais: Aulas 05 e 06

Este manual descreve como testar a API de beneficiários pelo IntelliJ IDEA, Swagger ou Postman.

## 1. Pré-requisitos

- PostgreSQL iniciado.
- Banco `socialconnect` criado.
- Usuário e senha iguais aos configurados em `src/main/resources/application.properties`.
- JDK compatível com o projeto.
- Projeto aberto no IntelliJ IDEA.

Criar o banco, caso ainda não exista:

```sql
CREATE DATABASE socialconnect;
```

A configuração atual usa:

```text
URL: jdbc:postgresql://localhost:5432/socialconnect
Usuário: postgres
Senha: 123456
Porta da API: 8080
```

Se necessário, ajuste `spring.datasource.password` no arquivo `application.properties`.

## 2. Iniciar a aplicação

1. Abra `ApiApplication.java` no IntelliJ.
2. Clique no botão verde de execução.
3. Aguarde a mensagem indicando que o servidor iniciou na porta `8080`.
4. Acesse o Swagger:

```text
http://localhost:8080/swagger-ui.html
```

Documentação OpenAPI em JSON:

```text
http://localhost:8080/api-docs
```

## 3. Criar beneficiário: POST

Endpoint:

```http
POST http://localhost:8080/api/v1/beneficiarios
Content-Type: application/json
```

Body:

```json
{
  "nome": "Maria da Silva",
  "cpf": "12345678900",
  "telefone": "11999999999",
  "endereco": "Rua das Flores, 123",
  "situacaoVulnerabilidade": "Renda familiar baixa"
}
```

Resultado esperado:

- Status `201 Created`.
- Cabeçalho `Location` apontando para o novo recurso.
- Resposta contendo `idBeneficiario` e `dataCadastro`.

Exemplo de resposta:

```json
{
  "idBeneficiario": 1,
  "nome": "Maria da Silva",
  "cpf": "12345678900",
  "telefone": "11999999999",
  "endereco": "Rua das Flores, 123",
  "situacaoVulnerabilidade": "Renda familiar baixa",
  "dataCadastro": "2026-09-11"
}
```

Não envie `idBeneficiario` nem `dataCadastro`; esses campos são gerados pela API.

## 4. Listar beneficiários: GET

```http
GET http://localhost:8080/api/v1/beneficiarios
```

Resultado esperado:

- Status `200 OK`.
- Resposta paginada com `content`, `totalElements` e `totalPages`.

### Paginação

```http
GET http://localhost:8080/api/v1/beneficiarios?page=0&size=10&sort=nome,asc
```

### Filtro por nome

```http
GET http://localhost:8080/api/v1/beneficiarios?nome=Maria
```

### Filtro exato por CPF

```http
GET http://localhost:8080/api/v1/beneficiarios?cpf=12345678900
```

## 5. Buscar por ID: GET

```http
GET http://localhost:8080/api/v1/beneficiarios/1
```

Resultado esperado:

- Status `200 OK` quando o ID existir.
- Status `404 Not Found` quando o ID não existir.

## 6. Atualização completa: PUT

O `PUT` substitui todos os dados editáveis do beneficiário.

```http
PUT http://localhost:8080/api/v1/beneficiarios/1
Content-Type: application/json
```

Body:

```json
{
  "nome": "Maria da Silva Santos",
  "cpf": "12345678900",
  "telefone": "11988887777",
  "endereco": "Avenida Brasil, 500",
  "situacaoVulnerabilidade": "Desemprego"
}
```

Resultado esperado:

- Status `200 OK`.
- Todos os campos enviados são atualizados.
- `idBeneficiario` e `dataCadastro` permanecem controlados pelo servidor.

## 7. Atualização parcial: PATCH

O `PATCH` altera somente os campos enviados.

```http
PATCH http://localhost:8080/api/v1/beneficiarios/1
Content-Type: application/json
```

Body:

```json
{
  "telefone": "11977776666"
}
```

Resultado esperado:

- Status `200 OK`.
- Somente o telefone é alterado.
- Nome, CPF, endereço e situação permanecem iguais.

## 8. Exclusão: DELETE

```http
DELETE http://localhost:8080/api/v1/beneficiarios/1
```

Resultado esperado:

- Status `204 No Content` quando o registro existir.
- Status `404 Not Found` quando o ID não existir.

## 9. Testes de validação: Aula 06

### Nome obrigatório

Envie:

```json
{
  "nome": "",
  "cpf": "12345678900"
}
```

Resultado esperado: `400 Bad Request`.

### CPF obrigatório

Envie:

```json
{
  "nome": "Maria da Silva",
  "cpf": ""
}
```

Resultado esperado: `400 Bad Request`.

### CPF com formato inválido

Envie:

```json
{
  "nome": "Maria da Silva",
  "cpf": "ABC123"
}
```

Resultado esperado: `400 Bad Request`.

### Campo acima do limite

Envie um nome com mais de 150 caracteres ou um telefone com mais de 20 caracteres.

Resultado esperado: `400 Bad Request`.

## 10. Teste de CPF duplicado

1. Crie um beneficiário usando o CPF `12345678900`.
2. Envie outro `POST` usando o mesmo CPF.

Resultado esperado:

- Status `409 Conflict`.
- Resposta no formato Problem Details:

```json
{
  "type": "https://socialconnect.api/errors/cpf-duplicado",
  "title": "CPF já cadastrado",
  "status": 409,
  "detail": "O CPF 12345678900 já está cadastrado no sistema.",
  "instance": "/api/v1/beneficiarios",
  "timestamp": "2026-09-11T21:30:00",
  "errors": []
}
```

## 11. Teste de recurso inexistente

```http
GET http://localhost:8080/api/v1/beneficiarios/999999
```

Resultado esperado:

```text
404 Not Found
```

Exemplo de resposta:

```json
{
  "type": "https://socialconnect.api/errors/nao-encontrado",
  "title": "Recurso não encontrado",
  "status": 404,
  "detail": "Beneficiário não encontrado com o ID: 999999",
  "instance": "/api/v1/beneficiarios/999999",
  "timestamp": "2026-09-11T21:30:00",
  "errors": []
}
```

## 12. Teste de erro de validação RFC 7807

Envie um body inválido, por exemplo:

```json
{
  "nome": "",
  "cpf": "ABC"
}
```

Resultado esperado:

- Status `400 Bad Request`.
- Mensagens em português.
- Lista `errors` identificando os campos inválidos.

Exemplo:

```json
{
  "type": "https://socialconnect.api/errors/validacao",
  "title": "Erro de validação",
  "status": 400,
  "detail": "Um ou mais campos são inválidos.",
  "instance": "/api/v1/beneficiarios",
  "timestamp": "2026-09-11T21:30:00",
  "errors": [
    {
      "field": "nome",
      "message": "Nome é obrigatório"
    },
    {
      "field": "cpf",
      "message": "CPF deve ter 11 ou 14 dígitos"
    }
  ]
}
```

## 13. Checklist final

- [ ] Aplicação iniciou na porta `8080`.
- [ ] PostgreSQL conectou sem erro.
- [ ] Swagger abriu corretamente.
- [ ] POST retornou `201` e `Location`.
- [ ] GET retornou lista paginada.
- [ ] Filtro por nome funcionou.
- [ ] Filtro por CPF funcionou.
- [ ] GET por ID funcionou.
- [ ] PUT atualizou todos os campos.
- [ ] PATCH atualizou somente o campo enviado.
- [ ] DELETE retornou `204`.
- [ ] Dados inválidos retornaram `400` com detalhes.
- [ ] CPF duplicado retornou `409`.
- [ ] ID inexistente retornou `404`.
