package upt.gestaodespesas.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import upt.gestaodespesas.entity.*;
import upt.gestaodespesas.repository.*;

@Service
public class RecorrenciaService {

    private final DespesaRecorrenteRepository recorrenteRepo;
    private final CategoriaRepository categoriaRepo;
    private final DespesaRepository despesaRepo;

    public RecorrenciaService(DespesaRecorrenteRepository recorrenteRepo,
                              CategoriaRepository categoriaRepo,
                              DespesaRepository despesaRepo) {
        this.recorrenteRepo = recorrenteRepo;
        this.categoriaRepo = categoriaRepo;
        this.despesaRepo = despesaRepo;
    }

    public List<DespesaRecorrente> listar(Long userId) {
        return recorrenteRepo.findByUtilizadorId(userId);
    }

    public DespesaRecorrente criar(Long userId, DespesaRecorrente r) {
        if (r == null) throw new IllegalArgumentException("Dados inválidos.");

        validarBase(r);

        Categoria cat = categoriaRepo.findByIdAndUtilizadorId(r.getCategoria().getId(), userId)
                .orElseThrow(() -> new IllegalArgumentException("Categoria inválida ou não pertence ao utilizador."));

        Utilizador u = new Utilizador();
        u.setId(userId);

        r.setId(null);
        r.setUtilizador(u);
        r.setCategoria(cat);

        // se não vier definida, começa hoje
        if (r.getProximaGeracao() == null) {
            r.setProximaGeracao(LocalDate.now());
        }

        return recorrenteRepo.save(r);
    }

    public DespesaRecorrente atualizar(Long userId, Long id, DespesaRecorrente dados) {
        if (dados == null) throw new IllegalArgumentException("Dados inválidos.");

        DespesaRecorrente existente = recorrenteRepo.findByIdAndUtilizadorId(id, userId)
                .orElseThrow(() -> new IllegalArgumentException("Recorrência não encontrada."));

        validarBase(dados);

        Categoria cat = categoriaRepo.findByIdAndUtilizadorId(dados.getCategoria().getId(), userId)
                .orElseThrow(() -> new IllegalArgumentException("Categoria inválida ou não pertence ao utilizador."));

        existente.setDescricao(dados.getDescricao().trim());
        existente.setValor(dados.getValor());
        existente.setMetodoPagamento(dados.getMetodoPagamento());
        existente.setPeriodicidade(dados.getPeriodicidade());
        existente.setAtiva(dados.isAtiva());
        existente.setCategoria(cat);

        if (dados.getProximaGeracao() != null) {
            existente.setProximaGeracao(dados.getProximaGeracao());
        }

        return recorrenteRepo.save(existente);
    }

    public void apagar(Long userId, Long id) {
        DespesaRecorrente existente = recorrenteRepo.findByIdAndUtilizadorId(id, userId)
                .orElseThrow(() -> new IllegalArgumentException("Recorrência não encontrada."));
        recorrenteRepo.delete(existente);
    }

    // Job: gerar ocorrências (anti-duplicados)
    public int gerarOcorrenciasAteHoje() {
        LocalDate hoje = LocalDate.now();
        List<DespesaRecorrente> devidas = recorrenteRepo.findByAtivaTrueAndProximaGeracaoLessThanEqual(hoje);

        int criadas = 0;

        for (DespesaRecorrente r : devidas) {
            LocalDate data = r.getProximaGeracao();

            boolean jaExiste = despesaRepo.existsByUtilizadorIdAndDataAndDescricaoAndValorAndCategoriaIdAndMetodoPagamento(
                    r.getUtilizador().getId(),
                    data,
                    r.getDescricao(),
                    r.getValor(),
                    r.getCategoria().getId(),
                    r.getMetodoPagamento()
            );

            if (!jaExiste) {
                Despesa d = new Despesa();
                d.setDescricao(r.getDescricao());
                d.setValor(r.getValor());
                d.setData(data);
                d.setMetodoPagamento(r.getMetodoPagamento());
                d.setCategoria(r.getCategoria());
                d.setUtilizador(r.getUtilizador());
                despesaRepo.save(d);
                criadas++;
            }

            // avançar próxima geração
            r.setProximaGeracao(calcularProxima(data, r.getPeriodicidade()));
            recorrenteRepo.save(r);
        }

        return criadas;
    }

    private LocalDate calcularProxima(LocalDate atual, Periodicidade p) {
        if (p == Periodicidade.SEMANAL) return atual.plusWeeks(1);
        return atual.plusMonths(1);
    }

    private void validarBase(DespesaRecorrente r) {
        if (r.getDescricao() == null || r.getDescricao().trim().isEmpty())
            throw new IllegalArgumentException("Descrição é obrigatória.");
        if (r.getValor() == null || r.getValor() <= 0)
            throw new IllegalArgumentException("Valor deve ser superior a 0.");
        if (r.getMetodoPagamento() == null)
            throw new IllegalArgumentException("Método de pagamento é obrigatório.");
        if (r.getPeriodicidade() == null)
            throw new IllegalArgumentException("Periodicidade é obrigatória.");
        if (r.getCategoria() == null || r.getCategoria().getId() == null)
            throw new IllegalArgumentException("Categoria é obrigatória.");
        if (r.getProximaGeracao() != null && r.getProximaGeracao().isAfter(LocalDate.now().plusYears(10)))
            throw new IllegalArgumentException("Data de próxima geração inválida.");
    }
}
