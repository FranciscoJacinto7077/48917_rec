package upt.gestaodespesas.dto;

import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import upt.gestaodespesas.entity.MetodoPagamento;
import upt.gestaodespesas.entity.Periodicidade;

public class RecorrenciaRequest {

    @NotNull(message = "A categoriaId é obrigatória")
    private Long categoriaId;

    @NotBlank(message = "A descrição é obrigatória")
    private String descricao;

    @Positive(message = "O valor tem de ser positivo")
    private double valor;

    @NotNull(message = "O método de pagamento é obrigatório")
    private MetodoPagamento metodoPagamento;

    @NotNull(message = "A periodicidade é obrigatória")
    private Periodicidade periodicidade;

    private boolean ativa = true;

    @NotNull(message = "A proximaGeracao é obrigatória")
    private LocalDate proximaGeracao;

    public RecorrenciaRequest() {}

    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }

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
