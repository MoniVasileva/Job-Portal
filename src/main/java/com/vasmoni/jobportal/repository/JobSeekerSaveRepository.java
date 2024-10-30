package com.vasmoni.jobportal.repository;

import com.vasmoni.jobportal.entity.JobPostActivity;
import com.vasmoni.jobportal.entity.JobSeekerProfile;
import com.vasmoni.jobportal.entity.JobSeekerSave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface JobSeekerSaveRepository extends JpaRepository<JobSeekerSave, Integer> {

    public List<JobSeekerSave> findByUserId(JobSeekerProfile userAccountId);

    List<JobSeekerSave> findByJob(JobPostActivity job);
}
