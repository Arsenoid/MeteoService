package ru.meteo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WeatherResponseParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public int extractCurrentTemp(String json) throws Exception {
        JsonNode root = objectMapper.readTree(json);
        JsonNode temp = root.path("fact").path("temp");
        if (temp.isMissingNode() || !temp.isNumber()) {
            throw new IllegalStateException("Field fact.temp not found in response");
        }
        return temp.asInt();
    }

    public double calculateAverageDayTemp(String json, int limit) throws Exception {
        JsonNode root = objectMapper.readTree(json);
        JsonNode forecasts = root.path("forecasts");

        if (!forecasts.isArray() || forecasts.isEmpty()) {
            throw new IllegalStateException("Field forecasts not found in response");
        }

        double sum = 0;
        int count = 0;
        int daysToProcess = Math.min(limit, forecasts.size());

        for (int i = 0; i < daysToProcess; i++) {
            JsonNode tempAvg = forecasts.get(i).path("parts").path("day").path("temp_avg");
            if (tempAvg.isNumber()) {
                sum += tempAvg.asDouble();
                count++;
            }
        }

        if (count == 0) {
            throw new IllegalStateException("No temp_avg values found in forecasts");
        }

        return sum / count;
    }
}
