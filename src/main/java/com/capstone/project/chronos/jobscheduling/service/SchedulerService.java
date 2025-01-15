package com.capstone.project.chronos.jobscheduling.service;

import com.capstone.project.chronos.jobscheduling.entity.Jobs;
import com.capstone.project.chronos.jobscheduling.enums.ScheduledType;
import com.capstone.project.chronos.jobscheduling.enums.Status;
import com.capstone.project.chronos.jobscheduling.model.CodingChallenge;
import com.capstone.project.chronos.jobscheduling.model.FitnessAndNutritionAdvice;
import com.capstone.project.chronos.jobscheduling.model.NewsSummaryReport;
import com.capstone.project.chronos.jobscheduling.model.TechSuggestion;
import com.capstone.project.chronos.jobscheduling.model.WeatherAndAirQualityReport;
import com.capstone.project.chronos.jobscheduling.repository.JobsRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Service
public class SchedulerService {

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JobsRepository jobsRepository;

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private NewsSummaryService newsSummaryService;

    @Autowired
    private CodingAndTechService codingAndTechService;

    @Autowired
    private FitnessAndNutritionAdviceService fitnessAndNutritionAdviceService;

    private Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();


    /**
     *
     */
    @PostConstruct
    public void init() {
        List<Jobs> jobs = jobsRepository.findAll();
        for (Jobs job : jobs) {
            if (job.getStatus().equals(Status.PENDING)) { // Optional: Schedule only active jobs
                initiateJob(job);
            }
        }
    }

    /**
     *
     * Schedules a job based on whether it's recurring or not.
     * @param job The job object that holds the reminder details.
     */
    public void initiateJob(Jobs job) {
        if (scheduledTasks.containsKey(job.getId())) {
            cancelJob(job.getId()); // Cancel if already scheduled
        }
        if (job.getScheduleType().equals(ScheduledType.RECURRING)) {
            scheduleRecurringJob(job);
        } else if (job.getScheduleType().equals(ScheduledType.ONCE)) {
            scheduleOneTimeJob(job);
        }
    }

    // Cancels a scheduled job
    public void cancelJob(Long jobId) {
        ScheduledFuture<?> future = scheduledTasks.remove(jobId);
        if (future != null) {
            future.cancel(false); // Cancel the task
        }
    }

    /**
     * Schedules a one-time job based on the specified schedule time.
     * @param job The job object to be scheduled.
     */
    private void scheduleOneTimeJob(Jobs job) {
        Duration delay = Duration.between(LocalDateTime.now(),job.getCronExpressionDate());
        if (delay.isNegative()) return; // Skip past jobs
        Runnable task = () -> executeJob(job);
        ScheduledFuture<?> future = taskScheduler.schedule(
                task, Instant.now().plusMillis(delay.toMillis())
        );
        scheduledTasks.put(job.getId(), future);
    }

    /**
     * Schedules a recurring job based on the specified recurrence interval.
     * @param job The job object to be scheduled.
     */
    private void scheduleRecurringJob(Jobs job) {
        String cronExpression = this.convertToCronExpression(job.getRecurringType());
        Runnable task = () -> executeJob(job);
        ScheduledFuture<?> future = taskScheduler.schedule(task, new CronTrigger(cronExpression));
        scheduledTasks.put(job.getId(), future);
        job.setUpdatedAt(LocalDate.now());
        job.setStatus(Status.RUNNING);
        jobsRepository.save(job);
    }

    /**
     *
     * @param job
     */
    private void executeJob(Jobs job) {
        log.info("Executing job: {}", job.getId());
        try {
            switch (job.getJobType().toUpperCase()) {
               case "EMAIL REMINDER":
                   emailService.sendReminder(job);
                   break;
               case "WEATHER AND AIR QUALITY REPORT":
                   List<WeatherAndAirQualityReport> reportList = new ArrayList<>();
                   List<String> locations = List.of("Hyderabad", "Chennai", "Bangalore", "Mumbai", "Delhi","Kochi","Kolkata","Ahmedabad","Jaipur");
                   for (String location : locations) {
                       weatherService.fetchWeatherAndAirQualityData(location, reportList);
                   }
                   // Send the email with the summaries
                   emailService.sendReminderForWeatherAndAirQuality(job, reportList);
                   break;
               case "NEWS SUMMARY REPORT":
                // Fetch news summaries
                List<NewsSummaryReport> indiaNews = newsSummaryService.fetchIndiaNews();
                List<NewsSummaryReport> worldNews = newsSummaryService.fetchWorldNews();

                // Send the email
                emailService.sendNewsSummaryEmail(job, indiaNews, worldNews);
                break;
                case "CODING CHALLENGE AND TECH SUGGESTION":
                    List<String> tags = Arrays.asList(
                            "AI",
                            "Machine Learning",
                            "Cloud Computing",
                            "DevOps",
                            "Web Development",
                            "Cybersecurity",
                            "Data Science",
                            "Blockchain",
                            "Programming Practices",
                            "Microservices"
                    );
                    Random random = new Random();
                    CodingChallenge challenge = codingAndTechService.fetchDailyLeetCodeChallenge();
                    TechSuggestion techSuggestion = codingAndTechService.fetchDailyMediumArticle(tags.get(random.nextInt(tags.size())));

                    emailService.sendCodingAndTechEmail(job, challenge, techSuggestion);
                    break;
                  case "FITNESS AND NUTRITION ADVICE":
                      FitnessAndNutritionAdvice fitnessAndNutritionAdvice = fitnessAndNutritionAdviceService.getDailyAdvice();
                      emailService.sendNutritionAndFitnessEmail(fitnessAndNutritionAdvice, job);
                      break;
                default:
                    throw new IllegalArgumentException("Invalid job type: " + job.getJobType());
            }
            job.setStatus(Status.COMPLETED);
        } catch (Exception e) {
            log.error("Error executing job {}: {}", job.getId(), e.getMessage());
            job.setStatus(Status.FAILED);
        }
        job.setUpdatedAt(LocalDate.now());
        jobsRepository.save(job);
    }




    /**
     * Calculates the interval in milliseconds based on the recurrence interval.
     * @param interval The recurrence interval (e.g., DAILY, WEEKLY, MONTHLY).
     * @return The interval in milliseconds.
     */
    private String convertToCronExpression(String interval) {
        switch (interval.toUpperCase()) {
            case "DAILY":
                return "0 9 0 * * *"; // Every day at 9 AM
            case "WEEKLY":
                return "0 9 0 * * MON"; // Every Monday at 9 AM
            case "MONTHLY":
                return "0 9 1 * * *"; // First day of every month at 9 AM
            default:
                throw new IllegalArgumentException("Invalid recurrence interval: " + interval);
        }
    }



}
