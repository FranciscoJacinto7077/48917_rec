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
        // validações datas (aceita só início, só fim, ou ambos)
        if (dataInicio != null && dataFim != null && dataInicio.isAfter(dataFim)) {
            throw new BadRequestException("dataInicio não pode ser posterior a dataFim.");
        }

        // validações valores (aceita só min, só max, ou ambos)
        if (valorMin != null && valorMax != null && valorMin > valorMax) {
            throw new BadRequestException("valorMin não pode ser maior que valorMax.");
        }

        // se pediram categoriaId, garante que pertence ao utilizador
        if (categoriaId != null && categoriaRepo.findByIdAndUtilizadorId(categoriaId, u.getId()).isEmpty()) {
            throw new BadRequestException("categoriaId inválida (não pertence ao utilizador autenticado).");
        }

        Specification<Despesa> spec = (root, query, cb) -> {
            query.orderBy(cb.desc(root.get("data")));

            var p = cb.conjunction();
            p.getExpressions().add(cb.equal(root.get("utilizador").get("id"), u.getId()));

            // filtro por categoria
            if (categoriaId != null) {
                p.getExpressions().add(cb.equal(root.get("categoria").get("id"), categoriaId));
            }

            // filtro por datas
            if (dataInicio != null && dataFim != null) {
                p.getExpressions().add(cb.between(root.get("data"), dataInicio, dataFim));
            } else if (dataInicio != null) {
                p.getExpressions().add(cb.greaterThanOrEqualTo(root.get("data"), dataInicio));
            } else if (dataFim != null) {
                p.getExpressions().add(cb.lessThanOrEqualTo(root.get("data"), dataFim));
            }

            // filtro por valores
            if (valorMin != null && valorMax != null) {
                p.getExpressions().add(cb.between(root.get("valor"), valorMin, valorMax));
            } else if (valorMin != null) {
                p.getExpressions().add(cb.greaterThanOrEqualTo(root.get("valor"), valorMin));
            } else if (valorMax != null) {
                p.getExpressions().add(cb.lessThanOrEqualTo(root.get("valor"), valorMax));
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
        if (req.getData() != null && req.getData().isAfter(LocalDate.now())) {
            throw new BadRequestException("A data não pode ser futura.");
        }

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
        if (req.getData() != null && req.getData().isAfter(LocalDate.now())) {
            throw new BadRequestException("A data não pode ser futura.");
        }

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
