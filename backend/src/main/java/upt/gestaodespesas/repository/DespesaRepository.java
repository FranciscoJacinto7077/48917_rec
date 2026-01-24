package upt.gestaodespesas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import upt.gestaodespesas.entity.Despesa;

import java.util.List;

public interface DespesaRepository extends JpaRepository<Despesa, Long> {
	List <Despesa> findByCategoriaId(Long categoriaId);
	boolean existsByCategoriaId(Long categoriaId);
	
}
