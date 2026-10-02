package br.com.socialconnect.api.produtos.controller;

import br.com.socialconnect.api.produtos.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProdutoControllerIntegrationTest {
    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:17-alpine");

    @DynamicPropertySource
    static void configurarBanco(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired private ProdutoRepository repository;
    @LocalServerPort private int port;
    private final HttpClient http = HttpClient.newHttpClient();

    @BeforeEach
    void limparBanco() { repository.deleteAll(); }

    @Test
    void deveCriarProdutoQuandoDadosValidos() {
        // Arrange
        // Act
        HttpResponse<String> resposta = post("Doacao arroz", 3);

        // Assert
        assertEquals(201, resposta.statusCode());
        assertNotNull(resposta.headers().firstValue("Location").orElse(null));
        assertTrue(resposta.body().contains("\"estoqueBaixo\":true"));
    }

    @Test
    void deveRetornar409QuandoNomeDuplicado() {
        // Arrange
        post("Doacao arroz", 3);

        // Act
        HttpResponse<String> resposta = post("Doacao arroz", 3);

        // Assert
        assertEquals(409, resposta.statusCode());
        assertTrue(resposta.body().contains("\"status\":409"));
    }

    @Test
    void deveRetornar422QuandoEstoqueNegativo() {
        // Arrange
        // Act
        HttpResponse<String> resposta = post("Doacao feijao", -1);

        // Assert
        assertEquals(422, resposta.statusCode());
        assertTrue(resposta.body().contains("\"status\":422"));
    }

    private HttpResponse<String> post(String nome, int estoqueAtual) {
        String body = """
                {"nome":"%s","categoria":"ALIMENTO","estoqueAtual":%d,"estoqueMinimo":10,"unidadeMedida":"kg"}
                """.formatted(nome, estoqueAtual);
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/v1/produtos"))
                .header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(body)).build();
        try {
            return http.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception exception) {
            throw new AssertionError("Falha na requisição de integração", exception);
        }
    }
}
