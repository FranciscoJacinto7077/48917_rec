package upt.gestaodespesas.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import upt.gestaodespesas.model.Categoria;
import upt.gestaodespesas.repository.CategoriaRepository;
import upt.gestaodespesas.repository.DespesaRepository;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaRepository repo;
    private final DespesaRepository despesaRepo;

    public CategoriaController(CategoriaRepository repo, DespesaRepository despesaRepo) {
        this.repo = repo;
        this.despesaRepo = despesaRepo;
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
    public ResponseEntity<String> deletarCategoria(@PathVariable Long id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Categoria não encontrada");
        }
        if (despesaRepo.existsByCategoriaId(id)) {
        	return ResponseEntity.status(HttpStatus.CONFLICT).body("Não é possível apagar categorias que estão a ser utilizadas em despesas.");	
        }
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
        
    }
    
}
