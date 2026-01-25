package upt.gestaodespesas.security;

import java.util.Collections;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import upt.gestaodespesas.entity.Utilizador;
import upt.gestaodespesas.repository.UtilizadorRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UtilizadorRepository utilizadorRepository;

    public CustomUserDetailsService(UtilizadorRepository utilizadorRepository) {
        this.utilizadorRepository = utilizadorRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Utilizador u = utilizadorRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilizador não encontrado"));

        return new User(u.getEmail(), u.getPasswordHash(), Collections.emptyList());
    }
}
