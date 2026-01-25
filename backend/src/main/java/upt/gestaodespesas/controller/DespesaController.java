package upt.gestaodespesas.controller;

import java.time.LocalDate;
import java.util.List;

import javax.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import upt.gestaodespesas.entity.Despesa;
import upt.gestaodespesas.entity.Utilizador;
import upt.gestaodespesas.repository.UtilizadorRepository;
import upt.gestaodespesas.service.DespesaService;

@RestController
@RequestMapping("/api/despesas")
public class DespesaController {

    private final DespesaService despesaService;
    private final UtilizadorRepository utilizadorRepo;

    public DespesaController(DespesaService despesaService, UtilizadorRepository utilizadorRepo) {
        this.despesaService = despesaService;
        this.utilizadorRepo = utilizadorRepo;
    }

    /**
     * US10 (minhas despesas, ordenadas por data desc)
     * US11–US13 (filtros por datas, categoria, valores)
     *
     * Exemplos:
     *  - /api/despesas
     *  - /api/despesas?categoriaId=2
     *  - /api/despesas?dataInicio=2026-01-01&dataFim=2026-01-31
     *  - /api/despesas?min=10&max=50
     *  - combinações possíveis
     */
    @GetMapping
    public ResponseEntity<List<Despesa>> listar(
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) Double min,
            @RequestParam(required = false) Double max,
            Authentication auth
    ) {
        Long userId = getUserId(auth);

        List<Despesa> despesas = despesaService.listar(
                userId,
                categoriaId,
                dataInicio,
                dataFim,
                min,
                max
        );

        return ResponseEntity.ok(despesas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Despesa> obterPorId(@PathVariable Long id, Authentication auth) {
        Long userId = getUserId(auth);
        Despesa d = despesaService.obterPorId(userId, id);
        if (d == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(d);
    }

    @PostMapping
    public ResponseEntity<Despesa> criar(@Valid @RequestBody Despesa despesa, Authentication auth) {
        Long userId = getUserId(auth);
        Despesa criada = despesaService.criar(userId, despesa);
        if (criada == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Despesa> atualizar(@PathVariable Long id,
                                            @Valid @RequestBody Despesa dados,
                                            Authentication auth) {
        Long userId = getUserId(auth);
        Despesa atualizada = despesaService.atualizar(userId, id, dados);
        if (atualizada == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(atualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagar(@PathVariable Long id, Authentication auth) {
        Long userId = getUserId(auth);
        boolean ok = despesaService.apagar(userId, id);
        if (!ok) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }

    // -------------------------
    // Helpers
    // -------------------------

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
