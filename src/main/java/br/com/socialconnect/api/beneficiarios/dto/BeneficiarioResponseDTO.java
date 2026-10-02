package br.com.socialconnect.api.beneficiarios.dto;

import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;

public record BeneficiarioResponseDTO(
        @Schema(example = "1")
        Long idBeneficiario,
        @Schema(example = "Maria da Silva")
        String nome,
        @Schema(example = "12345678900")
        String cpf,
        @Schema(example = "11999999999")
        String telefone,
        @Schema(example = "Rua das Flores, 123")
        String endereco,
        @Schema(example = "Renda familiar baixa")
        String situacaoVulnerabilidade,
        @Schema(example = "2026-09-11")
        LocalDate dataCadastro
) {
}