package upt.gestaodespesas.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import upt.gestaodespesas.model.UpdatePasswordRequest;
import upt.gestaodespesas.model.UpdateProfileRequest;
import upt.gestaodespesas.model.Utilizador;
import upt.gestaodespesas.service.ApiService;

public class ProfileController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;

    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;

    private final ApiService api = new ApiService();
    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void initialize() {
        loadMe();
    }

    private void loadMe() {
        try {
            Utilizador me = api.get("/api/utilizador/me", Utilizador.class);
            if (me != null) {
                nameField.setText(me.getNome() == null ? "" : me.getNome());
                emailField.setText(me.getEmail() == null ? "" : me.getEmail());
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erro", normalizeApiError(e, "Falha ao carregar perfil."));
        }
    }

    @FXML
    public void onSaveProfile() {
        String nome = nameField.getText() == null ? "" : nameField.getText().trim();
        String email = emailField.getText() == null ? "" : emailField.getText().trim();

        if (nome.isEmpty() || email.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Aviso", "Nome e email são obrigatórios.");
            return;
        }

        try {
            UpdateProfileRequest req = new UpdateProfileRequest(nome, email);
            api.put("/api/utilizador/profile", req, Utilizador.class);
            showAlert(Alert.AlertType.INFORMATION, "OK", "Perfil atualizado com sucesso.");
            loadMe();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erro", normalizeApiError(e, "Falha ao atualizar perfil."));
        }
    }

    @FXML
    public void onChangePassword() {
        String current = currentPasswordField.getText();
        String newPass = newPasswordField.getText();
        String confirm = confirmPasswordField.getText();

        if (isBlank(current) || isBlank(newPass) || isBlank(confirm)) {
            showAlert(Alert.AlertType.WARNING, "Aviso", "Preenche todos os campos da password.");
            return;
        }
        if (!newPass.equals(confirm)) {
            showAlert(Alert.AlertType.WARNING, "Aviso", "Nova password e confirmação não coincidem.");
            return;
        }

        try {
            UpdatePasswordRequest req = new UpdatePasswordRequest(current, newPass);
            //endpoint correto do backend
            api.put("/api/utilizador/password", req, Void.class);

            showAlert(Alert.AlertType.INFORMATION, "OK", "Password alterada com sucesso.");
            currentPasswordField.clear();
            newPasswordField.clear();
            confirmPasswordField.clear();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erro", normalizeApiError(e, "Falha ao alterar password."));
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private String normalizeApiError(Exception e, String fallback) {
        String msg = e.getMessage();
        if (msg == null || msg.isBlank()) return fallback;
        int idx = msg.indexOf(" - ");
        return (idx >= 0) ? msg.substring(idx + 3) : msg;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(content);
        a.show();
    }
}
