package upt.gestaodespesas.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;

@Entity
@Table(name = "despesas")
public class Despesa {
	
	@NotNull(message = "O método de pagamento é obrigatório")
	@Enumerated(EnumType.STRING)
	@Column(name = "metodo_pagamento", nullable = false)
	private MetodoPagamento metodoPagamento;
	
	@ManyToOne
	@JoinColumn(name = "categoria_id", nullable = false)
	private Categoria categoria;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private	 Long id;
	
	@NotBlank
	@Column(nullable = false)
	private String descricao;
	
	@NotNull
	@Positive
	@Column(nullable = false)
	private Double valor;
	
	@NotNull
	@Column(nullable = false)
	private LocalDate data;
	
	public Categoria getCategoria() {
		return categoria;
	}
	
	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}
	
	public MetodoPagamento getMetodoPagamento() {
		return metodoPagamento;
	}
	
	public void setMetodoPagamento(MetodoPagamento metodoPagamento) {
		this.metodoPagamento = metodoPagamento;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getDescricao() {
		return descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public Double getValor() {
		return valor;
	}
	
	public void setValor(Double valor) {
		this.valor = valor;
	}
	
	public LocalDate getData() {
		return data;
		
	}
	
	public void setData(LocalDate data) {
		this.data = data;
	}
}
