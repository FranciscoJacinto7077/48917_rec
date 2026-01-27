package upt.gestaodespesas.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import upt.gestaodespesas.model.TotalMensalResponse;
import upt.gestaodespesas.model.TotalPorCategoriaItem;
import upt.gestaodespesas.service.ApiService;

import java.time.LocalDate;
import java.util.List;

public class TotaisController {

    @FXML private ComboBox<Integer> yearBox;
    @FXML private ComboBox<Integer> monthBox;
    @FXML private Label monthlyTotalLabel;

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;

    @FXML private TableView<TotalPorCategoriaItem> table;
    @FXML private TableColumn<TotalPorCategoriaItem, String> catColumn;
    @FXML private TableColumn<TotalPorCategoriaItem, Double> totalColumn;

    private final ApiService api = new ApiService();
    private final ObservableList<TotalPorCategoriaItem> data = FXCollections.observableArrayList();

    private MainController mainController;
    public void setMainController(MainController mainController) { this.mainController = mainController; }

    @FXML
    public void initialize() {
        int anoAtual = LocalDate.now().getYear();
        yearBox.setItems(FXCollections.observableArrayList(anoAtual - 2, anoAtual - 1, anoAtual, anoAtual + 1));
        yearBox.setValue(anoAtual);

        monthBox.setItems(FXCollections.observableArrayList(1,2,3,4,5,6,7,8,9,10,11,12));
        monthBox.setValue(LocalDate.now().getMonthValue());

        catColumn.setCellValueFactory(cd ->
                new javafx.beans.property.SimpleStringProperty(cd.getValue().getCategoriaNome()));
        totalColumn.setCellValueFactory(cd ->
                new javafx.beans.property.SimpleObjectProperty<>(cd.getValue().getTotal()));
        table.setItems(data);

        startDatePicker.setValue(LocalDate.now().withDayOfMonth(1));
        endDatePicker.setValue(LocalDate.now());

        refreshAll();
    }

    @FXML public void onRefreshMonthly() { refreshMonthly(); }
    @FXML public void onRefreshByCategory() { refreshByCategory(); }
    @FXML public void onRefreshAll() { refreshAll(); }

    private void refreshAll() {
        refreshMonthly();
        refreshByCategory();
    }

    private void refreshMonthly() {
        try {
            Integer ano = yearBox.getValue();
            Integer mes = monthBox.getValue();
            if (ano == null || mes == null) {
                alert(Alert.AlertType.WARNING, "Aviso", "Seleciona ano e mês.");
                return;
            }

            TotalMensalResponse resp = api.get(
                    "/api/analytics/total-mensal?ano=" + ano + "&mes=" + mes,
                    TotalMensalResponse.class
            );

            monthlyTotalLabel.setText(String.format("%.2f €", resp.getTotal()));
        } catch (Exception e) {
            e.printStackTrace();
            alert(Alert.AlertType.ERROR, "Erro", normalize(e, "Falha ao obter total mensal."));
        }
    }

    private void refreshByCategory() {
        try {
            LocalDate ini = startDatePicker.getValue();
            LocalDate fim = endDatePicker.getValue();

            if ((ini == null) != (fim == null)) {
                alert(Alert.AlertType.WARNING, "Aviso", "Indica data início e data fim (ou nenhuma).");
                return;
            }
            if (ini != null && fim != null && ini.isAfter(fim)) {
                alert(Alert.AlertType.WARNING, "Aviso", "dataInicio não pode ser posterior a dataFim.");
                return;
            }

            String endpoint = "/api/analytics/total-por-categoria";
            if (ini != null && fim != null) {
                endpoint += "?dataInicio=" + ini + "&dataFim=" + fim;
            }

            List<TotalPorCategoriaItem> list =
                    api.getList(endpoint, new TypeReference<List<TotalPorCategoriaItem>>() {});
            data.setAll(list);

        } catch (Exception e) {
            e.printStackTrace();
            alert(Alert.AlertType.ERROR, "Erro", normalize(e, "Falha ao obter totais por categoria."));
        }
    }

    private String normalize(Exception e, String fallback) {
        String msg = e.getMessage();
        if (msg == null || msg.isBlank()) return fallback;
        int idx = msg.indexOf(" - ");
        return (idx >= 0) ? msg.substring(idx + 3) : msg;
    }

    private void alert(Alert.AlertType type, String title, String content) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(content);
        a.show();
    }
}
