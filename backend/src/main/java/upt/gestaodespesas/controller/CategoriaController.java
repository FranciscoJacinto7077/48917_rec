package upt.gestaodespesas.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

import upt.gestaodespesas.dto.CategoriaRequest;
import upt.gestaodespesas.dto.CategoriaResponse;
import upt.gestaodespesas.dto.DtoMapper;
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
    public List<CategoriaResponse> listarCategorias() {
        Utilizador u = utilizadorService.getAuthenticatedUser();
        return categoriaService.listarCategorias(u).stream()
                .map(DtoMapper::toCategoriaResponse)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<CategoriaResponse> criarCategoria(@Valid @RequestBody CategoriaRequest c) {
        Utilizador u = utilizadorService.getAuthenticatedUser();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(DtoMapper.toCategoriaResponse(categoriaService.criarCategoria(u, c)));
    }

    // RF5
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponse> atualizar(@PathVariable Long id, @Valid @RequestBody CategoriaRequest dados) {
        Utilizador u = utilizadorService.getAuthenticatedUser();
        return ResponseEntity.ok(DtoMapper.toCategoriaResponse(categoriaService.atualizarCategoria(u, id, dados)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagarCategoria(@PathVariable Long id) {
        Utilizador u = utilizadorService.getAuthenticatedUser();
        categoriaService.apagarCategoria(u, id);
        return ResponseEntity.noContent().build();
    }
}
