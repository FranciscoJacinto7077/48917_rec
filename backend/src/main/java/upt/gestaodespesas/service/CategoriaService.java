package upt.gestaodespesas.service;

import java.util.List;

import org.springframework.stereotype.Service;

import upt.gestaodespesas.entity.Categoria;
import upt.gestaodespesas.repository.CategoriaRepository;
import upt.gestaodespesas.repository.DespesaRepository;

@Service
public class CategoriaService {

    private final CategoriaRepository repo;
    private final DespesaRepository despesaRepo;

    public CategoriaService(CategoriaRepository repo, DespesaRepository despesaRepo) {
        this.repo = repo;
        this.despesaRepo = despesaRepo;
    }

    public List<Categoria> listarCategorias() {
        return repo.findAll();
    }

    public Categoria criarCategoria(Categoria c) {
        c.setId(null);
        return repo.save(c);
    }

    public void apagarCategoria(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Categoria não encontrada");
        }
        if (despesaRepo.existsByCategoriaId(id)) {
            throw new RuntimeException("Não é possível apagar categorias que estão a ser utilizadas em despesas.");
        }
        repo.deleteById(id);
    }
}
