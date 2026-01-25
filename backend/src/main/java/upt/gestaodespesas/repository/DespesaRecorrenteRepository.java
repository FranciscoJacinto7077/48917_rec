package upt.gestaodespesas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import upt.gestaodespesas.entity.DespesaRecorrente;

import java.util.List;
import java.util.Optional;

@Repository
public interface DespesaRecorrenteRepository extends JpaRepository<DespesaRecorrente, Long> {

    List<DespesaRecorrente> findByUtilizadorId(Long utilizadorId);

    Optional<DespesaRecorrente> findByIdAndUtilizadorId(Long id, Long utilizadorId);
}
