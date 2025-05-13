package org.example.kaisse.model;

public class User {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public Float getWorkTime() {
        return workTime;
    }

    public void setWorkTime(Float workTime) {
        this.workTime = workTime;
    }

    public User(String name, String job, Float workTime) {
        this.name = name;
        this.job = job;
        this.workTime = workTime;
    }

    private String name;
    private String job;
    private Float workTime;
}
