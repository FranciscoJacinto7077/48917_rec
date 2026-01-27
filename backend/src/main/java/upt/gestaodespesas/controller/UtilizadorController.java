package upt.gestaodespesas.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import upt.gestaodespesas.dto.UpdatePasswordRequest;
import upt.gestaodespesas.dto.UpdateProfileRequest;
import upt.gestaodespesas.dto.UserMeResponse;
import upt.gestaodespesas.dto.DtoMapper;
import upt.gestaodespesas.service.UtilizadorService;

@RestController
@RequestMapping("/api/utilizador")
public class UtilizadorController {

    private final UtilizadorService utilizadorService;

    public UtilizadorController(UtilizadorService utilizadorService) {
        this.utilizadorService = utilizadorService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserMeResponse> me() {
        return ResponseEntity.ok(DtoMapper.toUserMe(utilizadorService.getAuthenticatedUser()));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserMeResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest req) {
        return ResponseEntity.ok(DtoMapper.toUserMe(utilizadorService.updateProfile(req)));
    }

    @PutMapping("/password")
    public ResponseEntity<Void> updatePassword(@Valid @RequestBody UpdatePasswordRequest req) {
        utilizadorService.updatePassword(req);
        return ResponseEntity.noContent().build();
    }
}
