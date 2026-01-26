package upt.gestaodespesas.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import upt.gestaodespesas.model.AuthLoginRequest;
import upt.gestaodespesas.model.AuthTokenResponse;
import upt.gestaodespesas.service.ApiService;

import java.io.IOException;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    private final ApiService api = new ApiService();

    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void onLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Erro", "Por favor preencha todos os campos.");
            return;
        }

        try {
            AuthLoginRequest request = new AuthLoginRequest(email, password);
            AuthTokenResponse response = api.post("/api/auth/login", request, AuthTokenResponse.class);
            
            if (response != null && response.getToken() != null) {
                ApiService.setToken(response.getToken());
                // showAlert("Sucesso", "Login efetuado com sucesso!");
                if (mainController != null) {
                    mainController.setAuthenticated(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Login falhou", "Erro ao efetuar login: " + e.getMessage());
        }
    }

    @FXML
    public void onRegister() {
        if (mainController != null) {
            mainController.showRegister();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
