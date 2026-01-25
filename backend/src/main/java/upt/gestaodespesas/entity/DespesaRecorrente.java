package upt.gestaodespesas.entity;

import java.time.LocalDate;

import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@Table(name = "despesas_recorrentes")
public class DespesaRecorrente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "utilizador_id", nullable = false)
    private Utilizador utilizador;

    @ManyToOne(optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @NotBlank
    @Column(nullable = false)
    private String descricao;

    @NotNull
    @Positive
    @Column(nullable = false)
    private Double valor;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pagamento", nullable = false)
    private MetodoPagamento metodoPagamento;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Periodicidade periodicidade;

    @Column(nullable = false)
    private boolean ativa = true;

    @NotNull
    @Column(name = "proxima_geracao", nullable = false)
    private LocalDate proximaGeracao;

    public DespesaRecorrente() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Utilizador getUtilizador() { return utilizador; }
    public void setUtilizador(Utilizador utilizador) { this.utilizador = utilizador; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

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
}
