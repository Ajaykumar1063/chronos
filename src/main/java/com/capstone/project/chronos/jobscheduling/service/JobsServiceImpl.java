package com.capstone.project.chronos.jobscheduling.service;

import com.capstone.project.chronos.auth.entity.User;
import com.capstone.project.chronos.auth.repository.UserRepository;
import com.capstone.project.chronos.jobscheduling.entity.Jobs;
import com.capstone.project.chronos.jobscheduling.enums.ScheduledType;
import com.capstone.project.chronos.jobscheduling.enums.Status;
import com.capstone.project.chronos.jobscheduling.exception.JobNotFoundException;
import com.capstone.project.chronos.jobscheduling.model.JobSubmissionRequest;
import com.capstone.project.chronos.jobscheduling.model.JobSubmissionResponse;
import com.capstone.project.chronos.jobscheduling.model.RescheduleJobRequest;
import com.capstone.project.chronos.jobscheduling.model.ViewJobResponse;
import com.capstone.project.chronos.jobscheduling.repository.JobsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;

@Service
public class JobsServiceImpl implements JobsService {

    @Autowired
    private JobsRepository jobsRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public JobSubmissionResponse submitJob(JobSubmissionRequest request) {
        // Validate job type
        ScheduledType scheduledType;
        if ("ONCE".equalsIgnoreCase(request.getJobType())) {
            scheduledType = ScheduledType.ONCE;
        } else if ("RECURRING".equalsIgnoreCase(request.getJobType())) {
            scheduledType = ScheduledType.RECURRING;
        } else {
            throw new IllegalArgumentException("Invalid job type. Must be ONCE or RECURRING.");
        }

        // Fetch user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new JobNotFoundException("User with ID " + request.getUserId() + " not found."));

        // Create Jobs entity
        Jobs existingJob = jobsRepository.findByJobNameAndUserIdAndJobType(request.getJobName(), request.getUserId(), request.getJobType());
        if (existingJob != null) {
            throw new IllegalArgumentException("A job with the name and type already exists for this user, please choose a different name.");
        } else {
            Jobs job = new Jobs();
            job.setUser(user);
            job.setJobName(request.getJobName());
            job.setJobType(request.getJobType());
            job.setScheduleType(scheduledType);
            job.setCronExpression(request.getScheduleExpression());
            job.setParameters(convertPayloadToJson(request.getPayload()));
            job.setStatus(Status.PENDING);
            job.setRetryCount(0);
            // maxRetries is set via @PrePersist to default to 3 if not set
            job.setCreatedAt(LocalDate.now());
            job.setUpdatedAt(LocalDate.now());

            // Save job
            Jobs savedJob = jobsRepository.save(job);

            // Return response
            return new JobSubmissionResponse(savedJob.getId(), savedJob.getStatus().name(), "Job submitted successfully.");
        }
    }

    @Override
    public ViewJobResponse viewJobStatus(Integer jobId) {
        Jobs job = jobsRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Job with ID " + jobId + " not found."));

        ViewJobResponse response = new ViewJobResponse();
        response.setId(job.getId());
        response.setUserId(job.getUser().getUserId());
        response.setJobName(job.getJobName());
        response.setJobType(job.getJobType());
        response.setStatus(job.getStatus().name());
        response.setScheduleExpression(job.getCronExpression());
        response.setRetryCount(job.getRetryCount());
        response.setCreatedAt(job.getCreatedAt());
        response.setUpdatedAt(job.getUpdatedAt());

        return response;
    }

    @Override
    public JobSubmissionResponse cancelJob(Integer jobId) {
        Jobs job = jobsRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Job with ID " + jobId + " not found."));

        if (job.getStatus() == Status.CANCELLED) {
            throw new IllegalArgumentException("Job is already cancelled.");
        }
        job.setStatus(Status.CANCELLED);
        job.setUpdatedAt(LocalDate.now());
        jobsRepository.save(job);

        return new JobSubmissionResponse(job.getId(), job.getStatus().name(), ("Job cancelled successfully."));
    }

    @Override
    public JobSubmissionResponse rescheduleJob(Integer jobId, RescheduleJobRequest request) {
        Jobs job = jobsRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Job with ID " + jobId + " not found."));

        if (job.getStatus() == Status.CANCELLED) {
            throw new IllegalArgumentException("Cannot reschedule a cancelled job.");
        }
        // Update schedule expression
        job.setCronExpression(request.getNewScheduleExpression());

        job.setUpdatedAt(LocalDate.now());
        jobsRepository.save(job);

        return new JobSubmissionResponse(job.getId(), job.getStatus().name(), ("Job rescheduled successfully."));
    }


    // Utility methods

    private String convertPayloadToJson(Map<String, Object> payload) {
        // Implement JSON conversion, e.g., using Jackson ObjectMapper
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid payload format.");
        }
    }

    private LocalDate parseScheduleExpression(String expression, ScheduledType type) {
        // Implement parsing logic based on job type
        try {
            return LocalDate.parse(expression);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid schedule expression format.");
        }
    }
}
