package upt.gestaodespesas.dto;

public class AuthTokenResponse {

    private String token;
    private String type = "Bearer";

    public AuthTokenResponse() {}

    public AuthTokenResponse(String token) {
        this.token = token;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
