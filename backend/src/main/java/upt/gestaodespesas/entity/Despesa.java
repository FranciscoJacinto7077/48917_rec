package upt.gestaodespesas.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@Entity
@Table(name = "despesas",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_despesa_recorrente_data", columnNames = {"despesa_recorrente_id", "data"})
       }
)
public class Despesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "A data é obrigatória")
    @Column(nullable = false)
    private LocalDate data;

    @NotBlank(message = "A descrição é obrigatória")
    @Column(nullable = false)
    private String descricao;

    @Positive(message = "O valor tem de ser positivo")
    @Column(nullable = false)
    private double valor;

    @NotNull(message = "O método de pagamento é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pagamento", nullable = false)
    private MetodoPagamento metodoPagamento;

    @NotNull(message = "A categoria é obrigatória")
    @ManyToOne(optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "utilizador_id", nullable = false)
    private Utilizador utilizador;

    @ManyToOne(optional = true)
    @JoinColumn(name = "despesa_recorrente_id")
    private DespesaRecorrente origemRecorrente;

    public Despesa() {}

    public Long getId() { return id; }
    public LocalDate getData() { return data; }
    public String getDescricao() { return descricao; }
    public double getValor() { return valor; }
    public MetodoPagamento getMetodoPagamento() { return metodoPagamento; }
    public Categoria getCategoria() { return categoria; }
    public Utilizador getUtilizador() { return utilizador; }
    public DespesaRecorrente getOrigemRecorrente() { return origemRecorrente; }

    public void setId(Long id) { this.id = id; }
    public void setData(LocalDate data) { this.data = data; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setValor(double valor) { this.valor = valor; }
    public void setMetodoPagamento(MetodoPagamento metodoPagamento) { this.metodoPagamento = metodoPagamento; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
    public void setUtilizador(Utilizador utilizador) { this.utilizador = utilizador; }
    public void setOrigemRecorrente(DespesaRecorrente origemRecorrente) { this.origemRecorrente = origemRecorrente; }
}
