package upt.gestaodespesas.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import upt.gestaodespesas.entity.Despesa;
import upt.gestaodespesas.entity.Utilizador;
import upt.gestaodespesas.repository.DespesaRepository;

@Service
public class DespesaService {

    private final DespesaRepository despesaRepo;

    public DespesaService(DespesaRepository despesaRepo) {
        this.despesaRepo = despesaRepo;
    }

    // RF10 + RF11/12/13: listagem do utilizador com filtros
    public List<Despesa> listar(Utilizador u,
                                Long categoriaId,
                                LocalDate dataInicio,
                                LocalDate dataFim,
                                Double valorMin,
                                Double valorMax) {

        // validações básicas
        if ((dataInicio == null) != (dataFim == null)) {
            throw new RuntimeException("Indica dataInicio e dataFim (ou nenhuma).");
        }
        if (dataInicio != null && dataFim != null && dataInicio.isAfter(dataFim)) {
            throw new RuntimeException("dataInicio não pode ser posterior a dataFim.");
        }
        if ((valorMin == null) != (valorMax == null)) {
            throw new RuntimeException("Indica valorMin e valorMax (ou nenhum).");
        }
        if (valorMin != null && valorMax != null && valorMin > valorMax) {
            throw new RuntimeException("valorMin não pode ser maior que valorMax.");
        }

        Specification<Despesa> spec = (root, query, cb) -> {
            // ordenação por data desc
            query.orderBy(cb.desc(root.get("data")));

            var p = cb.conjunction();

            // sempre por utilizador
            p.getExpressions().add(cb.equal(root.get("utilizador").get("id"), u.getId()));

            if (categoriaId != null) {
                p.getExpressions().add(cb.equal(root.get("categoria").get("id"), categoriaId));
            }
            if (dataInicio != null && dataFim != null) {
                p.getExpressions().add(cb.between(root.get("data"), dataInicio, dataFim));
            }
            if (valorMin != null && valorMax != null) {
                p.getExpressions().add(cb.between(root.get("valor"), valorMin, valorMax));
            }
            return p;
        };

        return despesaRepo.findAll(spec);
    }

    public Despesa obterPorId(Utilizador u, Long id) {
        return despesaRepo.findByIdAndUtilizadorId(id, u.getId()).orElse(null);
    }

    public Despesa criar(Utilizador u, Despesa despesa) {
        despesa.setId(null);
        despesa.setUtilizador(u);
        return despesaRepo.save(despesa);
    }

    public Despesa atualizar(Utilizador u, Long id, Despesa dados) {
        return despesaRepo.findByIdAndUtilizadorId(id, u.getId()).map(existing -> {
            existing.setDescricao(dados.getDescricao());
            existing.setValor(dados.getValor());
            existing.setData(dados.getData());
            existing.setCategoria(dados.getCategoria());
            existing.setMetodoPagamento(dados.getMetodoPagamento());
            return despesaRepo.save(existing);
        }).orElse(null);
    }

    public boolean apagar(Utilizador u, Long id) {
        return despesaRepo.findByIdAndUtilizadorId(id, u.getId()).map(d -> {
            despesaRepo.delete(d);
            return true;
        }).orElse(false);
    }
}
