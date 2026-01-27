package upt.gestaodespesas.model;

import java.time.LocalDate;

public class Recorrencia {
    private Long id;
    private String descricao;
    private Double valor;
    private MetodoPagamento metodoPagamento;
    private Periodicidade periodicidade;
    private boolean ativa;
    private LocalDate proximaGeracao;
    private Categoria categoria;

    public Recorrencia() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Double getValor() { return valor; }
    public void setValor(Double valor) { this.valor = valor; }

    public MetodoPagamento getMetodoPagamento() { return metodoPagamento; }
    public void setMetodoPagamento(MetodoPagamento metodoPagamento) { this.metodoPagamento = metodoPagamento; }

    public Periodicidade getPeriodicidade() { return periodicidade; }
    public void setPeriodicidade(Periodicidade periodicidade) { this.periodicidade = periodicidade; }

    public boolean isAtiva() { return ativa; }
    public void setAtiva(boolean ativa) { this.ativa = ativa; }

    public LocalDate getProximaGeracao() { return proximaGeracao; }
    public void setProximaGeracao(LocalDate proximaGeracao) { this.proximaGeracao = proximaGeracao; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
}
