package upt.gestaodespesas.controller;

import javax.validation.Valid;

import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import upt.gestaodespesas.dto.*;
import upt.gestaodespesas.entity.Utilizador;
import upt.gestaodespesas.repository.UtilizadorRepository;
import upt.gestaodespesas.security.JwtUtil;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UtilizadorRepository utilizadorRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UtilizadorRepository utilizadorRepo, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.utilizadorRepo = utilizadorRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody AuthRegisterRequest req) {
        if (utilizadorRepo.existsByEmail(req.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        Utilizador u = new Utilizador();
        u.setNome(req.getNome());
        u.setEmail(req.getEmail());
        u.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        utilizadorRepo.save(u);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthTokenResponse> login(@Valid @RequestBody AuthLoginRequest req) {
        Utilizador u = utilizadorRepo.findByEmail(req.getEmail())
                .orElse(null);

        if (u == null || !passwordEncoder.matches(req.getPassword(), u.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = jwtUtil.generateToken(u.getEmail());
        return ResponseEntity.ok(new AuthTokenResponse(token));
    }

    @PutMapping("/password")
    public ResponseEntity<Void> updatePassword(@Valid @RequestBody UpdatePasswordRequest req,
                                              org.springframework.security.core.Authentication auth) {
        String email = auth.getName();
        Utilizador u = utilizadorRepo.findByEmail(email).orElse(null);
        if (u == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if (!passwordEncoder.matches(req.getPasswordAtual(), u.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        u.setPasswordHash(passwordEncoder.encode(req.getPasswordNova()));
        utilizadorRepo.save(u);
        return ResponseEntity.noContent().build();
    }
}
