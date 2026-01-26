package upt.gestaodespesas.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.MenuBar;
import upt.gestaodespesas.App;

import java.io.IOException;

public class MainController {

    @FXML
    private BorderPane mainLayout;
    
    @FXML
    private MenuBar appMenuBar;

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

    public void showDashboard() {
        loadView("dashboard-view.fxml", this);
    }
    
    public void showExpenses() {
        loadView("expenses-view.fxml", this);
    }
    
    public void showCategories() {
        loadView("categories-view.fxml", this);
    }

    public void showChangePassword() {
        loadView("change-password-view.fxml", this);
    }

    public void doLogout() {
        setAuthenticated(false);
    }

    public void showLogin() {
        loadView("login-view.fxml", this);
    }

    public void showRegister() {
        loadView("register-view.fxml", this);
    }

    private void loadView(String fxml, MainController parent) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/upt/gestaodespesas/view/" + fxml));
            Parent view = loader.load();
            
            // Inject MainController into the child controller if it supports it
            Object controller = loader.getController();
            if (controller instanceof LoginController) {
                ((LoginController) controller).setMainController(this);
            } else if (controller instanceof RegisterController) {
                ((RegisterController) controller).setMainController(this);
            } else if (controller instanceof DashboardController) {
                ((DashboardController) controller).setMainController(this);
            }

            mainLayout.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading view: " + fxml);
            System.err.println("Error message: " + e.getMessage());
        }
    }
}
