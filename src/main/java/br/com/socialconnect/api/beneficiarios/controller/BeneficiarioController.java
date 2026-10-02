package br.com.socialconnect.api.beneficiarios.controller;

import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioPatchDTO;
import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioRequestDTO;
import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioResponseDTO;
import br.com.socialconnect.api.beneficiarios.service.BeneficiarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/beneficiarios")
@Tag(name = "Beneficiários", description = "API para gestão de beneficiários")
public class BeneficiarioController {

    private final BeneficiarioService service;

    public BeneficiarioController(BeneficiarioService service) {
        this.service = service;
    }

    @GetMapping
        @Operation(summary = "Lista beneficiários", description = "Retorna uma lista paginada com filtros opcionais por nome ou CPF.")
        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<Page<BeneficiarioResponseDTO>> listar(
            @Parameter(description = "Nome para filtro parcial", example = "Maria")
            @RequestParam(required = false) String nome,
            @Parameter(description = "CPF para filtro exato", example = "12345678900")
            @RequestParam(required = false) String cpf,
            @ParameterObject
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(service.listar(nome, cpf, pageable));
    }

    @GetMapping("/{idBeneficiario}")
        @Operation(summary = "Busca beneficiário por ID")
            @ApiResponse(responseCode = "200", description = "Beneficiário encontrado")
            @ApiResponse(responseCode = "404", description = "Beneficiário não encontrado")
    public ResponseEntity<BeneficiarioResponseDTO> buscarPorId(@PathVariable Long idBeneficiario) {
        return ResponseEntity.ok(service.buscarPorId(idBeneficiario));
    }

    @PostMapping
        @Operation(summary = "Cria um beneficiário")
            @ApiResponse(responseCode = "201", description = "Beneficiário criado com sucesso")
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
            @ApiResponse(responseCode = "409", description = "CPF já cadastrado")
    public ResponseEntity<BeneficiarioResponseDTO> criar(
            @Valid @RequestBody BeneficiarioRequestDTO dto) {
        BeneficiarioResponseDTO salvo = service.criar(dto);
        URI location = URI.create("/api/v1/beneficiarios/" + salvo.idBeneficiario());
        return ResponseEntity.created(location).body(salvo);
    }

    @PutMapping("/{idBeneficiario}")
        @Operation(summary = "Atualiza completamente um beneficiário")
            @ApiResponse(responseCode = "200", description = "Beneficiário atualizado")
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
            @ApiResponse(responseCode = "404", description = "Beneficiário não encontrado")
            @ApiResponse(responseCode = "409", description = "CPF já cadastrado")
    public ResponseEntity<BeneficiarioResponseDTO> atualizar(
            @PathVariable Long idBeneficiario,
            @Valid @RequestBody BeneficiarioRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(idBeneficiario, dto));
    }

    @PatchMapping("/{idBeneficiario}")
        @Operation(summary = "Atualiza parcialmente um beneficiário")
            @ApiResponse(responseCode = "200", description = "Beneficiário atualizado")
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
            @ApiResponse(responseCode = "404", description = "Beneficiário não encontrado")
    public ResponseEntity<BeneficiarioResponseDTO> atualizarParcial(
            @PathVariable Long idBeneficiario,
            @Valid @RequestBody BeneficiarioPatchDTO dto) {
        return ResponseEntity.ok(service.atualizarParcial(idBeneficiario, dto));
    }

    @DeleteMapping("/{idBeneficiario}")
        @Operation(summary = "Remove um beneficiário")
            @ApiResponse(responseCode = "204", description = "Beneficiário removido")
            @ApiResponse(responseCode = "404", description = "Beneficiário não encontrado")
    public ResponseEntity<Void> deletar(@PathVariable Long idBeneficiario) {
        service.deletar(idBeneficiario);
        return ResponseEntity.noContent().build();
    }
}