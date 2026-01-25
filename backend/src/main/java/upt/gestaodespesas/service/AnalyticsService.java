package upt.gestaodespesas.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import upt.gestaodespesas.dto.analytics.*;
import upt.gestaodespesas.entity.Despesa;
import upt.gestaodespesas.repository.DespesaRepository;

@Service
public class AnalyticsService {

    private final DespesaRepository despesaRepo;

    public AnalyticsService(DespesaRepository despesaRepo) {
        this.despesaRepo = despesaRepo;
    }

    // US14
    public TotalMensalResponse totalMensal(Long userId, int ano, int mes) {
        if (ano < 1900 || ano > 3000) throw new IllegalArgumentException("Ano inválido.");
        if (mes < 1 || mes > 12) throw new IllegalArgumentException("Mês inválido.");

        YearMonth ym = YearMonth.of(ano, mes);
        LocalDate inicio = ym.atDay(1);
        LocalDate fim = ym.atEndOfMonth();

        List<Despesa> despesas = listarPeriodo(userId, inicio, fim);

        double total = despesas.stream().mapToDouble(Despesa::getValor).sum();
        return new TotalMensalResponse(ano, mes, round2(total));
    }

    // US15 (período opcional)
    public List<TotalPorCategoriaItem> totalPorCategoria(Long userId, LocalDate dataInicio, LocalDate dataFim) {
        List<Despesa> despesas;

        if ((dataInicio == null) != (dataFim == null)) {
            throw new IllegalArgumentException("Indica dataInicio e dataFim (ou nenhuma).");
        }
        if (dataInicio != null && dataFim != null && dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("dataInicio não pode ser posterior a dataFim.");
        }

        if (dataInicio == null) {
            despesas = despesaRepo.findAll((root, query, cb) ->
                cb.equal(root.get("utilizador").get("id"), userId)
            );
        } else {
            despesas = listarPeriodo(userId, dataInicio, dataFim);
        }

        Map<Long, Double> totals = despesas.stream()
                .collect(Collectors.groupingBy(d -> d.getCategoria().getId(),
                        Collectors.summingDouble(Despesa::getValor)));

        // precisamos também do nome
        Map<Long, String> nomes = despesas.stream()
                .collect(Collectors.toMap(d -> d.getCategoria().getId(),
                        d -> d.getCategoria().getNome(),
                        (a, b) -> a));

        return totals.entrySet().stream()
                .map(e -> new TotalPorCategoriaItem(e.getKey(), nomes.get(e.getKey()), round2(e.getValue())))
                .sorted((a, b) -> Double.compare(b.getTotal(), a.getTotal()))
                .collect(Collectors.toList());
    }

    // US16
    public DashboardResponse dashboard(Long userId, LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio == null || dataFim == null) {
            throw new IllegalArgumentException("dataInicio e dataFim são obrigatórias.");
        }
        if (dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("dataInicio não pode ser posterior a dataFim.");
        }

        List<Despesa> despesas = listarPeriodo(userId, dataInicio, dataFim);

        double total = despesas.stream().mapToDouble(Despesa::getValor).sum();

        long dias = ChronoUnit.DAYS.between(dataInicio, dataFim) + 1;
        double media = (dias <= 0) ? 0 : (total / dias);

        DashboardResponse.MaiorDespesa maior = null;
        Optional<Despesa> optMaior = despesas.stream().max(Comparator.comparingDouble(Despesa::getValor));
        if (optMaior.isPresent()) {
            Despesa d = optMaior.get();
            maior = new DashboardResponse.MaiorDespesa(d.getId(), d.getDescricao(), round2(d.getValor()), d.getData());
        }

        DashboardResponse.TopCategoria top = null;
        Map<Long, Double> totals = despesas.stream()
                .collect(Collectors.groupingBy(d -> d.getCategoria().getId(),
                        Collectors.summingDouble(Despesa::getValor)));

        if (!totals.isEmpty()) {
            Long topId = totals.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .get().getKey();

            String topNome = despesas.stream()
                    .filter(d -> d.getCategoria().getId().equals(topId))
                    .findFirst().get().getCategoria().getNome();

            top = new DashboardResponse.TopCategoria(topId, topNome, round2(totals.get(topId)));
        }

        return new DashboardResponse(dataInicio, dataFim, round2(total), round2(media), maior, top);
    }

    private List<Despesa> listarPeriodo(Long userId, LocalDate inicio, LocalDate fim) {
        return despesaRepo.findAll((root, query, cb) -> cb.and(
                cb.equal(root.get("utilizador").get("id"), userId),
                cb.between(root.get("data"), inicio, fim)
        ));
    }

    private double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
