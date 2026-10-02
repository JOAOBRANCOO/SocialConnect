package br.com.socialconnect.api.beneficiarios.service;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioPatchDTO;
import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioRequestDTO;
import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioResponseDTO;
import br.com.socialconnect.api.beneficiarios.model.Beneficiario;
import br.com.socialconnect.api.beneficiarios.repository.BeneficiarioRepository;
import br.com.socialconnect.api.exception.CpfDuplicadoException;
import br.com.socialconnect.api.exception.RecursoNaoEncontradoException;

@ExtendWith(MockitoExtension.class)
class BeneficiarioServiceTest {

    @Mock
    private BeneficiarioRepository repository;

    @InjectMocks
    private BeneficiarioService service;

    @Test
    void deveCriarBeneficiarioQuandoCpfAindaNaoExiste() {
        // Arrange
        BeneficiarioRequestDTO dto = novoRequest();
        Beneficiario salvo = Beneficiario.builder()
                .idBeneficiario(1L)
                .nome(dto.nome())
                .cpf(dto.cpf())
                .telefone(dto.telefone())
                .endereco(dto.endereco())
                .situacaoVulnerabilidade(dto.situacaoVulnerabilidade())
                .dataCadastro(LocalDate.now())
                .build();
        when(repository.existsByCpf(dto.cpf())).thenReturn(false);
        when(repository.save(any(Beneficiario.class))).thenReturn(salvo);

        // Act
        BeneficiarioResponseDTO resultado = service.criar(dto);

        // Assert
        assertEquals(1L, resultado.idBeneficiario());
        assertEquals(dto.nome(), resultado.nome());
        assertEquals(dto.cpf(), resultado.cpf());
        verify(repository).save(any(Beneficiario.class));
    }

    @Test
    void deveRecusarCriacaoQuandoCpfJaExiste() {
        // Arrange
        BeneficiarioRequestDTO dto = novoRequest();
        when(repository.existsByCpf(dto.cpf())).thenReturn(true);

        // Act e Assert
        CpfDuplicadoException exception = assertThrows(
            CpfDuplicadoException.class, () -> service.criar(dto));
        assertEquals("O CPF 12345678900 já está cadastrado no sistema.", exception.getMessage());
        verify(repository, never()).save(any(Beneficiario.class));
    }

    @Test
    void deveLancarExcecaoAoBuscarBeneficiarioInexistente() {
        // Arrange
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // Act e Assert
        RecursoNaoEncontradoException exception = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.buscarPorId(99L));
        assertEquals("Beneficiário não encontrado com o ID: 99", exception.getMessage());
    }

    @Test
    void deveAtualizarSomenteCamposInformadosNoPatch() {
        // Arrange
        Beneficiario beneficiario = beneficiarioExistente();
        BeneficiarioPatchDTO dto = new BeneficiarioPatchDTO(
                "Nome atualizado", null, null, null);
        when(repository.findById(1L)).thenReturn(Optional.of(beneficiario));
        when(repository.save(beneficiario)).thenReturn(beneficiario);

        // Act
        BeneficiarioResponseDTO resultado = service.atualizarParcial(1L, dto);

        // Assert
        assertEquals("Nome atualizado", resultado.nome());
        assertEquals("11999999999", resultado.telefone());
        verify(repository).save(beneficiario);
    }

    @Test
    void deveDeletarBeneficiarioExistente() {
        // Arrange
        when(repository.existsById(1L)).thenReturn(true);

        // Act
        service.deletar(1L);

        // Assert
        verify(repository).deleteById(1L);
    }

    @Test
    void deveLancarExcecaoAoDeletarBeneficiarioInexistente() {
        // Arrange
        when(repository.existsById(99L)).thenReturn(false);

        // Act e Assert
        RecursoNaoEncontradoException exception = assertThrows(
            RecursoNaoEncontradoException.class, () -> service.deletar(99L));
        assertEquals("Beneficiário não encontrado com o ID: 99", exception.getMessage());
        verify(repository, never()).deleteById(99L);
    }

    private BeneficiarioRequestDTO novoRequest() {
        return new BeneficiarioRequestDTO(
                "Maria da Silva",
                "12345678900",
                "11999999999",
                "Rua das Flores, 123",
                "Renda familiar baixa");
    }

    private Beneficiario beneficiarioExistente() {
        return Beneficiario.builder()
                .idBeneficiario(1L)
                .nome("Maria da Silva")
                .cpf("12345678900")
                .telefone("11999999999")
                .endereco("Rua das Flores, 123")
                .situacaoVulnerabilidade("Renda familiar baixa")
                .dataCadastro(LocalDate.now())
                .build();
    }
}