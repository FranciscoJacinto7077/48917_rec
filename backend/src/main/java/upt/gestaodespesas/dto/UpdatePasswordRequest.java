package upt.gestaodespesas.dto;

import javax.validation.constraints.NotBlank;

public class UpdatePasswordRequest {

    @NotBlank
    private String passwordAtual;

    @NotBlank
    private String passwordNova;

    public String getPasswordAtual() { return passwordAtual; }
    public void setPasswordAtual(String passwordAtual) { this.passwordAtual = passwordAtual; }

    public String getPasswordNova() { return passwordNova; }
    public void setPasswordNova(String passwordNova) { this.passwordNova = passwordNova; }
}
