package upt.gestaodespesas.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DashboardResponse {
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private double totalPeriodo;
    private double mediaDiaria;
    private MaiorDespesa maiorDespesa;
    private TopCategoria topCategoria;

    public static class MaiorDespesa {
        private Long despesaId;
        private String descricao;
        private double valor;
        private LocalDate data;

        public MaiorDespesa() {}

        public Long getDespesaId() { return despesaId; }
        public void setDespesaId(Long despesaId) { this.despesaId = despesaId; }

        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }

        public double getValor() { return valor; }
        public void setValor(double valor) { this.valor = valor; }

        public LocalDate getData() { return data; }
        public void setData(LocalDate data) { this.data = data; }
    }

    public static class TopCategoria {
        private Long categoriaId;
        private String categoriaNome;
        private double total;

        public TopCategoria() {}

        public Long getCategoriaId() { return categoriaId; }
        public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }

        public String getCategoriaNome() { return categoriaNome; }
        public void setCategoriaNome(String categoriaNome) { this.categoriaNome = categoriaNome; }

        public double getTotal() { return total; }
        public void setTotal(double total) { this.total = total; }
    }

    public DashboardResponse() {}

    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }

    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }

    public double getTotalPeriodo() { return totalPeriodo; }
    public void setTotalPeriodo(double totalPeriodo) { this.totalPeriodo = totalPeriodo; }

    public double getMediaDiaria() { return mediaDiaria; }
    public void setMediaDiaria(double mediaDiaria) { this.mediaDiaria = mediaDiaria; }

    public MaiorDespesa getMaiorDespesa() { return maiorDespesa; }
    public void setMaiorDespesa(MaiorDespesa maiorDespesa) { this.maiorDespesa = maiorDespesa; }

    public TopCategoria getTopCategoria() { return topCategoria; }
    public void setTopCategoria(TopCategoria topCategoria) { this.topCategoria = topCategoria; }
}
