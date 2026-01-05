package upt.gestaodespesas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import upt.gestaodespesas.model.Despesa;

public interface DespesaRepository extends JpaRepository<Despesa, Long> {
	
}
