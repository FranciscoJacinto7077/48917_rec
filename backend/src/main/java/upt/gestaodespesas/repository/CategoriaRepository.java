package upt.gestaodespesas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import upt.gestaodespesas.entity.Categoria;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    Optional<Categoria> findByIdAndUtilizadorId(Long id, Long utilizadorId);

    List<Categoria> findByUtilizadorIdOrderByNomeAsc(Long utilizadorId);

    boolean existsByUtilizadorIdAndNomeIgnoreCase(Long utilizadorId, String nome);
}
