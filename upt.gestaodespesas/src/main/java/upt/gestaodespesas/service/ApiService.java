package upt.gestaodespesas.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

public class ApiService {

    private final HttpClient client;
    private final ObjectMapper mapper;
    private static final String BASE_URL = "http://localhost:8080";
    private static String token; // Static token to share across instances

    public ApiService() {
        this.client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
    }

    public static void setToken(String jwtToken) {
        token = jwtToken;
    }

    private HttpRequest.Builder addAuthHeader(HttpRequest.Builder builder) {
        if (token != null && !token.isEmpty()) {
            builder.header("Authorization", "Bearer " + token);
        }
        return builder;
    }

    public <T> T get(String endpoint, Class<T> responseType) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Accept", "application/json");
        
        addAuthHeader(builder);

        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return mapper.readValue(response.body(), responseType);
        } else {
            throw new IOException("Error: " + response.statusCode() + " - " + response.body());
        }
    }
    
    public <T> List<T> getList(String endpoint, TypeReference<List<T>> responseType) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Accept", "application/json");

        addAuthHeader(builder);

        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return mapper.readValue(response.body(), responseType);
        } else {
            throw new IOException("Error: " + response.statusCode() + " - " + response.body());
        }
    }

    public <T, R> R post(String endpoint, T body, Class<R> responseType) throws IOException, InterruptedException {
        String jsonBody = mapper.writeValueAsString(body);
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json");

        addAuthHeader(builder);

        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            if (responseType == Void.class) return null;
            return mapper.readValue(response.body(), responseType);
        } else {
            throw new IOException("Error: " + response.statusCode() + " - " + response.body());
        }
    }

    public <T, R> R put(String endpoint, T body, Class<R> responseType) throws IOException, InterruptedException {
        String jsonBody = mapper.writeValueAsString(body);
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json");

        addAuthHeader(builder);

        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
             if (responseType == Void.class) return null;
            return mapper.readValue(response.body(), responseType);
        } else {
             throw new IOException("Error: " + response.statusCode() + " - " + response.body());
        }
    }

    public void delete(String endpoint) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(BASE_URL + endpoint));
        
        addAuthHeader(builder);

        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
             throw new IOException("Error: " + response.statusCode() + " - " + response.body());
        }
    }
}
