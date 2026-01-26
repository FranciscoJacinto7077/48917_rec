package upt.gestaodespesas.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import upt.gestaodespesas.model.Categoria;
import upt.gestaodespesas.model.Despesa;
import upt.gestaodespesas.model.MetodoPagamento;
import upt.gestaodespesas.service.ApiService;
import com.fasterxml.jackson.core.type.TypeReference;

import java.time.LocalDate;
import java.util.List;

public class ExpensesController {

    @FXML private TableView<Despesa> expensesTable;
    @FXML private TableColumn<Despesa, LocalDate> dateColumn;
    @FXML private TableColumn<Despesa, String> descColumn;
    @FXML private TableColumn<Despesa, Double> valueColumn;
    @FXML private TableColumn<Despesa, Categoria> categoryColumn;
    @FXML private TableColumn<Despesa, MetodoPagamento> paymentColumn;

    @FXML private DatePicker datePicker;
    @FXML private TextField descField;
    @FXML private TextField valueField;
    @FXML private ComboBox<Categoria> categoryBox;
    @FXML private ComboBox<MetodoPagamento> paymentBox;
    
    // Filters
    @FXML private DatePicker filterStartDate;
    @FXML private DatePicker filterEndDate;
    @FXML private ComboBox<Categoria> filterCategoryBox;

    private final ApiService api = new ApiService();
    private final ObservableList<Despesa> expensesList = FXCollections.observableArrayList();
    private final ObservableList<Categoria> categoriesList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("data"));
        descColumn.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("valor"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        paymentColumn.setCellValueFactory(new PropertyValueFactory<>("metodoPagamento"));
        
        expensesTable.setItems(expensesList);
        
        paymentBox.setItems(FXCollections.observableArrayList(MetodoPagamento.values()));
        
        loadCategories();
        loadExpenses();
    }

    private void loadCategories() {
        try {
            List<Categoria> list = api.getList("/api/categorias", new TypeReference<List<Categoria>>(){});
            categoriesList.setAll(list);
            categoryBox.setItems(categoriesList);
            filterCategoryBox.setItems(categoriesList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void loadExpenses() {
        try {
            String query = "/api/despesas?";
            if (filterStartDate.getValue() != null) query += "dataInicio=" + filterStartDate.getValue() + "&";
            if (filterEndDate.getValue() != null) query += "dataFim=" + filterEndDate.getValue() + "&";
            if (filterCategoryBox.getValue() != null) query += "categoriaId=" + filterCategoryBox.getValue().getId() + "&";

            List<Despesa> list = api.getList(query, new TypeReference<List<Despesa>>(){});
            expensesList.setAll(list);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erro", "Falha ao carregar despesas.");
        }
    }

    @FXML
    public void onAdd() {
        try {
            Despesa d = new Despesa();
            d.setData(datePicker.getValue());
            d.setDescricao(descField.getText());
            d.setValor(Double.parseDouble(valueField.getText()));
            d.setCategoria(categoryBox.getValue());
            d.setMetodoPagamento(paymentBox.getValue());

            api.post("/api/despesas", d, Despesa.class);
            clearFields();
            loadExpenses();
        } catch (NumberFormatException e) {
            showAlert("Erro", "Valor inválido.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erro", "Falha ao criar despesa.");
        }
    }

    @FXML
    public void onDelete() {
        Despesa selected = expensesTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        
        try {
            api.delete("/api/despesas/" + selected.getId());
            loadExpenses();
        } catch (Exception e) {
            showAlert("Erro", "Falha ao remover.");
        }
    }
    
    @FXML
    public void onClearFilters() {
        filterStartDate.setValue(null);
        filterEndDate.setValue(null);
        filterCategoryBox.setValue(null);
        loadExpenses();
    }

    private void clearFields() {
        descField.clear();
        valueField.clear();
        datePicker.setValue(null);
        categoryBox.setValue(null);
        paymentBox.setValue(null);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
}
