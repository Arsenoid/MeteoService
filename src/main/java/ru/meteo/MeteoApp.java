package ru.meteo;

public class MeteoApp {

    private static final String API_KEY_ENV = "YANDEX_WEATHER_API_KEY";
    private static final String API_KEY_PROPERTY = "yandex.weather.api.key";
    private static final double DEFAULT_LAT = 55.75;
    private static final double DEFAULT_LON = 37.62;
    private static final int DEFAULT_LIMIT = 7;

    public static void main(String[] args) {
        try {
            YandexWeatherClient client = new YandexWeatherClient(resolveApiKey());
            WeatherResponseParser parser = new WeatherResponseParser();

            String json = client.fetchForecast(DEFAULT_LAT, DEFAULT_LON, DEFAULT_LIMIT);

            System.out.println();
            System.out.println(json);

            int currentTemp = parser.extractCurrentTemp(json);
            double avgTemp = parser.calculateAverageDayTemp(json, DEFAULT_LIMIT);

            System.out.println("Текущая температура в Москве: " + currentTemp + " °C");
            System.out.printf("Средняя дневная температура за %d суток: %.1f °C%n", DEFAULT_LIMIT, avgTemp);
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
            System.exit(1);
        }
    }

    private static String resolveApiKey() {
        String key = System.getenv(API_KEY_ENV);
        if (key == null || key.isBlank()) {
            key = System.getProperty(API_KEY_PROPERTY);
        }
        if (key == null || key.isBlank()) {
            throw new IllegalStateException(
                    "API-ключ не задан. Установите переменную окружения "
                            + API_KEY_ENV
                            + " или системное свойство "
                            + API_KEY_PROPERTY
            );
        }
        return key.trim();
    }
}
