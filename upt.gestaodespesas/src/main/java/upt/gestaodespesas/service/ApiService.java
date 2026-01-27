package upt.gestaodespesas.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import upt.gestaodespesas.model.ApiErrorResponse;

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
    private static String token;

    public ApiService() {
        this.client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());

        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static void setToken(String jwtToken) { token = jwtToken; }
    public static void clearToken() { token = null; }
    public static String getBaseUrl() { return BASE_URL; }

    private HttpRequest.Builder addAuthHeader(HttpRequest.Builder builder) {
        if (token != null && !token.isBlank()) {
            builder.header("Authorization", "Bearer " + token);
        }
        return builder;
    }

    private IOException apiError(int statusCode, String body) {
        try {
            ApiErrorResponse err = mapper.readValue(body, ApiErrorResponse.class);
            String msg = (err.getMessage() != null && !err.getMessage().isBlank()) ? err.getMessage() : body;
            return new IOException("HTTP " + statusCode + " - " + msg);
        } catch (Exception ignore) {
            return new IOException("HTTP " + statusCode + " - " + body);
        }
    }

    public <T> T get(String endpoint, Class<T> responseType) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Accept", "application/json");

        addAuthHeader(builder);

        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            if (responseType == Void.class) return null;
            return mapper.readValue(response.body(), responseType);
        }
        throw apiError(response.statusCode(), response.body());
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
        }
        throw apiError(response.statusCode(), response.body());
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
            if (response.body() == null || response.body().isBlank()) return null;
            return mapper.readValue(response.body(), responseType);
        }
        throw apiError(response.statusCode(), response.body());
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
            if (response.body() == null || response.body().isBlank()) return null;
            return mapper.readValue(response.body(), responseType);
        }
        throw apiError(response.statusCode(), response.body());
    }

    public void delete(String endpoint) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Accept", "application/json");

        addAuthHeader(builder);

        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw apiError(response.statusCode(), response.body());
        }
    }
}
