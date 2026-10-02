package br.com.socialconnect.api.beneficiarios.controller;

import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.socialconnect.api.beneficiarios.model.Beneficiario;
import br.com.socialconnect.api.beneficiarios.repository.BeneficiarioRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BeneficiarioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BeneficiarioRepository repository;

    @BeforeEach
    void limparBanco() {
        repository.deleteAll();
    }

    @Test
    void deveRetornar201AoCriarBeneficiarioValido() throws Exception {
        String payload = """
                {
                  "nome": "Maria da Silva",
                  "cpf": "12345678900",
                  "telefone": "11999999999",
                  "endereco": "Rua das Flores, 123",
                  "situacaoVulnerabilidade": "Renda familiar baixa"
                }
                """;

        mockMvc.perform(post("/api/v1/beneficiarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/v1/beneficiarios/")))
                .andExpect(jsonPath("$.nome").value("Maria da Silva"))
                .andExpect(jsonPath("$.cpf").value("12345678900"));
    }

    @Test
    void deveRetornar400QuandoPayloadForInvalido() throws Exception {
        String payload = """
                {
                  "nome": "",
                  "cpf": "123"
                }
                """;

        mockMvc.perform(post("/api/v1/beneficiarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Erro de validação"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void deveRetornar409QuandoCpfJaEstiverCadastrado() throws Exception {
        repository.save(Beneficiario.builder()
                .nome("Maria da Silva")
                .cpf("12345678900")
                .dataCadastro(LocalDate.now())
                .build());

        String payload = """
            {
              "nome": "Joao da Silva",
              "cpf": "12345678900"
            }
            """;

        mockMvc.perform(post("/api/v1/beneficiarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("CPF já cadastrado"))
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void deveRetornar404AoBuscarIdInexistente() throws Exception {
        mockMvc.perform(get("/api/v1/beneficiarios/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso não encontrado"))
                .andExpect(jsonPath("$.status").value(404));
    }
}