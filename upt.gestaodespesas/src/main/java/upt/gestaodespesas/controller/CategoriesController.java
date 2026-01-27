package upt.gestaodespesas.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Pair;
import upt.gestaodespesas.model.Categoria;
import upt.gestaodespesas.service.ApiService;

import java.util.List;

public class CategoriesController {

    @FXML private TableView<Categoria> categoriesTable;
    @FXML private TableColumn<Categoria, Long> idColumn;
    @FXML private TableColumn<Categoria, String> nameColumn;
    @FXML private TextField nameField;

    private final ApiService api = new ApiService();
    private final ObservableList<Categoria> categoriesList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nome"));

        categoriesTable.setItems(categoriesList);
        loadCategories();

        // Extra UX: duplo clique para editar
        categoriesTable.setRowFactory(tv -> {
            TableRow<Categoria> row = new TableRow<>();
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
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erro", normalizeApiErrorMessage(e, "Falha ao carregar categorias."));
        }
    }

    @FXML
    public void onAdd() {
        String name = (nameField.getText() == null) ? "" : nameField.getText().trim();
        if (name.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Aviso", "Insira um nome.");
            return;
        }

        try {
            Categoria c = new Categoria(name);
            api.post("/api/categorias", c, Categoria.class);
            nameField.clear();
            loadCategories();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erro", normalizeApiErrorMessage(e, "Falha ao criar categoria."));
        }
    }

    @FXML
    public void onEdit() {
        Categoria selected = categoriesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.INFORMATION, "Aviso", "Selecione uma categoria.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(selected.getNome());
        dialog.setTitle("Editar Categoria");
        dialog.setHeaderText(null);
        dialog.setContentText("Novo nome:");

        dialog.showAndWait().ifPresent(newName -> {
            String nome = (newName == null) ? "" : newName.trim();
            if (nome.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Aviso", "O nome é obrigatório.");
                return;
            }

            try {
                Categoria body = new Categoria(nome);
                api.put("/api/categorias/" + selected.getId(), body, Categoria.class);
                loadCategories();
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erro", normalizeApiErrorMessage(e, "Falha ao editar categoria."));
            }
        });
    }

    @FXML
    public void onDelete() {
        Categoria selected = categoriesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.INFORMATION, "Aviso", "Selecione uma categoria.");
            return;
        }

        try {
            api.delete("/api/categorias/" + selected.getId());
            loadCategories();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erro", normalizeApiErrorMessage(e, "Falha ao apagar categoria. Verifica se tem despesas associadas."));
        }
    }

    private String normalizeApiErrorMessage(Exception e, String fallback) {
        String msg = e.getMessage();
        if (msg == null || msg.isBlank()) return fallback;
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
