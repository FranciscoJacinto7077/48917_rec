package upt.gestaodespesas.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import upt.gestaodespesas.model.DashboardResponse;
import upt.gestaodespesas.model.Utilizador;
import upt.gestaodespesas.service.ApiService;

import java.time.LocalDate;

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

    /** Se precisares de forçar refresh manualmente a partir do MainController */
    public void refresh() {
        loadUserName();
        loadDashboard();
    }

    private void loadUserName() {
        try {
            Utilizador user = api.get("/api/utilizador/me", Utilizador.class);
            if (user != null && user.getNome() != null && !user.getNome().isBlank()) {
                welcomeLabel.setText("Olá, " + user.getNome() + "!");
                return;
            }
        } catch (Exception ignored) {
            // cai para o default
        }
        welcomeLabel.setText("Olá!");
    }

    private void loadDashboard() {
        // Defaults (garante que nunca ficas com dados “antigos” na UI)
        totalLabel.setText("€0,00");
        avgLabel.setText("€0,00/dia");
        biggestExpenseLabel.setText("N/A");
        topCategoryLabel.setText("N/A");

        try {
            // Últimos 30 dias (inclusive): end + (end-29) => 30 dias
            LocalDate end = LocalDate.now();
            LocalDate start = end.minusDays(29);

            String url = String.format(
                    "/api/analytics/dashboard?dataInicio=%s&dataFim=%s",
                    start, end
            );

            DashboardResponse data = api.get(url, DashboardResponse.class);
            if (data == null) return;

            totalLabel.setText(String.format("€%.2f", safeDouble(data.getTotalPeriodo())));
            avgLabel.setText(String.format("€%.2f/dia", safeDouble(data.getMediaDiaria())));

            if (data.getMaiorDespesa() != null) {
                String desc = data.getMaiorDespesa().getDescricao();
                Double val = data.getMaiorDespesa().getValor();
                if (desc != null && val != null) {
                    biggestExpenseLabel.setText(String.format("%s - €%.2f", desc, val));
                }
            }

            if (data.getTopCategoria() != null) {
                String nome = data.getTopCategoria().getCategoriaNome();
                Double total = data.getTopCategoria().getTotal();
                if (nome != null && total != null) {
                    topCategoryLabel.setText(String.format("%s - €%.2f", nome, total));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            totalLabel.setText("Erro ao carregar");
        }
    }

    private double safeDouble(Double v) {
        return (v == null) ? 0.0 : v;
    }

    @FXML
    public void onNewExpense() {
        if (mainController != null) mainController.showExpenses();
    }

    @FXML
    public void onViewAll() {
        if (mainController != null) mainController.showExpenses();
    }
}
