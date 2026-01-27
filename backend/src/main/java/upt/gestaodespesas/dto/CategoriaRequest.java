package upt.gestaodespesas.dto;

import javax.validation.constraints.NotBlank;

public class CategoriaRequest {

    @NotBlank(message = "O nome da categoria é obrigatório")
    private String nome;

    public CategoriaRequest() {}

    public CategoriaRequest(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
