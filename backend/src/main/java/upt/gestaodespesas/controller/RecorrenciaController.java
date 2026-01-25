package upt.gestaodespesas.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import upt.gestaodespesas.entity.DespesaRecorrente;
import upt.gestaodespesas.entity.Utilizador;
import upt.gestaodespesas.repository.UtilizadorRepository;
import upt.gestaodespesas.service.RecorrenciaService;

@RestController
@RequestMapping("/api/recorrencias")
public class RecorrenciaController {

    private final RecorrenciaService recorrenciaService;
    private final UtilizadorRepository utilizadorRepo;

    public RecorrenciaController(RecorrenciaService recorrenciaService, UtilizadorRepository utilizadorRepo) {
        this.recorrenciaService = recorrenciaService;
        this.utilizadorRepo = utilizadorRepo;
    }

    @GetMapping
    public ResponseEntity<List<DespesaRecorrente>> listar(Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(recorrenciaService.listar(userId));
    }

    @PostMapping
    public ResponseEntity<DespesaRecorrente> criar(@Valid @RequestBody DespesaRecorrente r, Authentication auth) {
        Long userId = getUserId(auth);
        DespesaRecorrente criada = recorrenciaService.criar(userId, r);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DespesaRecorrente> atualizar(@PathVariable Long id,
                                                       @Valid @RequestBody DespesaRecorrente dados,
                                                       Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(recorrenciaService.atualizar(userId, id, dados));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagar(@PathVariable Long id, Authentication auth) {
        Long userId = getUserId(auth);
        recorrenciaService.apagar(userId, id);
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
