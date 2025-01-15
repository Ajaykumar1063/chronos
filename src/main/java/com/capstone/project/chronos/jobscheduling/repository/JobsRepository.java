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

    // Find all jobs by job id and user
    List<Jobs> findByUserUserId(long userId);

    Jobs findByJobNameAndUserUserIdAndJobType(String jobName, Long userId, String jobType);

    Optional<Jobs> findById(Long jobId);
}

