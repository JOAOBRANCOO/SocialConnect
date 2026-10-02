package br.com.socialconnect.api.produtos.service;

import br.com.socialconnect.api.exception.EstoqueNegativoException;
import br.com.socialconnect.api.exception.NomeProdutoDuplicadoException;
import br.com.socialconnect.api.produtos.dto.ProdutoRequestDTO;
import br.com.socialconnect.api.produtos.dto.ProdutoResponseDTO;
import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import br.com.socialconnect.api.produtos.model.Produto;
import br.com.socialconnect.api.produtos.repository.ProdutoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {
    @Mock private ProdutoRepository repository;
    @InjectMocks private ProdutoService service;

    @Test
    void deveCriarProdutoQuandoDadosValidos() {
        // Arrange
        ProdutoRequestDTO dto = request(4, 10);
        when(repository.existsByNomeIgnoreCase("Arroz 5kg")).thenReturn(false);
        when(repository.save(any(Produto.class))).thenAnswer(invocation -> {
            Produto produto = invocation.getArgument(0);
            produto.setIdProduto(1L);
            return produto;
        });

        // Act
        ProdutoResponseDTO resultado = service.criar(dto);

        // Assert
        assertEquals(1L, resultado.idProduto());
        assertTrue(resultado.estoqueBaixo());
        assertEquals(LocalDate.now(), resultado.dataCadastro());
        verify(repository).save(any(Produto.class));
    }

    @Test
    void deveLancarExcecaoQuandoEstoqueNegativo() {
        // Arrange
        ProdutoRequestDTO dto = request(-1, 10);

        // Act e Assert
        assertThrows(EstoqueNegativoException.class, () -> service.criar(dto));
        verifyNoInteractions(repository);
    }

    @Test
    void deveLancarExcecaoQuandoNomeDuplicado() {
        // Arrange
        ProdutoRequestDTO dto = request(4, 10);
        when(repository.existsByNomeIgnoreCase("Arroz 5kg")).thenReturn(true);

        // Act e Assert
        assertThrows(NomeProdutoDuplicadoException.class, () -> service.criar(dto));
        verify(repository, never()).save(any(Produto.class));
    }

    private ProdutoRequestDTO request(int atual, int minimo) {
        return new ProdutoRequestDTO("Arroz 5kg", CategoriaProduto.ALIMENTO, atual, minimo, "unidade");
    }
}
