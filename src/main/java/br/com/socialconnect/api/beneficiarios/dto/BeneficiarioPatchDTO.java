package br.com.socialconnect.api.beneficiarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

public record BeneficiarioPatchDTO(
        @Schema(description = "Novo nome", example = "Maria da Silva Santos")
        @Size(max = 150, message = "{Size.nome}")
        String nome,
        @Schema(description = "Novo telefone", example = "11988887777")
        @Size(max = 20, message = "{Size.telefone}")
        String telefone,
        @Schema(description = "Novo endereço", example = "Avenida Brasil, 500")
        @Size(max = 255, message = "{Size.endereco}")
        String endereco,
        @Schema(description = "Nova situação de vulnerabilidade", example = "Desemprego")
        @Size(max = 500, message = "{Size.situacaoVulnerabilidade}")
        String situacaoVulnerabilidade
) {
}