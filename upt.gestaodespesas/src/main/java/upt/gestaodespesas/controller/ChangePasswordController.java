package upt.gestaodespesas.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import upt.gestaodespesas.model.UpdatePasswordRequest;
import upt.gestaodespesas.service.ApiService;

public class ChangePasswordController {

    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;

    private final ApiService api = new ApiService();

    @FXML
    public void onChangePassword() {
        String current = currentPasswordField.getText();
        String newPass = newPasswordField.getText();
        String confirm = confirmPasswordField.getText();

        if (current.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
            showAlert("Erro", "Preencha todos os campos.");
            return;
        }

        if (!newPass.equals(confirm)) {
            showAlert("Erro", "A nova password e a confirmação não coincidem.");
            return;
        }

        try {
            UpdatePasswordRequest req = new UpdatePasswordRequest(current, newPass);
            api.put("/api/auth/password", req, Void.class);
            
            showAlert("Sucesso", "Password alterada com sucesso.");
            clearFields();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erro", "Falha ao alterar password: " + e.getMessage());
        }
    }

    private void clearFields() {
        currentPasswordField.setText("");
        newPasswordField.setText("");
        confirmPasswordField.setText("");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();
    }
}
