package upt.gestaodespesas.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import upt.gestaodespesas.dto.DtoMapper;
import upt.gestaodespesas.dto.RecorrenciaRequest;
import upt.gestaodespesas.dto.RecorrenciaResponse;
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
    public ResponseEntity<List<RecorrenciaResponse>> listar(Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(
                recorrenciaService.listar(userId)
                        .stream()
                        .map(DtoMapper::toRecorrenciaResponse)
                        .collect(Collectors.toList())
        );
    }

    @PostMapping
    public ResponseEntity<RecorrenciaResponse> criar(@Valid @RequestBody RecorrenciaRequest req, Authentication auth) {
        Long userId = getUserId(auth);
        var criada = recorrenciaService.criar(userId, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(DtoMapper.toRecorrenciaResponse(criada));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecorrenciaResponse> atualizar(@PathVariable Long id,
                                                        @Valid @RequestBody RecorrenciaRequest req,
                                                        Authentication auth) {
        Long userId = getUserId(auth);
        // ATENÇÃO: ordem correta -> (id, userId, req)
        var atualizada = recorrenciaService.atualizar(id, userId, req);
        return ResponseEntity.ok(DtoMapper.toRecorrenciaResponse(atualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagar(@PathVariable Long id, Authentication auth) {
        Long userId = getUserId(auth);
        // ATENÇÃO: ordem correta -> (id, userId)
        recorrenciaService.apagar(id, userId);
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
