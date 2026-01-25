package upt.gestaodespesas.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import upt.gestaodespesas.dto.UpdatePasswordRequest;
import upt.gestaodespesas.dto.UpdateProfileRequest;
import upt.gestaodespesas.entity.Utilizador;
import upt.gestaodespesas.service.UtilizadorService;

@RestController
@RequestMapping("/api/utilizador")
public class UtilizadorController {

    private final UtilizadorService utilizadorService;

    public UtilizadorController(UtilizadorService utilizadorService) {
        this.utilizadorService = utilizadorService;
    }

    @GetMapping("/me")
    public ResponseEntity<Utilizador> me() {
        return ResponseEntity.ok(utilizadorService.getAuthenticatedUser());
    }

    @PutMapping("/profile")
    public ResponseEntity<Utilizador> updateProfile(@Valid @RequestBody UpdateProfileRequest req) {
        return ResponseEntity.ok(utilizadorService.updateProfile(req));
    }

    @PutMapping("/password")
    public ResponseEntity<Void> updatePassword(@Valid @RequestBody UpdatePasswordRequest req) {
        utilizadorService.updatePassword(req);
        return ResponseEntity.noContent().build();
    }
}
