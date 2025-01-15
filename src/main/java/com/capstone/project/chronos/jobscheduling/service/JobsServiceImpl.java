package com.capstone.project.chronos.jobscheduling.service;

import com.capstone.project.chronos.auth.entity.User;
import com.capstone.project.chronos.auth.repository.UserRepository;
import com.capstone.project.chronos.auth.service.UserService;
import com.capstone.project.chronos.jobscheduling.entity.Jobs;
import com.capstone.project.chronos.jobscheduling.enums.ScheduledType;
import com.capstone.project.chronos.jobscheduling.enums.Status;
import com.capstone.project.chronos.jobscheduling.exception.JobNotFoundException;
import com.capstone.project.chronos.jobscheduling.model.JobSubmissionRequest;
import com.capstone.project.chronos.jobscheduling.model.JobSubmissionResponse;
import com.capstone.project.chronos.jobscheduling.model.RescheduleJobRequest;
import com.capstone.project.chronos.jobscheduling.model.ViewJobResponse;
import com.capstone.project.chronos.jobscheduling.repository.JobsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class JobsServiceImpl implements JobsService {

    @Autowired
    private JobsRepository jobsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SchedulerService schedulerService;

    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public JobSubmissionResponse submitJob(JobSubmissionRequest request) {
        // Validate job type
        ScheduledType scheduledType;
        if ("ONCE".equalsIgnoreCase(request.getScheduleType())) {
            scheduledType = ScheduledType.ONCE;
        } else if ("RECURRING".equalsIgnoreCase(request.getScheduleType())) {
            scheduledType = ScheduledType.RECURRING;
        } else {
            throw new IllegalArgumentException("Invalid job type. Must be ONCE or RECURRING.");
        }

        // Fetch user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new JobNotFoundException("User with ID " + request.getUserId() + " not found."));

        // Create Jobs entity
        Jobs existingJob = jobsRepository.findByJobNameAndUserUserIdAndJobType(request.getJobName(), request.getUserId(), request.getScheduleType());
        if (existingJob != null) {
            throw new IllegalArgumentException("A job with the name and type already exists for this user, please choose a different name.");
        } else {
            Jobs job = new Jobs();
            job.setUser(user);
            job.setJobName(request.getJobName());
            job.setJobDescription(request.getJobDescription());
            job.setScheduleType(scheduledType);
            if (request.getRecurringType() != null) {
                job.setRecurringType(request.getRecurringType());
            }
            if (request.getScheduleExpression() != null) {
                job.setCronExpressionDate(request.getScheduleExpression());
            }
            job.setStatus(Status.PENDING);
            job.setJobType(request.getJobType());
            job.setRetryCount(0);
            job.setCreatedAt(LocalDate.now());

            // Save job
            Jobs savedJob = jobsRepository.save(job);
            schedulerService.initiateJob(savedJob);

            // Return response
            return new JobSubmissionResponse(savedJob.getId(), savedJob.getStatus().name(), "Job submitted successfully.");
        }
    }

    /**
     *
     * @param jobId
     * @return
     * @throws JobNotFoundException
     */
    @Override
    public ViewJobResponse viewJobStatus(Long jobId) throws JobNotFoundException {
        Jobs job = jobsRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Job with ID " + jobId + " not found."));

        ViewJobResponse response = new ViewJobResponse();
        response.setId(job.getId());
        response.setUserId(job.getUser().getUserId());
        response.setJobName(job.getJobName());
        response.setJobDescription(job.getJobDescription());
        response.setJobType(job.getJobType());
        if (job.getRecurringType() != null) {
            response.setRecurringType(job.getRecurringType());
        }
        response.setStatus(job.getStatus().name());
        response.setCronExpressionDate(job.getCronExpressionDate());
        if (job.getRetryCount() != null) {
            response.setRetryCount(job.getRetryCount());
        }
        response.setCreatedAt(job.getCreatedAt());
        if (job.getUpdatedAt() != null) {
            response.setUpdatedAt(job.getUpdatedAt());
        }
        return response;
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public List<ViewJobResponse> viewAllJobs() throws Exception {
        User user = userService.getCurrentUser();
        List<Jobs> jobs = jobsRepository.findByUserUserId(user.getUserId());
        return jobs.stream().map(job -> {
            ViewJobResponse response = new ViewJobResponse();
            response.setId(job.getId());
            response.setUserId(job.getUser().getUserId());
            response.setJobName(job.getJobName());
            response.setJobDescription(job.getJobDescription());
            response.setJobType(job.getJobType());
            if (job.getRecurringType() != null) {
                response.setRecurringType(job.getRecurringType());
            }
            response.setStatus(job.getStatus().name());
            response.setCronExpressionDate(job.getCronExpressionDate());
            if (job.getRetryCount() != null) {
                response.setRetryCount(job.getRetryCount());
            }
            response.setCreatedAt(job.getCreatedAt());
            if (job.getUpdatedAt() != null) {
                response.setUpdatedAt(job.getUpdatedAt());
            }
            return response;
        }).toList();
    }

    /**
     *
     * @param jobId
     * @return
     */
    @Override
    public JobSubmissionResponse cancelJob(Long jobId) {
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

    /**
     *
     * @param jobId
     * @param request
     * @return
     */
    @Override
    public JobSubmissionResponse rescheduleJob(Integer jobId, RescheduleJobRequest request) {
        Jobs job = jobsRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Job with ID " + jobId + " not found."));

        if (job.getStatus() == Status.CANCELLED) {
            throw new IllegalArgumentException("Cannot reschedule a cancelled job.");
        }

        if (request.getJobName() != null) {
            job.setJobName(request.getJobName());
        }
        if (request.getJobDescription() != null) {
            job.setJobDescription(request.getJobDescription());
        }
        if (request.getNewScheduleExpression() != null) {
            job.setCronExpressionDate(request.getNewScheduleExpression());
        }
        if (request.getRecurringType() != null) {
            job.setRecurringType(request.getRecurringType());
        }
        if (request.getJobType() != null) {
            job.setJobType(request.getJobType());
        }
        job.setStatus(Status.PENDING);
        job.setJobType(request.getJobType());
        job.setUpdatedAt(LocalDate.now());
        jobsRepository.save(job);
        schedulerService.initiateJob(job);
        return new JobSubmissionResponse(job.getId(), job.getStatus().name(), ("Job rescheduled successfully."));
    }
}
