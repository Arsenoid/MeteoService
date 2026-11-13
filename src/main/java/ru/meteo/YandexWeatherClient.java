package ru.meteo;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class YandexWeatherClient {

    private static final String BASE_URL = "https://api.weather.yandex.ru/v2/forecast";
    private static final String API_KEY_HEADER = "X-Yandex-Weather-Key";

    private final HttpClient httpClient;
    private final String apiKey;

    public YandexWeatherClient(String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public String fetchForecast(double lat, double lon, int limit) throws IOException, InterruptedException {
        String url = buildUrl(lat, lon, limit);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header(API_KEY_HEADER, apiKey)
                .GET()
                .timeout(Duration.ofSeconds(30))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("API returned status " + response.statusCode() + ": " + response.body());
        }

        return response.body();
    }

    private String buildUrl(double lat, double lon, int limit) {
        return BASE_URL
                + "?lat=" + encode(String.valueOf(lat))
                + "&lon=" + encode(String.valueOf(lon))
                + "&limit=" + encode(String.valueOf(limit))
                + "&hours=false";
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
