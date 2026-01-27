package upt.gestaodespesas.dto;

import java.time.LocalDate;

import upt.gestaodespesas.entity.MetodoPagamento;

public class DespesaResponse {
    private Long id;
    private LocalDate data;
    private String descricao;
    private double valor;
    private MetodoPagamento metodoPagamento;
    private CategoriaResponse categoria;

    public DespesaResponse() {}

    public DespesaResponse(Long id, LocalDate data, String descricao, double valor,
                          MetodoPagamento metodoPagamento, CategoriaResponse categoria) {
        this.id = id;
        this.data = data;
        this.descricao = descricao;
        this.valor = valor;
        this.metodoPagamento = metodoPagamento;
        this.categoria = categoria;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    public MetodoPagamento getMetodoPagamento() { return metodoPagamento; }
    public void setMetodoPagamento(MetodoPagamento metodoPagamento) { this.metodoPagamento = metodoPagamento; }

    public CategoriaResponse getCategoria() { return categoria; }
    public void setCategoria(CategoriaResponse categoria) { this.categoria = categoria; }
}
