package upt.gestaodespesas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import upt.gestaodespesas.entity.Despesa;
import upt.gestaodespesas.entity.DespesaRecorrente;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DespesaRepository extends JpaRepository<Despesa, Long>, JpaSpecificationExecutor<Despesa> {

    Optional<Despesa> findByIdAndUtilizadorId(Long id, Long utilizadorId);

    boolean existsByUtilizadorIdAndCategoriaId(Long utilizadorId, Long categoriaId);

    Optional<Despesa> findByOrigemRecorrenteAndData(DespesaRecorrente origemRecorrente, LocalDate data);
}
