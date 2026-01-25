package upt.gestaodespesas.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import upt.gestaodespesas.dto.AuthLoginRequest;
import upt.gestaodespesas.dto.AuthRegisterRequest;
import upt.gestaodespesas.dto.AuthTokenResponse;
import upt.gestaodespesas.dto.UpdatePasswordRequest;
import upt.gestaodespesas.entity.Utilizador;
import upt.gestaodespesas.exception.UnauthorizedException;
import upt.gestaodespesas.repository.UtilizadorRepository;
import upt.gestaodespesas.security.JwtUtil;
import upt.gestaodespesas.service.UtilizadorService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UtilizadorService utilizadorService;
    private final UtilizadorRepository utilizadorRepository;
    private final JwtUtil jwtUtil;

    public AuthController(UtilizadorService utilizadorService,
                          UtilizadorRepository utilizadorRepository,
                          JwtUtil jwtUtil) {
        this.utilizadorService = utilizadorService;
        this.utilizadorRepository = utilizadorRepository;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody AuthRegisterRequest req) {
        utilizadorService.register(req.getNome(), req.getEmail(), req.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthTokenResponse> login(@Valid @RequestBody AuthLoginRequest req) {
        Utilizador u = utilizadorRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Credenciais inválidas"));

        if (!utilizadorService.verifyPassword(u, req.getPassword())) {
            throw new UnauthorizedException("Credenciais inválidas");
        }

        String token = jwtUtil.generateToken(u.getEmail());
        return ResponseEntity.ok(new AuthTokenResponse(token));
    }

    @PutMapping("/password")
    public ResponseEntity<Void> updatePassword(@Valid @RequestBody UpdatePasswordRequest req) {
        Utilizador u = utilizadorService.getAuthenticatedUser();
        utilizadorService.updatePassword(u, req.getCurrentPassword(), req.getNewPassword());
        return ResponseEntity.noContent().build();
    }
}
