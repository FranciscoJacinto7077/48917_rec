package upt.gestaodespesas.service;

import java.util.List;

import org.springframework.stereotype.Service;

import upt.gestaodespesas.dto.CategoriaRequest;
import upt.gestaodespesas.entity.Categoria;
import upt.gestaodespesas.entity.Utilizador;
import upt.gestaodespesas.exception.BadRequestException;
import upt.gestaodespesas.exception.ConflictException;
import upt.gestaodespesas.exception.NotFoundException;
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

    public Categoria criarCategoria(Utilizador u, CategoriaRequest req) {
        String nome = (req.getNome() == null) ? "" : req.getNome().trim();
        if (nome.isEmpty()) {
            throw new BadRequestException("O nome da categoria é obrigatório.");
        }
        if (categoriaRepo.existsByUtilizadorIdAndNomeIgnoreCase(u.getId(), nome)) {
            throw new ConflictException("Já existe uma categoria com esse nome.");
        }
        Categoria c = new Categoria();
        c.setId(null);
        c.setNome(nome);
        c.setUtilizador(u);
        return categoriaRepo.save(c);
    }

    // RF5: editar categoria (nome único por utilizador)
    public Categoria atualizarCategoria(Utilizador u, Long categoriaId, CategoriaRequest dados) {
        Categoria existente = categoriaRepo.findByIdAndUtilizadorId(categoriaId, u.getId())
                .orElseThrow(() -> new NotFoundException("Categoria não encontrada."));

        String novoNome = (dados.getNome() == null) ? "" : dados.getNome().trim();
        if (novoNome.isEmpty()) {
            throw new BadRequestException("O nome da categoria é obrigatório.");
        }

        if (!existente.getNome().equalsIgnoreCase(novoNome)
                && categoriaRepo.existsByUtilizadorIdAndNomeIgnoreCase(u.getId(), novoNome)) {
            throw new ConflictException("Já existe uma categoria com esse nome.");
        }

        existente.setNome(novoNome);
        return categoriaRepo.save(existente);
    }

    public void apagarCategoria(Utilizador u, Long categoriaId) {
        Categoria cat = categoriaRepo.findByIdAndUtilizadorId(categoriaId, u.getId())
                .orElseThrow(() -> new NotFoundException("Categoria não encontrada."));

        if (despesaRepo.existsByUtilizadorIdAndCategoriaId(u.getId(), categoriaId)) {
            throw new ConflictException("Não é possível apagar categorias com despesas associadas.");
        }
        categoriaRepo.delete(cat);
    }
}
