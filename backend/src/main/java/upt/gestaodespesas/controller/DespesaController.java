package upt.gestaodespesas.controller;

import java.time.LocalDate;
import java.util.List;

import javax.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

import upt.gestaodespesas.dto.DespesaRequest;
import upt.gestaodespesas.dto.DespesaResponse;
import upt.gestaodespesas.dto.DtoMapper;
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
    public List<DespesaResponse> listar(
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) Double valorMin,
            @RequestParam(required = false) Double valorMax
    ) {
        Utilizador u = utilizadorService.getAuthenticatedUser();
        return despesaService.listar(u, categoriaId, dataInicio, dataFim, valorMin, valorMax)
                .stream().map(DtoMapper::toDespesaResponse).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DespesaResponse> obterPorId(@PathVariable Long id) {
        Utilizador u = utilizadorService.getAuthenticatedUser();
        var d = despesaService.obterPorIdOrThrow(u, id);
        return ResponseEntity.ok(DtoMapper.toDespesaResponse(d));
    }

    @PostMapping
    public ResponseEntity<DespesaResponse> criar(@Valid @RequestBody DespesaRequest despesa) {
        Utilizador u = utilizadorService.getAuthenticatedUser();
        var guardada = despesaService.criar(u, despesa);
        return ResponseEntity.status(HttpStatus.CREATED).body(DtoMapper.toDespesaResponse(guardada));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DespesaResponse> atualizar(@PathVariable Long id, @Valid @RequestBody DespesaRequest dados) {
        Utilizador u = utilizadorService.getAuthenticatedUser();
        var d = despesaService.atualizar(u, id, dados);
        return ResponseEntity.ok(DtoMapper.toDespesaResponse(d));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagar(@PathVariable Long id) {
        Utilizador u = utilizadorService.getAuthenticatedUser();
        despesaService.apagar(u, id);
        return ResponseEntity.noContent().build();
    }
}
