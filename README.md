# MeteoService

Консольное Java-приложение для получения прогноза погоды через [API Яндекс.Погоды](https://yandex.ru/dev/weather/). Программа запрашивает прогноз по координатам, парсит JSON-ответ и выводит текущую температуру и среднюю дневную температуру за заданное число суток.

## Возможности

- HTTP-запрос к API Яндекс.Погоды (`java.net.http.HttpClient`)
- Парсинг JSON с помощью Jackson
- Вывод текущей температуры и средней дневной температуры за N дней
- По умолчанию — координаты Москвы (55.75°N, 37.62°E), период прогноза — 7 суток

## Стек

| Компонент | Версия |
|-----------|--------|
| Java | 22 |
| Maven | 3.6+ |
| Jackson Databind | 2.22.4 |

## Структура проекта

```
src/main/java/ru/meteo/
├── MeteoApp.java              — точка входа, координаты и лимит прогноза
├── YandexWeatherClient.java   — HTTP-клиент к API
└── WeatherResponseParser.java — извлечение температуры из JSON
```

## Требования

1. **JDK 22** или новее
2. **Maven**
3. **API-ключ** Яндекс.Погоды — получить на [странице разработчика](https://yandex.ru/dev/weather/doc/ru/concepts/quickstart)

## Настройка API-ключа

Ключ **не хранится в коде**. Приложение читает его из переменной окружения `YANDEX_WEATHER_API_KEY` или из системного свойства `yandex.weather.api.key`.

### Вариант 1: переменная окружения (рекомендуется)

**macOS / Linux:**

```bash
export YANDEX_WEATHER_API_KEY="ваш-ключ-здесь"
```

**Windows (PowerShell):**

```powershell
$env:YANDEX_WEATHER_API_KEY = "ваш-ключ-здесь"
```

### Вариант 2: файл `.env` (локально)

```bash
cp .env.example .env
# отредактируйте .env и подставьте свой ключ
```

Файл `.env` добавлен в `.gitignore` и не попадёт в репозиторий. Перед запуском подгрузите переменные:

```bash
set -a && source .env && set +a   # macOS / Linux
```

### Вариант 3: системное свойство JVM

```bash
mvn exec:java -Dexec.jvmArgs="-Dyandex.weather.api.key=ваш-ключ-здесь"
```

### IntelliJ IDEA

**Run → Edit Configurations → Environment variables** → добавьте `YANDEX_WEATHER_API_KEY` со значением ключа.

## Сборка и запуск

```bash
# Сборка
mvn clean package

# Запуск (ключ должен быть уже в окружении)
export YANDEX_WEATHER_API_KEY="ваш-ключ-здесь"
mvn exec:java

# Или напрямую после сборки
java -cp target/classes:$(mvn -q dependency:build-classpath -Dmdep.outputFile=/dev/stdout) ru.meteo.MeteoApp
```

## Пример вывода

```
{ ... JSON-ответ API ... }

Текущая температура в Москве: 5 °C
Средняя дневная температура за 7 суток: 4.3 °C
```

Точные значения зависят от актуального прогноза.

## Используемый API

- **Эндпоинт:** `GET https://api.weather.yandex.ru/v2/forecast`
- **Параметры:** `lat`, `lon`, `limit`, `hours=false`
- **Заголовок:** `X-Yandex-Weather-Key`

Из ответа используются поля:

- `fact.temp` — текущая температура
- `forecasts[].parts.day.temp_avg` — средняя дневная температура по дням

## Обработка ошибок

При отсутствии API-ключа, сетевых сбоях, неверном ключе или неожиданной структуре ответа приложение выводит сообщение об ошибке в `stderr` и завершается с кодом `1`.

## Автор

Учебный проект, Магистратура МИФИ.
