package com.capstone.project.chronos.jobscheduling.service;

import com.capstone.project.chronos.jobscheduling.entity.Jobs;
import com.capstone.project.chronos.jobscheduling.model.CodingChallenge;
import com.capstone.project.chronos.jobscheduling.model.FitnessAndNutritionAdvice;
import com.capstone.project.chronos.jobscheduling.model.NewsSummaryReport;
import com.capstone.project.chronos.jobscheduling.model.TechSuggestion;
import com.capstone.project.chronos.jobscheduling.model.WeatherAndAirQualityReport;
import com.capstone.project.chronos.jobscheduling.repository.NotificationRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private NotificationRepository notificationRepository;

    /**
     * Sends a reminder email to the user associated with the job.
     * @param job The job object containing reminder details.
     */
    public void sendReminder(Jobs job) {
        // Create an email message
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(job.getUser().getEmail());
        message.setSubject(job.getJobType()+": " +job.getJobName());
        message.setText(buildEmailBodyForReminder(job));

        log.info("Processing email reminder for job: {}", job.getId());
        // Send the email
        mailSender.send(message);

        // Log the notification status
       // saveNotificationStatus(job, "SENT");
    }


    /**
     *
     * @param job
     * @return
     */
    private String buildEmailBodyForReminder(Jobs job) {
        return new StringBuilder()
                .append("Hello, ").append(job.getUser().getFirstName()).append(",\n\n")
                .append("You have a reminder:\n")
                .append("Title: ").append(job.getJobName()).append("\n")
                .append("Description: ").append(job.getJobDescription()).append("\n\n")
                .append("Thank you for using our service!\n")
                .append("Best regards,\n")
                .append("Chronos Service Team")
                .toString();
    }

    /**
     *
     * @param job
     * @param reportList
     */
    public void sendReminderForWeatherAndAirQuality(Jobs job, List<WeatherAndAirQualityReport> reportList) {
        // Create an email message
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(job.getUser().getEmail());
        message.setSubject(job.getJobType() + ": " + job.getJobName());
        message.setText(buildEmailBodyForWeatherAndAirQuality(job, reportList));

        log.info("Processing email reminder for job: {}", job.getId());

        // Send the email
        mailSender.send(message);

        // Log the notification status (Optional)
        //saveNotificationStatus(job, "SENT");
    }


    /**
     *
     * @param job
     * @param reportList
     * @return
     */
    private String buildEmailBodyForWeatherAndAirQuality(Jobs job, List<WeatherAndAirQualityReport> reportList) {
        StringBuilder emailBody = new StringBuilder()
                .append("Hello, ").append(job.getUser().getFirstName()).append(",\n\n")
                .append("Here is your Daily Weather and Air Quality report for the following locations:\n\n");

        // Group and format summaries by location
        Map<String, List<WeatherAndAirQualityReport>> reportsByLocation = reportList.stream()
                .collect(Collectors.groupingBy(WeatherAndAirQualityReport::getLocation));

        for (Map.Entry<String, List<WeatherAndAirQualityReport>> entry : reportsByLocation.entrySet()) {
            String location = entry.getKey();
            List<WeatherAndAirQualityReport> locationReports = entry.getValue();

            emailBody.append("Location: ").append(location).append("\n");

            for (WeatherAndAirQualityReport report : locationReports) {
                emailBody.append(report.getSummary(job.getRecurringType())).append("\n");
            }

            emailBody.append("\n---\n");
        }

        emailBody.append("Thank you for using our service!\n")
                .append("Best regards,\n")
                .append("Chronos Service Team");

        return emailBody.toString();
    }

    /**
     *
     * @param job
     * @param indiaNews
     * @param worldNews
     */
    public void sendNewsSummaryEmail(Jobs job, List<NewsSummaryReport> indiaNews, List<NewsSummaryReport> worldNews) {
        try {

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(job.getUser().getEmail());
            helper.setSubject("Daily News Summary: " + LocalDate.now());
            helper.setText(buildHtmlNewsEmailBody(job, indiaNews, worldNews), true);

            log.info("Sending daily news summary email for job: {}", job.getId());
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Error while sending news summary email for job: {}", job.getId(), e);
        }
    }

    /**
     *
     * @param job
     * @param indiaNews
     * @param worldNews
     * @return
     */
    private String buildHtmlNewsEmailBody(Jobs job, List<NewsSummaryReport> indiaNews, List<NewsSummaryReport> worldNews) {
        StringBuilder emailBody = new StringBuilder()
                .append("<html><body>")
                .append("<p>Hello, ").append(job.getUser().getFirstName()).append(",</p>")
                .append("<p>Here are the top news updates for today:</p>");

        // Add domestic news
        emailBody.append("<h2>Domestic News (India)</h2>");
        for (NewsSummaryReport report : indiaNews) {
            emailBody.append("<div style='margin-bottom: 20px;'>")
                    .append("<h4>").append(report.getTitle()).append("</h4>")
                    .append("<p>").append(report.getDescription()).append("</p>")
                    .append("<p>Source: <a href='").append(report.getSourceUrl()).append("'>")
                    .append(report.getSourceUrl()).append("</a></p>");

            if (report.getImageUrl() != null) {
                emailBody.append("<img src='").append(report.getImageUrl())
                        .append("' alt='News Image' style='width:100%; max-width:600px;'/>");
            }

            emailBody.append("</div>");
        }

        // Add international news
        emailBody.append("<h2>International News</h2>");
        for (NewsSummaryReport report : worldNews) {
            emailBody.append("<div style='margin-bottom: 20px;'>")
                    .append("<h4>").append(report.getTitle()).append("</h4>")
                    .append("<p>").append(report.getDescription()).append("</p>")
                    .append("<p>Source: <a href='").append(report.getSourceUrl()).append("'>")
                    .append(report.getSourceUrl()).append("</a></p>");

            if (report.getImageUrl() != null) {
                emailBody.append("<img src='").append(report.getImageUrl())
                        .append("' alt='News Image' style='width:100%; max-width:600px;'/>");
            }

            emailBody.append("</div>");
        }

        emailBody.append("<p>Thank you for using our service!</p>")
                .append("<p>Best regards,<br>Chronos Service Team</p>")
                .append("</body></html>");

        return emailBody.toString();
    }

    /**
     *
     * @param job
     * @param challenge
     * @param techSuggestion
     */
    public void sendCodingAndTechEmail(Jobs job, CodingChallenge challenge, TechSuggestion techSuggestion) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(job.getUser().getEmail());
        message.setSubject("Daily Coding Challenge & Tech Learning");

        String emailBody = buildHtmlCodingAndTechEmail(job, challenge, techSuggestion);
        message.setText(emailBody);

        mailSender.send(message);
    }

    /**
     * Sends a daily email containing fitness advice and nutritional guidance.
     *
     * @param job the user's job details
     */
    public void sendNutritionAndFitnessEmail(FitnessAndNutritionAdvice advice, Jobs job) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(job.getUser().getEmail());
        message.setSubject("Your Daily Fitness & Nutrition Advice");

        // Build the email content
        String emailBody = buildEmailBodyForFitnessAndNutrition(advice);
        message.setText(emailBody);

        // Send the email
        mailSender.send(message);
    }

    /**
     *
     * @param job
     * @param challenge
     * @param techSuggestion
     * @return
     */
    private String buildHtmlCodingAndTechEmail(Jobs job, CodingChallenge challenge, TechSuggestion techSuggestion) {
        return new StringBuilder()
                .append("<html><body>")
                .append("<p>Hello, ").append(job.getUser().getFirstName()).append(",</p>")
                .append("<h2>Daily Coding Challenge</h2>")
                .append("<p><strong>Title:</strong> ").append(challenge.getTitle()).append("</p>")
                .append("<p><strong>Difficulty:</strong> ").append(challenge.getDifficulty()).append("</p>")
                .append("<p><a href='").append(challenge.getLink()).append("'>Solve on LeetCode</a></p>")
                .append("<h2>Tech Learning Suggestion</h2>")
                .append("<p><strong>Title:</strong> ").append(techSuggestion.getTitle()).append("</p>")
                .append("<p>").append(techSuggestion.getDescription()).append("</p>")
                .append("<p><a href='").append(techSuggestion.getUrl()).append("'>Read More</a></p>")
                .append("<p>Thank you for using our service!</p>")
                .append("<p>Best regards,</p>")
                .append("<p>Chronos Service Team</p>")
                .append("</body></html>")
                .toString();
    }


    /**
     *
     * @param advice
     * @return
     */
    private String buildEmailBodyForFitnessAndNutrition(FitnessAndNutritionAdvice advice) {
        return new StringBuilder()
                .append("<html><body>")
                .append("<h1>Your Daily Fitness & Nutrition Advice</h1>")
                .append("<h2>Fitness Activity:</h2>")
                .append("<p>").append(advice.getFitnessActivity()).append("</p>")

                .append("<h2>Nutrition Advice:</h2>")
                .append("<p><strong>Breakfast:</strong> ").append(extractMealPart(advice.getNutritionAdvice(), "Breakfast")).append("</p>")
                .append("<p><strong>Lunch:</strong> ").append(extractMealPart(advice.getNutritionAdvice(), "Lunch")).append("</p>")
                .append("<p><strong>Dinner:</strong> ").append(extractMealPart(advice.getNutritionAdvice(), "Dinner")).append("</p>")

                .append("<p>Stay healthy and stay fit!</p>")
                .append("<p>Best regards,</p>")
                .append("<p>Chronos Service Team</p>")
                .append("</body></html>")
                .toString();
    }

    // Helper method to extract the meal part (Breakfast, Lunch, or Dinner)
    private String extractMealPart(String fullNutritionAdvice, String mealType) {
        String[] mealParts = fullNutritionAdvice.split("\\. ");
        for (String meal : mealParts) {
            if (meal.contains(mealType)) {
                return meal.replace(mealType + ": ", "");
            }
        }
        return "No " + mealType + " advice available.";
    }







//    /**
//     * Saves the notification status to the database after sending the email.
//     * @param job The job object to associate the notification with.
//     * @param status The status of the notification (e.g., SENT, FAILED).
//     */
//    private void saveNotificationStatus(Jobs job, String status) {
//        Notification notification = new Notification();
//        notification.setJob(job);
//        notification.setSentAt(LocalDateTime.now());
//        notification.setStatus(status);
//
//        // Save the notification to the database
//        notificationRepository.save(notification);
//    }
}
