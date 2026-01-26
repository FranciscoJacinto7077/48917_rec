package upt.gestaodespesas.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import upt.gestaodespesas.model.AuthRegisterRequest;
import upt.gestaodespesas.service.ApiService;

public class RegisterController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    private MainController mainController;
    private final ApiService api = new ApiService();

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void onRegister() {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert("Erro", "Por favor preencha todos os campos.");
            return;
        }

        try {
            AuthRegisterRequest request = new AuthRegisterRequest(name, email, password);
            // The backend returns Void on success (201 Created)
            api.post("/api/auth/register", request, Void.class);
            
            showAlert("Sucesso", "Registo efetuado com sucesso! Faça login.");
            if (mainController != null) {
                mainController.showLogin();
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erro", "Erro ao efetuar registo: " + e.getMessage());
        }
    }

    @FXML
    public void onBack() {
        if (mainController != null) {
            mainController.showLogin();
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
