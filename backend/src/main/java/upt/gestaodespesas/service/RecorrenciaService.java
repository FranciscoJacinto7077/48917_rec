package upt.gestaodespesas.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import upt.gestaodespesas.dto.RecorrenciaRequest;
import upt.gestaodespesas.entity.Categoria;
import upt.gestaodespesas.entity.Despesa;
import upt.gestaodespesas.entity.DespesaRecorrente;
import upt.gestaodespesas.entity.Periodicidade;
import upt.gestaodespesas.entity.Utilizador;
import upt.gestaodespesas.exception.BadRequestException;
import upt.gestaodespesas.exception.NotFoundException;
import upt.gestaodespesas.repository.CategoriaRepository;
import upt.gestaodespesas.repository.DespesaRecorrenteRepository;
import upt.gestaodespesas.repository.DespesaRepository;
import upt.gestaodespesas.repository.UtilizadorRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class RecorrenciaService {

    private final DespesaRecorrenteRepository recorrenciaRepo;
    private final CategoriaRepository categoriaRepo;
    private final DespesaRepository despesaRepo;
    private final UtilizadorRepository utilizadorRepo;

    public RecorrenciaService(DespesaRecorrenteRepository recorrenciaRepo,
                              CategoriaRepository categoriaRepo,
                              DespesaRepository despesaRepo,
                              UtilizadorRepository utilizadorRepo) {
        this.recorrenciaRepo = recorrenciaRepo;
        this.categoriaRepo = categoriaRepo;
        this.despesaRepo = despesaRepo;
        this.utilizadorRepo = utilizadorRepo;
    }

    public List<DespesaRecorrente> listar(Long utilizadorId) {
        return recorrenciaRepo.findByUtilizadorId(utilizadorId);
    }

    @Transactional
    public DespesaRecorrente criar(Long utilizadorId, RecorrenciaRequest req) {

        if (req.getProximaGeracao() != null && req.getProximaGeracao().isBefore(LocalDate.now())) {
            throw new BadRequestException("A proximaGeracao não pode ser no passado.");
        }

        Categoria c = categoriaRepo.findByIdAndUtilizadorId(req.getCategoriaId(), utilizadorId)
                .orElseThrow(() -> new NotFoundException("Categoria não encontrada."));

        Utilizador uRef = utilizadorRepo.getReferenceById(utilizadorId);

        DespesaRecorrente r = new DespesaRecorrente();
        r.setId(null);
        r.setUtilizador(uRef);
        r.setCategoria(c);
        r.setDescricao(req.getDescricao() == null ? null : req.getDescricao().trim());
        r.setValor(req.getValor());
        r.setMetodoPagamento(req.getMetodoPagamento());
        r.setPeriodicidade(req.getPeriodicidade());
        r.setProximaGeracao(req.getProximaGeracao());
        r.setAtiva(req.isAtiva());

        return recorrenciaRepo.save(r);
    }

    @Transactional
    public DespesaRecorrente atualizar(Long id, Long utilizadorId, RecorrenciaRequest req) {

        DespesaRecorrente existente = recorrenciaRepo.findByIdAndUtilizadorId(id, utilizadorId)
                .orElseThrow(() -> new NotFoundException("Recorrência não encontrada."));

        if (req.getProximaGeracao() != null && req.getProximaGeracao().isBefore(LocalDate.now())) {
            throw new BadRequestException("A proximaGeracao não pode ser no passado.");
        }

        Categoria c = categoriaRepo.findByIdAndUtilizadorId(req.getCategoriaId(), utilizadorId)
                .orElseThrow(() -> new NotFoundException("Categoria não encontrada."));

        existente.setDescricao(req.getDescricao() == null ? null : req.getDescricao().trim());
        existente.setValor(req.getValor());
        existente.setMetodoPagamento(req.getMetodoPagamento());
        existente.setPeriodicidade(req.getPeriodicidade());
        existente.setProximaGeracao(req.getProximaGeracao());
        existente.setAtiva(req.isAtiva());
        existente.setCategoria(c);

        return recorrenciaRepo.save(existente);
    }

    @Transactional
    public void apagar(Long id, Long utilizadorId) {
        DespesaRecorrente existente = recorrenciaRepo.findByIdAndUtilizadorId(id, utilizadorId)
                .orElseThrow(() -> new NotFoundException("Recorrência não encontrada."));
        recorrenciaRepo.delete(existente);
    }

    @Transactional
    public void gerarDespesasRecorrentesHoje() {
        LocalDate hoje = LocalDate.now();
        List<DespesaRecorrente> todas = recorrenciaRepo.findAll();

        for (DespesaRecorrente r : todas) {
            if (!r.isAtiva()) continue;
            if (r.getProximaGeracao() == null) continue;

            if (!r.getProximaGeracao().isAfter(hoje)) {

                boolean jaExiste = despesaRepo.findByOrigemRecorrenteAndData(r, hoje).isPresent();
                if (!jaExiste) {
                    Despesa d = new Despesa();
                    d.setData(hoje);
                    d.setDescricao(r.getDescricao());
                    d.setValor(r.getValor());
                    d.setMetodoPagamento(r.getMetodoPagamento());
                    d.setCategoria(r.getCategoria());
                    d.setUtilizador(r.getUtilizador());
                    d.setOrigemRecorrente(r);

                    despesaRepo.save(d);
                }

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
