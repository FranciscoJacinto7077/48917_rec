package upt.gestaodespesas.service;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import upt.gestaodespesas.dto.UpdatePasswordRequest;
import upt.gestaodespesas.dto.UpdateProfileRequest;
import upt.gestaodespesas.entity.Utilizador;
import upt.gestaodespesas.exception.BadRequestException;
import upt.gestaodespesas.exception.ConflictException;
import upt.gestaodespesas.exception.NotFoundException;
import upt.gestaodespesas.exception.UnauthorizedException;
import upt.gestaodespesas.repository.UtilizadorRepository;

@Service
public class UtilizadorService {

    private final UtilizadorRepository utilizadorRepository;
    private final PasswordEncoder passwordEncoder;

    public UtilizadorService(UtilizadorRepository utilizadorRepository,
                             PasswordEncoder passwordEncoder) {
        this.utilizadorRepository = utilizadorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Utilizador getCurrentUserOrThrow() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            throw new UnauthorizedException("Não autenticado.");
        }

        String email = auth.getName();
        return utilizadorRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Utilizador não encontrado: " + email));
    }

    // Compatibilidade com controllers já criados
    public Utilizador getAuthenticatedUser() {
        return getCurrentUserOrThrow();
    }

    @Transactional
    public Utilizador register(String nome, String email, String password) {
        if (utilizadorRepository.existsByEmail(email)) {
            throw new ConflictException("Já existe um utilizador com esse email.");
        }

        Utilizador u = new Utilizador();
        u.setNome(nome);
        u.setEmail(email);
        u.setPasswordHash(passwordEncoder.encode(password));

        return utilizadorRepository.save(u);
    }

    public boolean verifyPassword(Utilizador utilizador, String rawPassword) {
        if (utilizador == null) return false;
        return passwordEncoder.matches(rawPassword, utilizador.getPasswordHash());
    }

    /**
     * Atualiza password (usado pelo AuthController):
     * valida password atual e aplica a nova.
     */
    @Transactional
    public void updatePassword(Utilizador utilizador, String currentPassword, String newPassword) {
        if (utilizador == null) {
            throw new NotFoundException("Utilizador não encontrado.");
        }
        if (currentPassword == null || currentPassword.isBlank()
                || newPassword == null || newPassword.isBlank()) {
            throw new BadRequestException("Password atual e nova password são obrigatórias.");
        }

        if (!verifyPassword(utilizador, currentPassword)) {
            throw new UnauthorizedException("Password atual incorreta.");
        }

        utilizador.setPasswordHash(passwordEncoder.encode(newPassword));
        utilizadorRepository.save(utilizador);
    }

    /**
     * Overload pedido pelo teu UtilizadorController:
     * recebe o DTO e aplica no utilizador autenticado.
     */
    @Transactional
    public void updatePassword(UpdatePasswordRequest req) {
        Utilizador u = getAuthenticatedUser();
        updatePassword(u, req.getCurrentPassword(), req.getNewPassword());
    }

    /**
     * Atualiza dados de perfil (nome/email) do utilizador autenticado.
     * Mantém validação de unicidade do email.
     */
    @Transactional
    public Utilizador updateProfile(UpdateProfileRequest req) {
        Utilizador u = getAuthenticatedUser();

        if (req.getNome() != null && !req.getNome().isBlank()) {
            u.setNome(req.getNome().trim());
        }

        if (req.getEmail() != null && !req.getEmail().isBlank()) {
            String novoEmail = req.getEmail().trim();

            // se mudou email, validar unicidade
            if (!novoEmail.equalsIgnoreCase(u.getEmail())
                    && utilizadorRepository.existsByEmail(novoEmail)) {
                throw new ConflictException("Já existe um utilizador com esse email.");
            }

            u.setEmail(novoEmail);
        }

        return utilizadorRepository.save(u);
    }
}
