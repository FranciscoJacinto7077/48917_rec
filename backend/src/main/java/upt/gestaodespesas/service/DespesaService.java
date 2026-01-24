package upt.gestaodespesas.service;

import java.util.List;

import org.springframework.stereotype.Service;

import upt.gestaodespesas.entity.Despesa;
import upt.gestaodespesas.repository.DespesaRepository;

@Service
public class DespesaService {

    private final DespesaRepository repo;

    public DespesaService(DespesaRepository repo) {
        this.repo = repo;
    }

    public List<Despesa> listar(Long categoriaId) {
        if (categoriaId == null) {
            return repo.findAll();
        }
        return repo.findByCategoriaId(categoriaId);
    }

    public Despesa obterPorId(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Despesa criar(Despesa despesa) {
        despesa.setId(null);
        return repo.save(despesa);
    }

    public Despesa atualizar(Long id, Despesa dados) {
        return repo.findById(id).map(existing -> {
            existing.setDescricao(dados.getDescricao());
            existing.setValor(dados.getValor());
            existing.setData(dados.getData());
            existing.setCategoria(dados.getCategoria());
            existing.setMetodoPagamento(dados.getMetodoPagamento());
            return repo.save(existing);
        }).orElse(null);
    }

    public boolean apagar(Long id) {
        if (!repo.existsById(id)) {
            return false;
        }
        repo.deleteById(id);
        return true;
    }
}
