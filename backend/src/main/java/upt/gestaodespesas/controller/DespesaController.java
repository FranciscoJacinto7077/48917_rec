package upt.gestaodespesas.controller;

import java.time.LocalDate;
import java.util.List;

import javax.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import upt.gestaodespesas.entity.Despesa;
import upt.gestaodespesas.entity.Utilizador;
import upt.gestaodespesas.service.DespesaService;
import upt.gestaodespesas.service.UtilizadorService;

@RestController
@RequestMapping("/api/despesas")
public class DespesaController {

    private final DespesaService despesaService;
    private final UtilizadorService utilizadorService;

    public DespesaController(DespesaService despesaService, UtilizadorService utilizadorService) {
        this.despesaService = despesaService;
        this.utilizadorService = utilizadorService;
    }

    @GetMapping
    public List<Despesa> listar(
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) Double valorMin,
            @RequestParam(required = false) Double valorMax
    ) {
        Utilizador u = utilizadorService.getAuthenticatedUser();
        return despesaService.listar(u, categoriaId, dataInicio, dataFim, valorMin, valorMax);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Despesa> obterPorId(@PathVariable Long id) {
        Utilizador u = utilizadorService.getAuthenticatedUser();
        Despesa d = despesaService.obterPorId(u, id);
        if (d == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(d);
    }

    @PostMapping
    public ResponseEntity<Despesa> criar(@Valid @RequestBody Despesa despesa) {
        Utilizador u = utilizadorService.getAuthenticatedUser();
        Despesa guardada = despesaService.criar(u, despesa);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Despesa> atualizar(@PathVariable Long id, @Valid @RequestBody Despesa dados) {
        Utilizador u = utilizadorService.getAuthenticatedUser();
        Despesa d = despesaService.atualizar(u, id, dados);
        if (d == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(d);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagar(@PathVariable Long id) {
        Utilizador u = utilizadorService.getAuthenticatedUser();
        boolean ok = despesaService.apagar(u, id);
        if (!ok) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }
}
