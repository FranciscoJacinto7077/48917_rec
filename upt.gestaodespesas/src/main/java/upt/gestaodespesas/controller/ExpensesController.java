package upt.gestaodespesas.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import upt.gestaodespesas.model.Categoria;
import upt.gestaodespesas.model.Despesa;
import upt.gestaodespesas.model.DespesaRequest;
import upt.gestaodespesas.model.MetodoPagamento;
import upt.gestaodespesas.service.ApiService;

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
    @FXML private TextField filterMinValue;
    @FXML private TextField filterMaxValue;

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

        // Extra UX: duplo clique para editar
        expensesTable.setRowFactory(tv -> {
            TableRow<Despesa> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    onEdit();
                }
            });
            return row;
        });
    }

    private void loadCategories() {
        try {
            List<Categoria> list = api.getList("/api/categorias", new TypeReference<List<Categoria>>(){});
            categoriesList.setAll(list);
            categoryBox.setItems(categoriesList);
            filterCategoryBox.setItems(categoriesList);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erro", "Falha ao carregar categorias.");
        }
    }

    @FXML
    public void loadExpenses() {
        try {
            StringBuilder query = new StringBuilder("/api/despesas?");

            if (filterStartDate.getValue() != null) {
                query.append("dataInicio=").append(filterStartDate.getValue()).append("&");
            }
            if (filterEndDate.getValue() != null) {
                query.append("dataFim=").append(filterEndDate.getValue()).append("&");
            }
            if (filterCategoryBox.getValue() != null) {
                query.append("categoriaId=").append(filterCategoryBox.getValue().getId()).append("&");
            }

            Double min = parseDoubleOrNull(filterMinValue.getText());
            Double max = parseDoubleOrNull(filterMaxValue.getText());

            if ((min == null) != (max == null)) {
                showAlert(Alert.AlertType.WARNING, "Aviso", "Indica valor mínimo e máximo (ou nenhum)." );
                return;
            }
            if (min != null && max != null) {
                if (min > max) {
                    showAlert(Alert.AlertType.WARNING, "Aviso", "valorMin não pode ser maior que valorMax." );
                    return;
                }
                query.append("valorMin=").append(min).append("&");
                query.append("valorMax=").append(max).append("&");
            }

            List<Despesa> list = api.getList(query.toString(), new TypeReference<List<Despesa>>(){});
            expensesList.setAll(list);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erro", normalizeApiErrorMessage(e, "Falha ao carregar despesas."));
        }
    }

    @FXML
    public void onAdd() {
        try {
            if (datePicker.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Aviso", "A data é obrigatória.");
                return;
            }
            if (descField.getText() == null || descField.getText().trim().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Aviso", "A descrição é obrigatória.");
                return;
            }
            if (categoryBox.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Aviso", "A categoria é obrigatória.");
                return;
            }
            if (paymentBox.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Aviso", "O método de pagamento é obrigatório.");
                return;
            }

            Double valor = parseDoubleOrNull(valueField.getText());
            if (valor == null || valor <= 0) {
                showAlert(Alert.AlertType.WARNING, "Aviso", "Valor inválido (tem de ser > 0)." );
                return;
            }

            DespesaRequest req = new DespesaRequest();
            req.setData(datePicker.getValue());
            req.setDescricao(descField.getText().trim());
            req.setValor(valor);
            req.setMetodoPagamento(paymentBox.getValue());
            req.setCategoriaId(categoryBox.getValue().getId());

            api.post("/api/despesas", req, Despesa.class);
            clearFields();
            loadExpenses();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erro", normalizeApiErrorMessage(e, "Falha ao criar despesa."));
        }
    }

    @FXML
    public void onEdit() {
        Despesa selected = expensesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.INFORMATION, "Aviso", "Selecione uma despesa.");
            return;
        }

        Dialog<Pair<DespesaRequest, Long>> dialog = new Dialog<>();
        dialog.setTitle("Editar Despesa");
        dialog.setHeaderText("Atualizar os dados da despesa selecionada");

        ButtonType btnSave = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnSave, ButtonType.CANCEL);

        DatePicker dp = new DatePicker(selected.getData());
        TextField tfDesc = new TextField(selected.getDescricao());
        TextField tfVal = new TextField(String.valueOf(selected.getValor()));

        ComboBox<Categoria> cbCat = new ComboBox<>(categoriesList);
        cbCat.setValue(selected.getCategoria());

        ComboBox<MetodoPagamento> cbPay = new ComboBox<>(FXCollections.observableArrayList(MetodoPagamento.values()));
        cbPay.setValue(selected.getMetodoPagamento());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Data:"), 0, 0);
        grid.add(dp, 1, 0);
        grid.add(new Label("Descrição:"), 0, 1);
        grid.add(tfDesc, 1, 1);
        grid.add(new Label("Valor:"), 0, 2);
        grid.add(tfVal, 1, 2);
        grid.add(new Label("Categoria:"), 0, 3);
        grid.add(cbCat, 1, 3);
        grid.add(new Label("Método Pagamento:"), 0, 4);
        grid.add(cbPay, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnSave) {
                Double v = parseDoubleOrNull(tfVal.getText());
                if (dp.getValue() == null || tfDesc.getText().trim().isEmpty() || v == null || v <= 0
                        || cbCat.getValue() == null || cbPay.getValue() == null) {
                    showAlert(Alert.AlertType.WARNING, "Aviso", "Preenche todos os campos corretamente." );
                    return null;
                }

                DespesaRequest req = new DespesaRequest();
                req.setData(dp.getValue());
                req.setDescricao(tfDesc.getText().trim());
                req.setValor(v);
                req.setCategoriaId(cbCat.getValue().getId());
                req.setMetodoPagamento(cbPay.getValue());

                return new Pair<>(req, selected.getId());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            try {
                api.put("/api/despesas/" + result.getValue(), result.getKey(), Despesa.class);
                loadExpenses();
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erro", normalizeApiErrorMessage(e, "Falha ao atualizar despesa."));
            }
        });
    }

    @FXML
    public void onDelete() {
        Despesa selected = expensesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.INFORMATION, "Aviso", "Selecione uma despesa.");
            return;
        }

        try {
            api.delete("/api/despesas/" + selected.getId());
            loadExpenses();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erro", normalizeApiErrorMessage(e, "Falha ao remover."));
        }
    }

    @FXML
    public void onClearFilters() {
        filterStartDate.setValue(null);
        filterEndDate.setValue(null);
        filterCategoryBox.setValue(null);
        filterMinValue.clear();
        filterMaxValue.clear();
        loadExpenses();
    }

    private void clearFields() {
        descField.clear();
        valueField.clear();
        datePicker.setValue(null);
        categoryBox.setValue(null);
        paymentBox.setValue(null);
    }

    private Double parseDoubleOrNull(String text) {
        if (text == null) return null;
        String t = text.trim();
        if (t.isEmpty()) return null;
        try {
            return Double.parseDouble(t.replace(",", "."));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String normalizeApiErrorMessage(Exception e, String fallback) {
        String msg = e.getMessage();
        if (msg == null || msg.isBlank()) return fallback;
        // mensagem vem tipicamente como "HTTP xxx - ..."
        int idx = msg.indexOf(" - ");
        return (idx >= 0) ? msg.substring(idx + 3) : msg;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();
    }
}
