package com.vasmoni.jobportal.repository;

import com.vasmoni.jobportal.entity.JobPostActivity;
import com.vasmoni.jobportal.entity.JobSeekerApply;
import com.vasmoni.jobportal.entity.JobSeekerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobSeekerApplyRepository extends JpaRepository<JobSeekerApply,Integer> {

    List<JobSeekerApply> findByUserId(JobSeekerProfile userId);
    List<JobSeekerApply> findByJob(JobPostActivity job);
}
