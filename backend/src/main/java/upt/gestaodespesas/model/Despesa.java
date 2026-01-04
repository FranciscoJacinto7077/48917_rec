package upt.gestaodespesas.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "despesas")
public class Despesa {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private	 Long id;
	
	@Column(nullable = false)
	private String descricao;
	
	@Column(nullable = false)
	private Double valor;
	
	@Column(nullable = false)
	private LocalDate data;
	
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
