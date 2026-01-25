package upt.gestaodespesas.dto;

public class UserMeResponse {
    private Long id;
    private String nome;
    private String email;

    public UserMeResponse() {}

    public UserMeResponse(Long id, String nome, String email) {
        this.id = id;
        this.nome = nome;
        this.email = email;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }

    public void setId(Long id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setEmail(String email) { this.email = email; }
}
