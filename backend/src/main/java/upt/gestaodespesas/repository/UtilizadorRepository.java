package upt.gestaodespesas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import upt.gestaodespesas.entity.Utilizador;

public interface UtilizadorRepository extends JpaRepository<Utilizador, Long> {
    Optional<Utilizador> findByEmail(String email);
    boolean existsByEmail(String email);
}
