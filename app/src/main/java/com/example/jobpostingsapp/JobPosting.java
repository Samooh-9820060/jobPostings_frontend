package com.example.jobpostingsapp;

public class JobPosting {
    private String jobName;
    private String officeName;
    private String datePosted;
    private String deadline;
    private String link;

    public JobPosting(String jobName, String officeName, String datePosted, String deadline, String link) {
        this.jobName = jobName;
        this.officeName = officeName;
        this.datePosted = datePosted;
        this.deadline = deadline;
        this.link = link;
    }

    public String getJobName() {
        return jobName;
    }

    public String getOfficeName() {
        return officeName;
    }

    public String getDatePosted() {
        return datePosted;
    }

    public String getDeadline() {
        return deadline;
    }

    public String getLink() {
        return link;
    }
}

