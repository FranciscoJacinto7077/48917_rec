package upt.gestaodespesas.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import upt.gestaodespesas.dto.DespesaRequest;
import upt.gestaodespesas.entity.Categoria;
import upt.gestaodespesas.entity.Despesa;
import upt.gestaodespesas.entity.Utilizador;
import upt.gestaodespesas.exception.BadRequestException;
import upt.gestaodespesas.exception.NotFoundException;
import upt.gestaodespesas.repository.CategoriaRepository;
import upt.gestaodespesas.repository.DespesaRepository;

@Service
public class DespesaService {

    private final DespesaRepository despesaRepo;
    private final CategoriaRepository categoriaRepo;

    public DespesaService(DespesaRepository despesaRepo, CategoriaRepository categoriaRepo) {
        this.despesaRepo = despesaRepo;
        this.categoriaRepo = categoriaRepo;
    }

    // RF10 + RF11/12/13
    public List<Despesa> listar(
            Utilizador u,
            Long categoriaId,
            LocalDate dataInicio,
            LocalDate dataFim,
            Double valorMin,
            Double valorMax
    ) {
        // validações
        if ((dataInicio == null) != (dataFim == null)) {
            throw new BadRequestException("Indica dataInicio e dataFim (ou nenhuma).");
        }
        if (dataInicio != null && dataFim != null && dataInicio.isAfter(dataFim)) {
            throw new BadRequestException("dataInicio não pode ser posterior a dataFim.");
        }

        if ((valorMin == null) != (valorMax == null)) {
            throw new BadRequestException("Indica valorMin e valorMax (ou nenhum).");
        }
        if (valorMin != null && valorMax != null && valorMin > valorMax) {
            throw new BadRequestException("valorMin não pode ser maior que valorMax.");
        }

        // se pediram categoriaId, garante que pertence ao utilizador
        if (categoriaId != null && !categoriaRepo.findByIdAndUtilizadorId(categoriaId, u.getId()).isPresent()) {
            throw new BadRequestException("categoriaId inválida (não pertence ao utilizador autenticado).");
        }

        Specification<Despesa> spec = (root, query, cb) -> {
            query.orderBy(cb.desc(root.get("data")));

            var p = cb.conjunction();
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

    public Despesa obterPorIdOrThrow(Utilizador u, Long id) {
        return despesaRepo.findByIdAndUtilizadorId(id, u.getId())
                .orElseThrow(() -> new NotFoundException("Despesa não encontrada."));
    }

    public Despesa criar(Utilizador u, DespesaRequest req) {
        Categoria categoria = categoriaRepo.findByIdAndUtilizadorId(req.getCategoriaId(), u.getId())
                .orElseThrow(() -> new BadRequestException("categoriaId inválida (não pertence ao utilizador autenticado)."));

        Despesa despesa = new Despesa();
        despesa.setId(null);
        despesa.setUtilizador(u);
        despesa.setCategoria(categoria);
        despesa.setData(req.getData());
        despesa.setDescricao(req.getDescricao() == null ? null : req.getDescricao().trim());
        despesa.setValor(req.getValor());
        despesa.setMetodoPagamento(req.getMetodoPagamento());

        return despesaRepo.save(despesa);
    }

    public Despesa atualizar(Utilizador u, Long id, DespesaRequest req) {
        Despesa existing = obterPorIdOrThrow(u, id);

        Categoria categoria = categoriaRepo.findByIdAndUtilizadorId(req.getCategoriaId(), u.getId())
                .orElseThrow(() -> new BadRequestException("categoriaId inválida (não pertence ao utilizador autenticado)."));

        existing.setDescricao(req.getDescricao() == null ? null : req.getDescricao().trim());
        existing.setValor(req.getValor());
        existing.setData(req.getData());
        existing.setCategoria(categoria);
        existing.setMetodoPagamento(req.getMetodoPagamento());

        return despesaRepo.save(existing);
    }

    public void apagar(Utilizador u, Long id) {
        Despesa d = obterPorIdOrThrow(u, id);
        despesaRepo.delete(d);
    }
}
