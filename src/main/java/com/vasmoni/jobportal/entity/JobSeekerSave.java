package com.vasmoni.jobportal.entity;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"userId", "job"})
})
public class JobSeekerSave implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "userId", referencedColumnName = "user_account_id")
    private JobSeekerProfile userId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "job", referencedColumnName = "jobPostId")
    private JobPostActivity job;

    public JobSeekerSave() {
    }

    public JobSeekerSave(Integer id, JobSeekerProfile userId, JobPostActivity job) {
        this.id = id;
        this.userId = userId;
        this.job = job;
    }

    public Integer getId() {
        return id;
    }

    public JobSeekerSave setId(Integer id) {
        this.id = id;
        return this;
    }

    public JobSeekerProfile getUserId() {
        return userId;
    }

    public JobSeekerSave setUserId(JobSeekerProfile userId) {
        this.userId = userId;
        return this;
    }

    public JobPostActivity getJob() {
        return job;
    }

    public JobSeekerSave setJob(JobPostActivity job) {
        this.job = job;
        return this;
    }

    @Override
    public String toString() {
        return "JobSeekerSave{" +
                "id=" + id +
                ", userId=" + userId +
                ", job=" + job +
                '}';
    }
}
