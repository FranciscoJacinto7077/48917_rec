package upt.gestaodespesas.security;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import upt.gestaodespesas.entity.Utilizador;
import upt.gestaodespesas.repository.UtilizadorRepository;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UtilizadorRepository utilizadorRepo;

    public CustomUserDetailsService(UtilizadorRepository utilizadorRepo) {
        this.utilizadorRepo = utilizadorRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Utilizador u = utilizadorRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilizador não encontrado"));
        return new org.springframework.security.core.userdetails.User(
                u.getEmail(),
                u.getPasswordHash(),
                Collections.emptyList()
        );
    }
}
