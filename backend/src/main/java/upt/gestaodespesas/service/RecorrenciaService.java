package upt.gestaodespesas.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import upt.gestaodespesas.entity.Categoria;
import upt.gestaodespesas.entity.Despesa;
import upt.gestaodespesas.entity.DespesaRecorrente;
import upt.gestaodespesas.entity.Periodicidade;
import upt.gestaodespesas.exception.NotFoundException;
import upt.gestaodespesas.repository.CategoriaRepository;
import upt.gestaodespesas.repository.DespesaRecorrenteRepository;
import upt.gestaodespesas.repository.DespesaRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class RecorrenciaService {

    private final DespesaRecorrenteRepository recorrenciaRepo;
    private final CategoriaRepository categoriaRepo;
    private final DespesaRepository despesaRepo;

    public RecorrenciaService(DespesaRecorrenteRepository recorrenciaRepo,
                              CategoriaRepository categoriaRepo,
                              DespesaRepository despesaRepo) {
        this.recorrenciaRepo = recorrenciaRepo;
        this.categoriaRepo = categoriaRepo;
        this.despesaRepo = despesaRepo;
    }

    public List<DespesaRecorrente> listar(Long utilizadorId) {
        return recorrenciaRepo.findByUtilizadorId(utilizadorId);
    }

    @Transactional
    public DespesaRecorrente criar(Long utilizadorId, DespesaRecorrente r) {
        r.setId(null);
        r.getUtilizador().setId(utilizadorId);

        Categoria c = categoriaRepo.findByIdAndUtilizadorId(r.getCategoria().getId(), utilizadorId)
                .orElseThrow(() -> new NotFoundException("Categoria não encontrada."));
        r.setCategoria(c);

        return recorrenciaRepo.save(r);
    }

    @Transactional
    public DespesaRecorrente atualizar(Long id, Long utilizadorId, DespesaRecorrente r) {
        DespesaRecorrente existente = recorrenciaRepo.findByIdAndUtilizadorId(id, utilizadorId)
                .orElseThrow(() -> new NotFoundException("Recorrência não encontrada."));

        Categoria c = categoriaRepo.findByIdAndUtilizadorId(r.getCategoria().getId(), utilizadorId)
                .orElseThrow(() -> new NotFoundException("Categoria não encontrada."));

        existente.setDescricao(r.getDescricao());
        existente.setValor(r.getValor());
        existente.setMetodoPagamento(r.getMetodoPagamento());
        existente.setPeriodicidade(r.getPeriodicidade());
        existente.setProximaGeracao(r.getProximaGeracao());
        existente.setAtiva(r.isAtiva());
        existente.setCategoria(c);

        return recorrenciaRepo.save(existente);
    }

    @Transactional
    public void apagar(Long id, Long utilizadorId) {
        DespesaRecorrente existente = recorrenciaRepo.findByIdAndUtilizadorId(id, utilizadorId)
                .orElseThrow(() -> new NotFoundException("Recorrência não encontrada."));
        recorrenciaRepo.delete(existente);
    }

    /**
     * Corre diariamente (scheduler) e:
     * - gera uma Despesa para cada DespesaRecorrente ativa cuja proximaGeracao == hoje (ou <= hoje)
     * - avança a proximaGeracao com base na periodicidade
     */
    @Transactional
    public void gerarDespesasRecorrentesHoje() {
        LocalDate hoje = LocalDate.now();

        // Se quiseres ser mais eficiente, crias query específica.
        // Para já: simples e correto.
        List<DespesaRecorrente> todas = recorrenciaRepo.findAll();

        for (DespesaRecorrente r : todas) {
            if (!r.isAtiva()) continue;
            if (r.getProximaGeracao() == null) continue;

            if (!r.getProximaGeracao().isAfter(hoje)) {

                // Evitar duplicados no mesmo dia (caso scheduler corra mais que uma vez)
                boolean jaExiste = despesaRepo.findByOrigemRecorrenteAndData(r, hoje).isPresent();
                if (!jaExiste) {
                    Despesa d = new Despesa();
                    d.setData(hoje);
                    d.setDescricao(r.getDescricao());
                    d.setValor(r.getValor());
                    d.setMetodoPagamento(r.getMetodoPagamento());
                    d.setCategoria(r.getCategoria());
                    d.setUtilizador(r.getUtilizador());

                    // se a tua entidade Despesa tiver este campo:
                    d.setOrigemRecorrente(r);

                    despesaRepo.save(d);
                }

                // avançar próxima geração
                r.setProximaGeracao(calcularProximaData(r.getProximaGeracao(), r.getPeriodicidade()));
                recorrenciaRepo.save(r);
            }
        }
    }

    private LocalDate calcularProximaData(LocalDate base, Periodicidade periodicidade) {
        if (periodicidade == null) return base.plusMonths(1);

        switch (periodicidade) {
            case DIARIA:
                return base.plusDays(1);
            case SEMANAL:
                return base.plusWeeks(1);
            case MENSAL:
                return base.plusMonths(1);
            case ANUAL:
                return base.plusYears(1);
            default:
                return base.plusMonths(1);
        }
    }
}
