package br.com.socialconnect.api.produtos.service;

import br.com.socialconnect.api.produtos.dto.ProdutoRequestDTO;
import br.com.socialconnect.api.produtos.dto.ProdutoResponseDTO;
import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import br.com.socialconnect.api.produtos.model.Produto;
import br.com.socialconnect.api.produtos.repository.ProdutoRepository;
import br.com.socialconnect.api.exception.EstoqueNegativoException;
import br.com.socialconnect.api.exception.NomeProdutoDuplicadoException;
import br.com.socialconnect.api.exception.RecursoNaoEncontradoException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ProdutoService {
    private final ProdutoRepository repository;

    public ProdutoService(ProdutoRepository repository) { this.repository = repository; }

    public Page<ProdutoResponseDTO> listar(String nome, CategoriaProduto categoria, Pageable pageable) {
        boolean temNome = nome != null && !nome.isBlank();
        Page<Produto> produtos;
        if (temNome && categoria != null) {
            produtos = repository.findByNomeContainingIgnoreCaseAndCategoria(nome.trim(), categoria, pageable);
        } else if (temNome) {
            produtos = repository.findByNomeContainingIgnoreCase(nome.trim(), pageable);
        } else if (categoria != null) {
            produtos = repository.findByCategoria(categoria, pageable);
        } else {
            produtos = repository.findAll(pageable);
        }
        return produtos.map(this::toResponse);
    }

    public ProdutoResponseDTO buscarPorId(Long id) { return toResponse(encontrar(id)); }

    public ProdutoResponseDTO criar(ProdutoRequestDTO dto) {
        validarEstoque(dto);
        if (repository.existsByNomeIgnoreCase(dto.nome().trim())) throw new NomeProdutoDuplicadoException(dto.nome());
        Produto produto = Produto.builder().nome(dto.nome().trim()).categoria(dto.categoria())
                .estoqueAtual(dto.estoqueAtual()).estoqueMinimo(dto.estoqueMinimo())
                .unidadeMedida(dto.unidadeMedida().trim()).dataCadastro(LocalDate.now()).build();
        return toResponse(repository.save(produto));
    }

    public ProdutoResponseDTO atualizar(Long id, ProdutoRequestDTO dto) {
        validarEstoque(dto);
        Produto produto = encontrar(id);
        if (repository.existsByNomeIgnoreCaseAndIdProdutoNot(dto.nome().trim(), id)) {
            throw new NomeProdutoDuplicadoException(dto.nome());
        }
        produto.setNome(dto.nome().trim());
        produto.setCategoria(dto.categoria());
        produto.setEstoqueAtual(dto.estoqueAtual());
        produto.setEstoqueMinimo(dto.estoqueMinimo());
        produto.setUnidadeMedida(dto.unidadeMedida().trim());
        return toResponse(repository.save(produto));
    }

    public void deletar(Long id) { repository.delete(encontrar(id)); }

    private Produto encontrar(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new RecursoNaoEncontradoException("Produto não encontrado com o ID: " + id));
    }

    private void validarEstoque(ProdutoRequestDTO dto) {
        if (dto.estoqueAtual() != null && dto.estoqueAtual() < 0
                || dto.estoqueMinimo() != null && dto.estoqueMinimo() < 0) {
            throw new EstoqueNegativoException("Os valores de estoque não podem ser negativos.");
        }
    }

    private ProdutoResponseDTO toResponse(Produto produto) {
        return new ProdutoResponseDTO(produto.getIdProduto(), produto.getNome(), produto.getCategoria(),
                produto.getEstoqueAtual(), produto.getEstoqueMinimo(), produto.getUnidadeMedida(),
                produto.getDataCadastro(), produto.getEstoqueAtual() < produto.getEstoqueMinimo());
    }
}
