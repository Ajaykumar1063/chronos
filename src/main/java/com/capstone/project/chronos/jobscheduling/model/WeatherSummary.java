package com.capstone.project.chronos.jobscheduling.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WeatherSummary {
    private LocalDate date;
    private String condition; // Example: "Sunny", "Cloudy", "Rainy"
    private double minTemperature;
    private double maxTemperature;
    private double humidity;
    private double precipitation; // mm of rain/snow

    @Override
    public String toString() {
        return String.format(
                "Date: %s\nCondition: %s\nMin Temp: %.1f°C\nMax Temp: %.1f°C\nHumidity: %.1f%%\nPrecipitation: %.1f mm\n",
                date.toString(),
                condition,
                minTemperature,
                maxTemperature,
                humidity,
                precipitation
        );
    }
}
