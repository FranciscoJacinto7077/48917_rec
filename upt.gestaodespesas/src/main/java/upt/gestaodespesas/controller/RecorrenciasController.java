package upt.gestaodespesas.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import upt.gestaodespesas.model.*;
import upt.gestaodespesas.service.ApiService;

import java.time.LocalDate;
import java.util.List;

public class RecorrenciasController {

    @FXML private TableView<Recorrencia> table;
    @FXML private TableColumn<Recorrencia, Long> idColumn;
    @FXML private TableColumn<Recorrencia, String> descColumn;
    @FXML private TableColumn<Recorrencia, Double> valueColumn;
    @FXML private TableColumn<Recorrencia, MetodoPagamento> payColumn;
    @FXML private TableColumn<Recorrencia, Periodicidade> periodColumn;
    @FXML private TableColumn<Recorrencia, Boolean> activeColumn;
    @FXML private TableColumn<Recorrencia, LocalDate> nextColumn;

    @FXML private TextField descField;
    @FXML private TextField valueField;
    @FXML private ComboBox<Categoria> categoryBox;
    @FXML private ComboBox<MetodoPagamento> paymentBox;
    @FXML private ComboBox<Periodicidade> periodicityBox;
    @FXML private CheckBox activeBox;
    @FXML private DatePicker nextDatePicker;

    private final ApiService api = new ApiService();
    private final ObservableList<Recorrencia> data = FXCollections.observableArrayList();
    private final ObservableList<Categoria> categories = FXCollections.observableArrayList();

    private MainController mainController;
    public void setMainController(MainController mainController) { this.mainController = mainController; }

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        descColumn.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("valor"));
        payColumn.setCellValueFactory(new PropertyValueFactory<>("metodoPagamento"));
        periodColumn.setCellValueFactory(new PropertyValueFactory<>("periodicidade"));
        activeColumn.setCellValueFactory(new PropertyValueFactory<>("ativa"));
        nextColumn.setCellValueFactory(new PropertyValueFactory<>("proximaGeracao"));

        table.setItems(data);

        paymentBox.setItems(FXCollections.observableArrayList(MetodoPagamento.values()));
        periodicityBox.setItems(FXCollections.observableArrayList(Periodicidade.values()));
        activeBox.setSelected(true);

        loadCategories();
        loadRecorrencias();

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldV, sel) -> {
            if (sel != null) fillForm(sel);
        });
    }

    private void loadCategories() {
        try {
            List<Categoria> list = api.getList("/api/categorias", new TypeReference<List<Categoria>>(){});
            categories.setAll(list);
            categoryBox.setItems(categories);
        } catch (Exception e) {
            e.printStackTrace();
            alert(Alert.AlertType.ERROR, "Erro", normalize(e, "Falha ao carregar categorias."));
        }
    }

    private void loadRecorrencias() {
        try {
            List<Recorrencia> list = api.getList("/api/recorrencias", new TypeReference<List<Recorrencia>>(){});
            data.setAll(list);
        } catch (Exception e) {
            e.printStackTrace();
            alert(Alert.AlertType.ERROR, "Erro", normalize(e, "Falha ao carregar recorrências."));
        }
    }

    @FXML
    public void onAdd() {
        try {
            RecorrenciaRequest req = buildRequestFromForm();
            if (req == null) return;

            api.post("/api/recorrencias", req, Recorrencia.class);
            clearForm();
            loadRecorrencias();
        } catch (Exception e) {
            e.printStackTrace();
            alert(Alert.AlertType.ERROR, "Erro", normalize(e, "Falha ao criar recorrência."));
        }
    }

    @FXML
    public void onUpdate() {
        Recorrencia sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) {
            alert(Alert.AlertType.INFORMATION, "Aviso", "Seleciona uma recorrência.");
            return;
        }

        try {
            RecorrenciaRequest req = buildRequestFromForm();
            if (req == null) return;

            api.put("/api/recorrencias/" + sel.getId(), req, Recorrencia.class);
            loadRecorrencias();
        } catch (Exception e) {
            e.printStackTrace();
            alert(Alert.AlertType.ERROR, "Erro", normalize(e, "Falha ao atualizar recorrência."));
        }
    }

    @FXML
    public void onDelete() {
        Recorrencia sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) {
            alert(Alert.AlertType.INFORMATION, "Aviso", "Seleciona uma recorrência.");
            return;
        }

        try {
            api.delete("/api/recorrencias/" + sel.getId());
            clearForm();
            loadRecorrencias();
        } catch (Exception e) {
            e.printStackTrace();
            alert(Alert.AlertType.ERROR, "Erro", normalize(e, "Falha ao apagar recorrência."));
        }
    }

    @FXML
    public void onClear() {
        clearForm();
        table.getSelectionModel().clearSelection();
    }

    private RecorrenciaRequest buildRequestFromForm() {
        String desc = descField.getText() == null ? "" : descField.getText().trim();
        Double v = parseDouble(valueField.getText());

        if (desc.isEmpty()) { alert(Alert.AlertType.WARNING, "Aviso", "Descrição é obrigatória."); return null; }
        if (v == null || v <= 0) { alert(Alert.AlertType.WARNING, "Aviso", "Valor tem de ser > 0."); return null; }
        if (categoryBox.getValue() == null) { alert(Alert.AlertType.WARNING, "Aviso", "Categoria é obrigatória."); return null; }
        if (paymentBox.getValue() == null) { alert(Alert.AlertType.WARNING, "Aviso", "Método de pagamento é obrigatório."); return null; }
        if (periodicityBox.getValue() == null) { alert(Alert.AlertType.WARNING, "Aviso", "Periodicidade é obrigatória."); return null; }
        if (nextDatePicker.getValue() == null) { alert(Alert.AlertType.WARNING, "Aviso", "Próxima geração é obrigatória."); return null; }

        RecorrenciaRequest req = new RecorrenciaRequest();
        req.setDescricao(desc);
        req.setValor(v);
        req.setCategoriaId(categoryBox.getValue().getId());
        req.setMetodoPagamento(paymentBox.getValue());
        req.setPeriodicidade(periodicityBox.getValue());
        req.setAtiva(activeBox.isSelected());
        req.setProximaGeracao(nextDatePicker.getValue());
        return req;
    }

    private void fillForm(Recorrencia r) {
        descField.setText(r.getDescricao());
        valueField.setText(r.getValor() == null ? "" : String.valueOf(r.getValor()));
        paymentBox.setValue(r.getMetodoPagamento());
        periodicityBox.setValue(r.getPeriodicidade());
        activeBox.setSelected(r.isAtiva());
        nextDatePicker.setValue(r.getProximaGeracao());

        // categoria pode vir null se backend ainda expõe entity "diferente"
        if (r.getCategoria() != null) {
            categoryBox.setValue(r.getCategoria());
        }
    }

    private void clearForm() {
        descField.clear();
        valueField.clear();
        categoryBox.setValue(null);
        paymentBox.setValue(null);
        periodicityBox.setValue(null);
        activeBox.setSelected(true);
        nextDatePicker.setValue(null);
    }

    private Double parseDouble(String s) {
        if (s == null) return null;
        String t = s.trim();
        if (t.isEmpty()) return null;
        try { return Double.parseDouble(t.replace(",", ".")); }
        catch (Exception e) { return null; }
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
