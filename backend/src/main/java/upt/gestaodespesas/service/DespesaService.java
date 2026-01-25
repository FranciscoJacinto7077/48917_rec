package upt.gestaodespesas.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import upt.gestaodespesas.entity.Categoria;
import upt.gestaodespesas.entity.Despesa;
import upt.gestaodespesas.entity.Utilizador;
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

    /**
     * Lista "as minhas despesas" (US10) com filtros opcionais (US11–US13).
     * Ordena sempre por data desc.
     */
    public List<Despesa> listar(Long utilizadorId,
                               Long categoriaId,
                               LocalDate dataInicio,
                               LocalDate dataFim,
                               Double valorMin,
                               Double valorMax) {

        validarFiltros(utilizadorId, categoriaId, dataInicio, dataFim, valorMin, valorMax);

        Specification<Despesa> spec = specDoUtilizador(utilizadorId);

        if (categoriaId != null) {
            spec = spec.and(specDaCategoria(categoriaId));
        }
        if (dataInicio != null && dataFim != null) {
            spec = spec.and(specDataBetween(dataInicio, dataFim));
        }
        if (valorMin != null || valorMax != null) {
            double min = (valorMin != null) ? valorMin : Double.MIN_VALUE;
            double max = (valorMax != null) ? valorMax : Double.MAX_VALUE;
            spec = spec.and(specValorBetween(min, max));
        }

        return despesaRepo.findAll(spec, Sort.by(Sort.Direction.DESC, "data"));
    }

    /**
     * Obtém uma despesa por id, garantindo que pertence ao utilizador.
     */
    public Despesa obterPorId(Long utilizadorId, Long despesaId) {
        if (utilizadorId == null || despesaId == null) {
            return null;
        }

        Specification<Despesa> spec = specDoUtilizador(utilizadorId).and(specDoId(despesaId));
        return despesaRepo.findOne(spec).orElse(null);
    }

    /**
     * Cria despesa para o utilizador, com validações do enunciado (US07).
     */
    public Despesa criar(Long utilizadorId, Despesa dados) {
        if (utilizadorId == null || dados == null) {
            return null;
        }

        // força criação
        dados.setId(null);

        // validações do enunciado
        validarDespesaBase(dados);

        // categoria tem de existir e ser do utilizador (US12/segurança lógica)
        Categoria categoria = validarEObterCategoriaDoUtilizador(utilizadorId, dados.getCategoria());

        // associar utilizador e categoria “seguras”
        Utilizador u = new Utilizador();
        u.setId(utilizadorId);
        dados.setUtilizador(u);
        dados.setCategoria(categoria);

        return despesaRepo.save(dados);
    }

    /**
     * Atualiza despesa, garantindo que pertence ao utilizador (US08).
     */
    public Despesa atualizar(Long utilizadorId, Long despesaId, Despesa dados) {
        if (utilizadorId == null || despesaId == null || dados == null) {
            return null;
        }

        Despesa existente = obterPorId(utilizadorId, despesaId);
        if (existente == null) {
            return null;
        }

        validarDespesaBase(dados);

        Categoria categoria = validarEObterCategoriaDoUtilizador(utilizadorId, dados.getCategoria());

        existente.setDescricao(dados.getDescricao());
        existente.setValor(dados.getValor());
        existente.setData(dados.getData());
        existente.setMetodoPagamento(dados.getMetodoPagamento());
        existente.setCategoria(categoria);

        return despesaRepo.save(existente);
    }

    /**
     * Apaga despesa do utilizador (US09).
     */
    public boolean apagar(Long utilizadorId, Long despesaId) {
        Despesa existente = obterPorId(utilizadorId, despesaId);
        if (existente == null) {
            return false;
        }
        despesaRepo.delete(existente);
        return true;
    }

    // -------------------------
    // Validações
    // -------------------------

    private void validarFiltros(Long utilizadorId,
                                Long categoriaId,
                                LocalDate dataInicio,
                                LocalDate dataFim,
                                Double valorMin,
                                Double valorMax) {

        if (utilizadorId == null) {
            throw new IllegalArgumentException("Utilizador inválido.");
        }

        if ((dataInicio == null) != (dataFim == null)) {
            throw new IllegalArgumentException("Para filtrar por datas, deve indicar dataInicio e dataFim.");
        }
        if (dataInicio != null && dataFim != null && dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("dataInicio não pode ser posterior a dataFim.");
        }

        if (valorMin != null && valorMin <= 0) {
            throw new IllegalArgumentException("valorMin deve ser superior a 0.");
        }
        if (valorMax != null && valorMax <= 0) {
            throw new IllegalArgumentException("valorMax deve ser superior a 0.");
        }
        if (valorMin != null && valorMax != null && valorMin > valorMax) {
            throw new IllegalArgumentException("valorMin não pode ser superior a valorMax.");
        }

        if (categoriaId != null) {
            // categoria tem de existir e pertencer ao utilizador (US12)
            categoriaRepo.findByIdAndUtilizadorId(categoriaId, utilizadorId)
                    .orElseThrow(() -> new IllegalArgumentException("Categoria inválida ou não pertence ao utilizador."));
        }
    }

    private void validarDespesaBase(Despesa d) {
        if (d.getDescricao() == null || d.getDescricao().trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição é obrigatória.");
        }
        if (d.getValor() == null || d.getValor() <= 0) {
            throw new IllegalArgumentException("Valor deve ser superior a 0.");
        }
        if (d.getData() == null) {
            throw new IllegalArgumentException("Data é obrigatória.");
        }
        if (d.getData().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data não pode ser no futuro.");
        }
        if (d.getMetodoPagamento() == null) {
            throw new IllegalArgumentException("Método de pagamento é obrigatório.");
        }
        if (d.getCategoria() == null || d.getCategoria().getId() == null) {
            throw new IllegalArgumentException("Categoria é obrigatória.");
        }
    }

    private Categoria validarEObterCategoriaDoUtilizador(Long utilizadorId, Categoria categoriaInput) {
        Long catId = categoriaInput.getId();
        return categoriaRepo.findByIdAndUtilizadorId(catId, utilizadorId)
                .orElseThrow(() -> new IllegalArgumentException("Categoria inválida ou não pertence ao utilizador."));
    }

    // -------------------------
    // Specifications
    // -------------------------

    private Specification<Despesa> specDoUtilizador(Long utilizadorId) {
        return (root, query, cb) -> cb.equal(root.get("utilizador").get("id"), utilizadorId);
    }

    private Specification<Despesa> specDaCategoria(Long categoriaId) {
        return (root, query, cb) -> cb.equal(root.get("categoria").get("id"), categoriaId);
    }

    private Specification<Despesa> specDataBetween(LocalDate inicio, LocalDate fim) {
        return (root, query, cb) -> cb.between(root.get("data"), inicio, fim);
    }

    private Specification<Despesa> specValorBetween(double min, double max) {
        return (root, query, cb) -> cb.between(root.get("valor"), min, max);
    }

    private Specification<Despesa> specDoId(Long despesaId) {
        return (root, query, cb) -> cb.equal(root.get("id"), despesaId);
    }
}
