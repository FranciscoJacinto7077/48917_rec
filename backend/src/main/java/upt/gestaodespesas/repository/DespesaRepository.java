package upt.gestaodespesas.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import upt.gestaodespesas.entity.Despesa;
import upt.gestaodespesas.entity.MetodoPagamento;

public interface DespesaRepository extends JpaRepository<Despesa, Long>, JpaSpecificationExecutor<Despesa> {

    boolean existsByCategoriaIdAndUtilizadorId(Long categoriaId, Long utilizadorId);
    boolean existsByUtilizadorIdAndDataAndDescricaoAndValorAndCategoriaIdAndMetodoPagamento(
            Long utilizadorId,
            LocalDate data,
            String descricao,
            Double valor,
            Long categoriaId,
            MetodoPagamento metodoPagamento
    );
}
