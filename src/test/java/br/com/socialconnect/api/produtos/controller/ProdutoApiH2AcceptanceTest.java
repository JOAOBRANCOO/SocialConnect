package br.com.socialconnect.api.produtos.controller;

import br.com.socialconnect.api.produtos.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = "springdoc.api-docs.enabled=true")
class ProdutoApiH2AcceptanceTest {
    private static final String BASE = "/api/v1/produtos";
    private final HttpClient http = HttpClient.newHttpClient();

    @LocalServerPort private int port;
    @Autowired private ProdutoRepository repository;

    @BeforeEach
    void limparBanco() { repository.deleteAll(); }

    @Test
    void deveCriarConsultarAtualizarListarEExcluirProduto() throws Exception {
        // Arrange e Act: cria um produto e obtém o ID fornecido no Location.
        HttpResponse<String> criado = post(BASE, payload("Arroz 5kg", 3, 10));

        // Assert: criação e alerta de estoque baixo.
        assertEquals(201, criado.statusCode());
        assertTrue(criado.body().contains("\"estoqueBaixo\":true"));
        String location = criado.headers().firstValue("Location").orElseThrow();
        String id = location.substring(location.lastIndexOf('/') + 1);

        // Act e Assert: consulta por ID e filtro de listagem.
        assertEquals(200, get(BASE + "/" + id).statusCode());
        HttpResponse<String> lista = get(BASE + "?nome=arroz&categoria=ALIMENTO&page=0&size=5&sort=nome,asc");
        assertEquals(200, lista.statusCode());
        assertTrue(lista.body().contains("Arroz 5kg"));

        // Act e Assert: atualização integral e exclusão.
        assertEquals(200, put(BASE + "/" + id, payload("Arroz 5kg", 12, 10)).statusCode());
        assertTrue(get(BASE + "/" + id).body().contains("\"estoqueBaixo\":false"));
        assertEquals(204, delete(BASE + "/" + id).statusCode());
        assertEquals(404, get(BASE + "/" + id).statusCode());
    }

    @Test
    void deveRetornar409ParaNomeDuplicadoSemDiferenciarMaiusculas() throws Exception {
        // Arrange
        assertEquals(201, post(BASE, payload("Arroz", 5, 10)).statusCode());

        // Act
        HttpResponse<String> resposta = post(BASE, payload("ARROZ", 5, 10));

        // Assert
        assertEquals(409, resposta.statusCode());
        assertTrue(resposta.body().contains("\"status\":409"));
    }

    @Test
    void deveRetornar422ParaEstoqueNegativoE400ParaCampoObrigatorioAusente() throws Exception {
        // Arrange, Act e Assert: estoque negativo é semanticamente não processável.
        HttpResponse<String> estoque = post(BASE, payload("Feijao", -1, 10));
        assertEquals(422, estoque.statusCode());
        assertTrue(estoque.body().contains("\"status\":422"));

        // Act e Assert: campo ausente continua sendo erro de validação do payload.
        HttpResponse<String> ausente = post(BASE, "{\"categoria\":\"ALIMENTO\"}");
        assertEquals(400, ausente.statusCode());
        assertTrue(ausente.body().contains("\"status\":400"));
    }

    @Test
    void deveExporEspecificacaoOpenApiDosProdutos() throws Exception {
        // Act
        HttpResponse<String> resposta = get("/api-docs");

        // Assert
        assertEquals(200, resposta.statusCode());
        assertTrue(resposta.body().contains("/api/v1/produtos"));
        assertTrue(resposta.body().contains("/api/v1/produtos/{idProduto}"));
        assertTrue(resposta.body().contains("\"get\""));
        assertTrue(resposta.body().contains("\"post\""));
        assertTrue(resposta.body().contains("\"put\""));
        assertTrue(resposta.body().contains("\"delete\""));
        assertTrue(resposta.body().contains("ProdutoRequestDTO"));
        assertTrue(resposta.body().contains("estoqueAtual"));

        HttpResponse<String> swagger = get("/swagger-ui.html");
        assertTrue(swagger.statusCode() == 200 || (swagger.statusCode() >= 300 && swagger.statusCode() < 400));
    }

    private HttpResponse<String> get(String path) throws Exception {
        return enviar("GET", path, null);
    }

    private HttpResponse<String> delete(String path) throws Exception {
        return enviar("DELETE", path, null);
    }

    private HttpResponse<String> post(String path, String body) throws Exception {
        return enviar("POST", path, body);
    }

    private HttpResponse<String> put(String path, String body) throws Exception {
        return enviar("PUT", path, body);
    }

    private HttpResponse<String> enviar(String method, String path, String body) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path));
        if (body == null) builder.method(method, HttpRequest.BodyPublishers.noBody());
        else builder.header("Content-Type", "application/json")
                .method(method, HttpRequest.BodyPublishers.ofString(body));
        return http.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private String payload(String nome, int estoqueAtual, int estoqueMinimo) {
        return """
                {"nome":"%s","categoria":"ALIMENTO","estoqueAtual":%d,"estoqueMinimo":%d,"unidadeMedida":"kg"}
                """.formatted(nome, estoqueAtual, estoqueMinimo);
    }
}
