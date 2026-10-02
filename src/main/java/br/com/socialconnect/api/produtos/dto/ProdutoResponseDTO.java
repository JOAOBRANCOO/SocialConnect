package br.com.socialconnect.api.produtos.dto;

import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Produto cadastrado no estoque")
public record ProdutoResponseDTO(
        @Schema(example = "1") Long idProduto,
        @Schema(example = "Arroz 5kg") String nome,
        @Schema(example = "ALIMENTO") CategoriaProduto categoria,
        @Schema(example = "3") Integer estoqueAtual,
        @Schema(example = "10") Integer estoqueMinimo,
        @Schema(example = "unidade") String unidadeMedida,
        @Schema(example = "2026-10-02") LocalDate dataCadastro,
        @Schema(description = "True quando o estoque atual está abaixo do mínimo", example = "true")
        boolean estoqueBaixo
) {}
