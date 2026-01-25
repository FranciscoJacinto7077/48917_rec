package upt.gestaodespesas.service;

import java.util.List;

import org.springframework.stereotype.Service;

import upt.gestaodespesas.entity.Categoria;
import upt.gestaodespesas.entity.Utilizador;
import upt.gestaodespesas.repository.CategoriaRepository;
import upt.gestaodespesas.repository.DespesaRepository;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepo;
    private final DespesaRepository despesaRepo;

    public CategoriaService(CategoriaRepository categoriaRepo, DespesaRepository despesaRepo) {
        this.categoriaRepo = categoriaRepo;
        this.despesaRepo = despesaRepo;
    }

    public List<Categoria> listarCategorias(Utilizador u) {
        return categoriaRepo.findByUtilizadorIdOrderByNomeAsc(u.getId());
    }

    public Categoria criarCategoria(Utilizador u, Categoria c) {
        if (categoriaRepo.existsByUtilizadorIdAndNomeIgnoreCase(u.getId(), c.getNome())) {
            throw new RuntimeException("Já existe uma categoria com esse nome.");
        }
        c.setId(null);
        c.setUtilizador(u);
        return categoriaRepo.save(c);
    }

    // RF5: editar categoria (nome único por utilizador)
    public Categoria atualizarCategoria(Utilizador u, Long categoriaId, Categoria dados) {
        Categoria existente = categoriaRepo.findByIdAndUtilizadorId(categoriaId, u.getId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        String novoNome = (dados.getNome() == null) ? "" : dados.getNome().trim();
        if (novoNome.isEmpty()) {
            throw new RuntimeException("O nome da categoria é obrigatório.");
        }

        if (!existente.getNome().equalsIgnoreCase(novoNome) &&
            categoriaRepo.existsByUtilizadorIdAndNomeIgnoreCase(u.getId(), novoNome)) {
            throw new RuntimeException("Já existe uma categoria com esse nome.");
        }

        existente.setNome(novoNome);
        return categoriaRepo.save(existente);
    }

    public void apagarCategoria(Utilizador u, Long categoriaId) {
        Categoria cat = categoriaRepo.findByIdAndUtilizadorId(categoriaId, u.getId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        if (despesaRepo.existsByUtilizadorIdAndCategoriaId(u.getId(), categoriaId)) {
            throw new RuntimeException("Não é possível apagar categorias com despesas associadas.");
        }
        categoriaRepo.delete(cat);
    }
}
