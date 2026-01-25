package upt.gestaodespesas.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import upt.gestaodespesas.entity.Categoria;
import upt.gestaodespesas.entity.Utilizador;
import upt.gestaodespesas.service.CategoriaService;
import upt.gestaodespesas.service.UtilizadorService;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;
    private final UtilizadorService utilizadorService;

    public CategoriaController(CategoriaService categoriaService, UtilizadorService utilizadorService) {
        this.categoriaService = categoriaService;
        this.utilizadorService = utilizadorService;
    }

    @GetMapping
    public List<Categoria> listarCategorias() {
        Utilizador u = utilizadorService.getAuthenticatedUser();
        return categoriaService.listarCategorias(u);
    }

    @PostMapping
    public ResponseEntity<Categoria> criarCategoria(@Valid @RequestBody Categoria c) {
        Utilizador u = utilizadorService.getAuthenticatedUser();
        Categoria guardada = categoriaService.criarCategoria(u, c);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }

    // RF5: editar
    @PutMapping("/{id}")
    public ResponseEntity<Categoria> atualizar(@PathVariable Long id, @Valid @RequestBody Categoria dados) {
        Utilizador u = utilizadorService.getAuthenticatedUser();
        Categoria atualizada = categoriaService.atualizarCategoria(u, id, dados);
        return ResponseEntity.ok(atualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagarCategoria(@PathVariable Long id) {
        Utilizador u = utilizadorService.getAuthenticatedUser();
        categoriaService.apagarCategoria(u, id);
        return ResponseEntity.noContent().build();
    }
}
