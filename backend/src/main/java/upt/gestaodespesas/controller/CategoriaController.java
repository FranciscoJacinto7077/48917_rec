package upt.gestaodespesas.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import upt.gestaodespesas.entity.Categoria;
import upt.gestaodespesas.entity.Utilizador;
import upt.gestaodespesas.repository.UtilizadorRepository;
import upt.gestaodespesas.service.CategoriaService;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;
    private final UtilizadorRepository utilizadorRepo;

    public CategoriaController(CategoriaService categoriaService, UtilizadorRepository utilizadorRepo) {
        this.categoriaService = categoriaService;
        this.utilizadorRepo = utilizadorRepo;
    }

    @GetMapping
    public ResponseEntity<List<Categoria>> listarCategorias(Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(categoriaService.listarCategorias(userId));
    }

    public static class CategoriaNomeRequest {
        @NotBlank
        private String nome;

        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
    }

    @PostMapping
    public ResponseEntity<Categoria> criarCategoria(@Valid @RequestBody CategoriaNomeRequest req, Authentication auth) {
        Long userId = getUserId(auth);
        Categoria criada = categoriaService.criarCategoria(userId, req.getNome());
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    // US05
    @PutMapping("/{id}")
    public ResponseEntity<Categoria> editarCategoria(@PathVariable Long id,
                                                    @Valid @RequestBody CategoriaNomeRequest req,
                                                    Authentication auth) {
        Long userId = getUserId(auth);
        Categoria atualizada = categoriaService.editarCategoria(userId, id, req.getNome());
        return ResponseEntity.ok(atualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagarCategoria(@PathVariable Long id, Authentication auth) {
        Long userId = getUserId(auth);
        categoriaService.apagarCategoria(userId, id);
        return ResponseEntity.noContent().build();
    }

    private Long getUserId(Authentication auth) {
        if (auth == null || auth.getName() == null) {
            throw new IllegalArgumentException("Utilizador não autenticado.");
        }
        String email = auth.getName();
        Utilizador u = utilizadorRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado."));
        return u.getId();
    }
}
