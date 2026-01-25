package upt.gestaodespesas.dto;

public class UpdatePasswordRequest {

    private String passwordAtual;
    private String novaPassword;

    public UpdatePasswordRequest() {
    }

    public UpdatePasswordRequest(String passwordAtual, String novaPassword) {
        this.passwordAtual = passwordAtual;
        this.novaPassword = novaPassword;
    }

    public String getPasswordAtual() {
        return passwordAtual;
    }

    public void setPasswordAtual(String passwordAtual) {
        this.passwordAtual = passwordAtual;
    }

    public String getNovaPassword() {
        return novaPassword;
    }

    public void setNovaPassword(String novaPassword) {
        this.novaPassword = novaPassword;
    }

    public String getCurrentPassword() {
        return this.passwordAtual;
    }

    public String getNewPassword() {
        return this.novaPassword;
    }
}
