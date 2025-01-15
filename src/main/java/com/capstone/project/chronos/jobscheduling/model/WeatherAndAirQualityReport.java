package com.capstone.project.chronos.jobscheduling.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WeatherAndAirQualityReport {

    private String location;
    private List<WeatherSummary> dailyWeatherSummaries;
    private List<AirQualitySummary> dailyAirQualitySummaries;

    public String getSummary(String scheduleType) {
        StringBuilder report = new StringBuilder();

        report.append("Weather and Air Quality Report for ").append(location).append("\n\n");

        if ("DAILY".equalsIgnoreCase(scheduleType)) {
            report.append("Weather:\n").append(dailyWeatherSummaries.get(0).toString()).append("\n");
            report.append("Air Quality:\n").append(dailyAirQualitySummaries.get(0).toString()).append("\n");
        } else {
            report.append("Weekly Weather:\n");
            dailyWeatherSummaries.forEach(summary -> report.append(summary.toString()).append("\n"));

            report.append("\nWeekly Air Quality:\n");
            dailyAirQualitySummaries.forEach(summary -> report.append(summary.toString()).append("\n"));
        }

        return report.toString();
    }
}
