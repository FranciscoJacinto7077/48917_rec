package upt.gestaodespesas.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "categorias", uniqueConstraints = {
		@UniqueConstraint(columnNames = "nome", name = "uk_categoria_nome")
})

public class Categoria {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank
	@Column(nullable = false, unique = true)
	private String nome;
	
	public Categoria() {}
	
	public Long getId() {
		return id;
	
	}
	
	public void setId(Long id) {
		this.id = id;
	
	}
	
	public String getNome() {
		return nome;
		
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
}
