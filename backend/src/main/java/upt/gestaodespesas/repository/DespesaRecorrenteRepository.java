package upt.gestaodespesas.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import upt.gestaodespesas.entity.DespesaRecorrente;

public interface DespesaRecorrenteRepository extends JpaRepository<DespesaRecorrente, Long> {

    List<DespesaRecorrente> findByUtilizadorId(Long utilizadorId);

    Optional<DespesaRecorrente> findByIdAndUtilizadorId(Long id, Long utilizadorId);

    List<DespesaRecorrente> findByAtivaTrueAndProximaGeracaoLessThanEqual(LocalDate dia);
}
