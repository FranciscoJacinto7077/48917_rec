package upt.gestaodespesas.dto.analytics;

public class TotalPorCategoriaItem {
    private Long categoriaId;
    private String categoriaNome;
    private double total;

    public TotalPorCategoriaItem() {}

    public TotalPorCategoriaItem(Long categoriaId, String categoriaNome, double total) {
        this.categoriaId = categoriaId;
        this.categoriaNome = categoriaNome;
        this.total = total;
    }

    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }

    public String getCategoriaNome() { return categoriaNome; }
    public void setCategoriaNome(String categoriaNome) { this.categoriaNome = categoriaNome; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}
