package upt.gestaodespesas.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import upt.gestaodespesas.model.Categoria;
import upt.gestaodespesas.repository.CategoriaRepository;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaRepository repo;

    public CategoriaController(CategoriaRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Categoria> listarCategorias() {
        return repo.findAll();
    }

    @PostMapping
    public ResponseEntity<Categoria> criarCategoria(@Valid @RequestBody Categoria c) {
        c.setId(null);
        Categoria guardada = repo.save(c);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCategoria(@PathVariable Long id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
