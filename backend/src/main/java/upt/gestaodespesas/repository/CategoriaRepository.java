package upt.gestaodespesas.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import upt.gestaodespesas.entity.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    List<Categoria> findByUtilizadorId(Long utilizadorId);
    boolean existsByUtilizadorIdAndNomeIgnoreCase(Long utilizadorId, String nome);
    Optional<Categoria> findByIdAndUtilizadorId(Long id, Long utilizadorId);
}
