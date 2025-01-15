package com.capstone.project.chronos.jobscheduling.repository;

import com.capstone.project.chronos.jobscheduling.entity.Jobs;
import com.capstone.project.chronos.jobscheduling.enums.ScheduledType;
import com.capstone.project.chronos.jobscheduling.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobsRepository extends JpaRepository<Jobs, Integer> {

    // Find by job name
    List<Jobs> findByJobName(String jobName);

    // Find all jobs with a specific status
    List<Jobs> findByStatus(Status status);

    // Find all jobs by job type
    List<Jobs> findByJobType(String jobType);

    // Find all jobs by job id and user
    List<Jobs> findByUserId(long userId);

    // Find all jobs with retry count less than a specified value
    List<Jobs> findByRetryCountLessThan(Integer retryCount);

    // Find all jobs with max retries greater than a specified value
    List<Jobs> findByMaxRetriesGreaterThan(Integer maxRetries);

    // Find all jobs by schedule type
    List<Jobs> findByScheduleType(ScheduledType scheduleType);

    // Custom query to find jobs created on a specific date
    @Query("SELECT j FROM Jobs j WHERE j.createdAt = :date")
    List<Jobs> findJobsByCreatedAt(LocalDate date);

    // Custom query to find jobs updated after a specific date
    @Query("SELECT j FROM Jobs j WHERE j.updatedAt > :date")
    List<Jobs> findJobsUpdatedAfter(LocalDate date);

    // Find all jobs with a specific cron expression
    List<Jobs> findByCronExpression(String cronExpression);

    Jobs findByJobNameAndUserIdAndJobType(String jobName, Long userId, String jobType);

    Optional<Jobs> findById(Long jobId);
}

