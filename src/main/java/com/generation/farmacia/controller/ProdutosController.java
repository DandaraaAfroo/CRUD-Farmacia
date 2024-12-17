package com.generation.farmacia.controller;

import com.generation.farmacia.model.Produto;
import com.generation.farmacia.model.Categoria;
import com.generation.farmacia.repository.ProdutoRepository;
import com.generation.farmacia.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutosController {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    // Listar todos os produtos
    @GetMapping
    public List<Produto> listar() {
        return produtoRepository.findAll();
    }

    // Buscar um produto por ID
    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable Long id) {
        return produtoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Adicionar um novo produto
    @PostMapping
    public ResponseEntity<Produto> adicionar(@RequestBody Produto produto) {
        try {
            // Verifica se a categoria existe
            Categoria categoria = categoriaRepository.findById(produto.getCategoria().getId()).orElse(null);
            if (categoria == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Erro 400 se a categoria não for encontrada
            }

            // Verifica se o nome do produto não é vazio
            if (produto.getNome() == null || produto.getNome().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Erro 400 se o nome estiver vazio
            }

            // Associa a categoria ao produto
            produto.setCategoria(categoria);

            // Salva o produto e retorna com status 201 (Created)
            Produto produtoSalvo = produtoRepository.save(produto);
            return ResponseEntity.status(HttpStatus.CREATED).body(produtoSalvo); // Retorna o produto criado

        } catch (Exception e) {
            // Em caso de exceção inesperada, retorna erro 500 (Internal Server Error)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Atualizar um produto existente
    @PutMapping("/{id}")
    public ResponseEntity<Produto> atualizar(@PathVariable Long id, @RequestBody Produto produto) {
        if (!produtoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        // Verifica se a categoria existe
        if (!categoriaRepository.existsById(produto.getCategoria().getId())) {
            return ResponseEntity.badRequest().build(); // Erro 400 se a categoria não existir
        }

        produto.setId(id);
        return ResponseEntity.ok(produtoRepository.save(produto));
    }

    // Remover um produto
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        if (!produtoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        produtoRepository.deleteById(id);
        return ResponseEntity.noContent().build(); // Retorna status 204 (No Content) para sucesso na exclusão
    }
}
