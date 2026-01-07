package upt.gestaodespesas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import upt.gestaodespesas.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
	Optional<Categoria> findByNome(String nome);
}
