package upt.gestaodespesas.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import upt.gestaodespesas.App;
import upt.gestaodespesas.service.ApiService;

import java.io.IOException;

public class MainController {

    @FXML private BorderPane mainLayout;
    @FXML private MenuBar appMenuBar;

    @FXML
    public void initialize() {
        setAuthenticated(false);
    }

    public void setAuthenticated(boolean isAuthenticated) {
        appMenuBar.setVisible(isAuthenticated);
        appMenuBar.setManaged(isAuthenticated);

        if (isAuthenticated) {
            showDashboard();
        } else {
            showLogin();
        }
    }

    public void showDashboard() { loadView("dashboard-view.fxml"); }
    public void showExpenses() { loadView("expenses-view.fxml"); }
    public void showCategories() { loadView("categories-view.fxml"); }
    public void showProfile() { loadView("profile-view.fxml"); }
    public void showRecorrencias() { loadView("recorrencias-view.fxml"); }
    public void showTotais() { loadView("totais-view.fxml"); }

    public void doLogout() {
        ApiService.clearToken();
        setAuthenticated(false);
    }

    public void showLogin() { loadView("login-view.fxml"); }
    public void showRegister() { loadView("register-view.fxml"); }

    private void loadView(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/upt/gestaodespesas/view/" + fxml));
            Parent view = loader.load();

            Object controller = loader.getController();

            // inject MainController em TODOS os controladores que precisem
            if (controller instanceof LoginController c) c.setMainController(this);
            if (controller instanceof RegisterController c) c.setMainController(this);
            if (controller instanceof DashboardController c) c.setMainController(this);
            if (controller instanceof ProfileController c) c.setMainController(this);
            if (controller instanceof RecorrenciasController c) c.setMainController(this);
            if (controller instanceof TotaisController c) c.setMainController(this);

            mainLayout.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro a carregar view: " + fxml + " -> " + e.getMessage());
        }
    }
}
