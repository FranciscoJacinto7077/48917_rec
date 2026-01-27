package upt.gestaodespesas.dto;

import java.time.LocalDate;

import upt.gestaodespesas.entity.MetodoPagamento;
import upt.gestaodespesas.entity.Periodicidade;


public class RecorrenciaResponse {

    private Long id;

    private Long categoriaId;
    private String categoriaNome;
    private String descricao;
    private double valor;
    private MetodoPagamento metodoPagamento;
    private Periodicidade periodicidade;
    private boolean ativa;
    private LocalDate proximaGeracao;

    public RecorrenciaResponse() {}

    public RecorrenciaResponse(Long id, Long categoriaId, String categoriaNome, String descricao, double valor,
                              MetodoPagamento metodoPagamento, Periodicidade periodicidade, boolean ativa,
                              LocalDate proximaGeracao) {
        this.id = id;
        this.categoriaId = categoriaId;
        this.categoriaNome = categoriaNome;
        this.descricao = descricao;
        this.valor = valor;
        this.metodoPagamento = metodoPagamento;
        this.periodicidade = periodicidade;
        this.ativa = ativa;
        this.proximaGeracao = proximaGeracao;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }

    public String getCategoriaNome() { return categoriaNome; }
    public void setCategoriaNome(String categoriaNome) { this.categoriaNome = categoriaNome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    public MetodoPagamento getMetodoPagamento() { return metodoPagamento; }
    public void setMetodoPagamento(MetodoPagamento metodoPagamento) { this.metodoPagamento = metodoPagamento; }

    public Periodicidade getPeriodicidade() { return periodicidade; }
    public void setPeriodicidade(Periodicidade periodicidade) { this.periodicidade = periodicidade; }

    public boolean isAtiva() { return ativa; }
    public void setAtiva(boolean ativa) { this.ativa = ativa; }

    public LocalDate getProximaGeracao() { return proximaGeracao; }
    public void setProximaGeracao(LocalDate proximaGeracao) { this.proximaGeracao = proximaGeracao; }
}
