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
public class AirQualitySummary {
    private LocalDate date;
    private int aqi; // Air Quality Index
    private String airQualityLevel; // Example: "Good", "Moderate", "Unhealthy"
    private double pm10; // Particulate Matter 10 µg/m3
    private double pm2_5; // Particulate Matter 2.5 µg/m3
    private double no2; // Nitrogen Dioxide µg/m3
    private double so2; // Sulfur Dioxide µg/m3
    private double co; // Carbon Monoxide µg/m3
    private double o3; // Ozone µg/m3

    @Override
    public String toString() {
        return String.format(
                "Date: %s\nAQI: %d (%s)\nPM10: %.2f µg/m3\nPM2.5: %.2f µg/m3\nNO2: %.2f µg/m3\nSO2: %.2f µg/m3\nCO: %.2f µg/m3\nO3: %.2f µg/m3\n",
                date.toString(),
                aqi,
                airQualityLevel,
                pm10,
                pm2_5,
                no2,
                so2,
                co,
                o3
        );
    }
}
