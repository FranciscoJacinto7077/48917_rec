package upt.gestaodespesas.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import upt.gestaodespesas.dto.analytics.*;
import upt.gestaodespesas.entity.Utilizador;
import upt.gestaodespesas.repository.UtilizadorRepository;
import upt.gestaodespesas.service.AnalyticsService;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final UtilizadorRepository utilizadorRepo;

    public AnalyticsController(AnalyticsService analyticsService, UtilizadorRepository utilizadorRepo) {
        this.analyticsService = analyticsService;
        this.utilizadorRepo = utilizadorRepo;
    }

    // US14
    @GetMapping("/total-mensal")
    public ResponseEntity<TotalMensalResponse> totalMensal(@RequestParam int ano,
                                                          @RequestParam int mes,
                                                          Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(analyticsService.totalMensal(userId, ano, mes));
    }

    // US15
    @GetMapping("/total-por-categoria")
    public ResponseEntity<List<TotalPorCategoriaItem>> totalPorCategoria(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            Authentication auth
    ) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(analyticsService.totalPorCategoria(userId, dataInicio, dataFim));
    }

    // US16
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> dashboard(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            Authentication auth
    ) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(analyticsService.dashboard(userId, dataInicio, dataFim));
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
