package br.com.socialconnect.api.beneficiarios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

public record BeneficiarioRequestDTO(
        @Schema(description = "Nome completo do beneficiário", example = "Maria da Silva")
        @NotBlank(message = "{NotBlank.nome}")
        @Size(max = 150, message = "{Size.nome}")
        String nome,
        @Schema(description = "CPF com 11 dígitos ou CNPJ com 14 dígitos, sem formatação", example = "12345678900")
        @NotBlank(message = "{NotBlank.cpf}")
        @Pattern(regexp = "\\d{11}|\\d{14}", message = "{Pattern.cpf}")
        String cpf,
        @Schema(description = "Telefone com DDD", example = "11999999999")
        @Size(max = 20, message = "{Size.telefone}")
        String telefone,
        @Schema(description = "Endereço do beneficiário", example = "Rua das Flores, 123")
        @Size(max = 255, message = "{Size.endereco}")
        String endereco,
        @Schema(description = "Descrição da situação de vulnerabilidade", example = "Renda familiar baixa")
        @Size(max = 500, message = "{Size.situacaoVulnerabilidade}")
        String situacaoVulnerabilidade
) {
}