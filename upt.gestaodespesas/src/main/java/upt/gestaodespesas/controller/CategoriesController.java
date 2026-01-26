package upt.gestaodespesas.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import upt.gestaodespesas.model.Categoria;
import upt.gestaodespesas.service.ApiService;

import com.fasterxml.jackson.core.type.TypeReference;

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
    }

    private void loadCategories() {
        try {
            List<Categoria> list = api.getList("/api/categorias", new TypeReference<List<Categoria>>(){});
            categoriesList.setAll(list);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erro", "Falha ao carregar categorias.");
        }
    }

    @FXML
    public void onAdd() {
        String name = nameField.getText();
        if (name.isEmpty()) {
            showAlert("Aviso", "Insira um nome.");
            return;
        }

        try {
            Categoria c = new Categoria(name);
            api.post("/api/categorias", c, Categoria.class);
            nameField.clear();
            loadCategories();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erro", "Falha ao criar categoria.");
        }
    }

    @FXML
    public void onDelete() {
        Categoria selected = categoriesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Aviso", "Selecione uma categoria.");
            return;
        }

        try {
            api.delete("/api/categorias/" + selected.getId());
            loadCategories();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erro", "Falha ao apagar categoria. Verifique se tem despesas associadas.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
}
