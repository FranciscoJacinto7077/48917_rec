package upt.gestaodespesas.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import upt.gestaodespesas.model.DashboardResponse;
import upt.gestaodespesas.model.Utilizador;
import upt.gestaodespesas.service.ApiService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label totalLabel;
    @FXML private Label avgLabel;
    @FXML private Label biggestExpenseLabel;
    @FXML private Label topCategoryLabel;

    private final ApiService api = new ApiService();
    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void initialize() {
        loadUserName();
        loadDashboard();
    }

    private void loadUserName() {
        try {
            Utilizador user = api.get("/api/utilizador/me", Utilizador.class);
            if (user != null && user.getNome() != null) {
                welcomeLabel.setText("Olá, " + user.getNome() + "!");
            }
        } catch (Exception e) {
            welcomeLabel.setText("Olá!");
        }
    }

    private void loadDashboard() {
        try {
            // Load last 30 days
            LocalDate end = LocalDate.now();
            LocalDate start = end.minusDays(30);
            
            String url = String.format("/api/analytics/dashboard?dataInicio=%s&dataFim=%s", start, end);
            DashboardResponse data = api.get(url, DashboardResponse.class);
            
            totalLabel.setText(String.format("€%.2f", data.getTotalPeriodo()));
            avgLabel.setText(String.format("€%.2f/dia", data.getMediaDiaria()));
            
            if (data.getMaiorDespesa() != null) {
                biggestExpenseLabel.setText(String.format("%s - €%.2f", 
                    data.getMaiorDespesa().getDescricao(), 
                    data.getMaiorDespesa().getValor()));
            }
            
            if (data.getTopCategoria() != null) {
                topCategoryLabel.setText(String.format("%s - €%.2f", 
                    data.getTopCategoria().getCategoriaNome(), 
                    data.getTopCategoria().getTotal()));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            totalLabel.setText("Erro ao carregar");
        }
    }

    @FXML
    public void onNewExpense() {
        if (mainController != null) {
            mainController.showExpenses();
        }
    }

    @FXML
    public void onViewAll() {
        if (mainController != null) {
            mainController.showExpenses();
        }
    }
}
