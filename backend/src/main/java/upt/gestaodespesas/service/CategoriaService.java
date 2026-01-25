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

    public List<Categoria> listarCategorias(Long utilizadorId) {
        return categoriaRepo.findByUtilizadorId(utilizadorId);
    }

    public Categoria criarCategoria(Long utilizadorId, String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da categoria é obrigatório.");
        }

        String n = nome.trim();
        if (categoriaRepo.existsByUtilizadorIdAndNomeIgnoreCase(utilizadorId, n)) {
            throw new IllegalArgumentException("Já existe uma categoria com esse nome para o utilizador.");
        }

        Categoria c = new Categoria();
        c.setId(null);
        c.setNome(n);

        Utilizador u = new Utilizador();
        u.setId(utilizadorId);
        c.setUtilizador(u);

        return categoriaRepo.save(c);
    }

    public Categoria editarCategoria(Long utilizadorId, Long categoriaId, String novoNome) {
        if (novoNome == null || novoNome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da categoria é obrigatório.");
        }

        Categoria c = categoriaRepo.findByIdAndUtilizadorId(categoriaId, utilizadorId)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada."));

        String n = novoNome.trim();

        // se mudou, tem de continuar único por utilizador
        if (!c.getNome().equalsIgnoreCase(n)
                && categoriaRepo.existsByUtilizadorIdAndNomeIgnoreCase(utilizadorId, n)) {
            throw new IllegalArgumentException("Já existe uma categoria com esse nome para o utilizador.");
        }

        c.setNome(n);
        return categoriaRepo.save(c);
    }

    public void apagarCategoria(Long utilizadorId, Long categoriaId) {
        Categoria c = categoriaRepo.findByIdAndUtilizadorId(categoriaId, utilizadorId)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada."));

        if (despesaRepo.existsByCategoriaIdAndUtilizadorId(categoriaId, utilizadorId)) {
            throw new IllegalStateException("Não é possível apagar categorias que estão a ser utilizadas em despesas.");
        }

        categoriaRepo.delete(c);
    }
}
