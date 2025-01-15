package com.capstone.project.chronos.jobscheduling.service;

import com.capstone.project.chronos.jobscheduling.model.AirQualitySummary;
import com.capstone.project.chronos.jobscheduling.model.WeatherAndAirQualityReport;
import com.capstone.project.chronos.jobscheduling.model.WeatherSummary;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class WeatherService {

    @Value("${weather.api.key}")
    private String weatherApiKey;

    @Value("${airQuality.api.key}")
    private String airQualityApiKey;

    /**
     *
     * @param location
     * @param reportList
     * @return
     * @throws JSONException
     */
   public List<WeatherAndAirQualityReport> fetchWeatherAndAirQualityData(String location, List<WeatherAndAirQualityReport> reportList) throws JSONException {
        JSONObject weatherJson = fetchWeatherData(location);
        JSONObject airQualityJson = fetchAirQualityData(location);

        WeatherAndAirQualityReport report = new WeatherAndAirQualityReport();
        report.setLocation(location);
        report.setDailyWeatherSummaries(parseWeatherData(weatherJson));
        report.setDailyAirQualitySummaries(parseAirQualityData(airQualityJson));

        reportList.add(report);
        return reportList;
    }


    /**
     *
     * @param location
     * @return
     */
    private JSONObject fetchWeatherData(String location) {
        String apiKey = weatherApiKey;
        String weatherApiUrl = String.format(
                "https://api.openweathermap.org/data/2.5/forecast?q=%s&units=metric&appid=%s",
                location, apiKey
        );

        try {
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(weatherApiUrl, String.class);
            return new JSONObject(response);
        } catch (Exception e) {
            log.error("Failed to fetch weather data for location: {}", location, e);
            throw new RuntimeException("Error fetching weather data.");
        }
    }


    /**
     *
     * @param location
     * @return
     */
    private JSONObject fetchAirQualityData(String location) {
        String apiKey = airQualityApiKey;
        String geoCodeApiUrl = String.format(
                "https://api.openweathermap.org/geo/1.0/direct?q=%s&limit=1&appid=%s",
                location, apiKey
        );

        try {
            RestTemplate restTemplate = new RestTemplate();
            String geoResponse = restTemplate.getForObject(geoCodeApiUrl, String.class);
            JSONArray geoData = new JSONArray(geoResponse);
            if (geoData.length() == 0) {
                throw new RuntimeException("Invalid location provided.");
            }

            double lat = geoData.getJSONObject(0).getDouble("lat");
            double lon = geoData.getJSONObject(0).getDouble("lon");

            String airQualityApiUrl = String.format(
                    "https://api.openweathermap.org/data/2.5/air_pollution/forecast?lat=%f&lon=%f&appid=%s",
                    lat, lon, apiKey
            );

            String airQualityResponse = restTemplate.getForObject(airQualityApiUrl, String.class);
            return new JSONObject(airQualityResponse);
        } catch (Exception e) {
            log.error("Failed to fetch air quality data for location: {}", location, e);
            throw new RuntimeException("Error fetching air quality data.");
        }
    }


    /**
     *
     * @param airQualityJson
     * @return
     * @throws JSONException
     */
    private List<AirQualitySummary> parseAirQualityData(JSONObject airQualityJson) throws JSONException {
        List<AirQualitySummary> summaries = new ArrayList<>();
        JSONArray dailyData = airQualityJson.getJSONArray("list");

        for (int i = 0; i < dailyData.length(); i++) {
            JSONObject entry = dailyData.getJSONObject(i);
            LocalDate date = Instant.ofEpochSecond(entry.getLong("dt"))
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            JSONObject components = entry.getJSONObject("components");

            AirQualitySummary summary = new AirQualitySummary();
            summary.setDate(date);
            summary.setAqi(entry.getInt("main_aqi"));
            summary.setAirQualityLevel(getAirQualityLevel(entry.getInt("main_aqi")));
            summary.setPm10(components.getDouble("pm10"));
            summary.setPm2_5(components.getDouble("pm2_5"));
            summary.setNo2(components.getDouble("no2"));
            summary.setSo2(components.getDouble("so2"));
            summary.setCo(components.getDouble("co"));
            summary.setO3(components.getDouble("o3"));

            summaries.add(summary);
        }

        return summaries;
    }

    /**
     *
     * @param aqi
     * @return
     */
    private String getAirQualityLevel(int aqi) {
        if (aqi <= 50) return "Good";
        else if (aqi <= 100) return "Moderate";
        else if (aqi <= 150) return "Unhealthy for Sensitive Groups";
        else if (aqi <= 200) return "Unhealthy";
        else if (aqi <= 300) return "Very Unhealthy";
        else return "No Data";
    }

    /**
     *
     * @param weatherJson
     * @return
     * @throws JSONException
     */
    private List<WeatherSummary> parseWeatherData(JSONObject weatherJson) throws JSONException {
        List<WeatherSummary> summaries = new ArrayList<>();
        JSONArray dailyData = weatherJson.getJSONArray("list");

        for (int i = 0; i < dailyData.length(); i += 8) { // 8 entries per day in a 3-hour interval forecast
            JSONObject entry = dailyData.getJSONObject(i);
            LocalDate date = Instant.ofEpochSecond(entry.getLong("dt"))
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            JSONObject main = entry.getJSONObject("main");
            JSONArray weatherArray = entry.getJSONArray("weather");
            JSONObject weather = weatherArray.getJSONObject(0);

            WeatherSummary summary = new WeatherSummary();
            summary.setDate(date);
            summary.setCondition(weather.getString("description"));
            summary.setMinTemperature(main.getDouble("temp_min"));
            summary.setMaxTemperature(main.getDouble("temp_max"));
            summary.setHumidity(main.getDouble("humidity"));
            summary.setPrecipitation(entry.has("rain") ? entry.getJSONObject("rain").optDouble("3h", 0) : 0.0);

            summaries.add(summary);
        }

        return summaries;
    }




}
