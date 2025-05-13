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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public Double getWorkTime() {
        return workTime;
    }

    public void setWorkTime(Double workTime) {
        this.workTime = workTime;
    }

    public User(String name, String password, String job, Double workTime) {
        this.name = name;
        this.password = password;
        this.job = job;
        this.workTime = workTime;
    }

    private String name;
    private String password;
    private String job;
    private Double workTime;
}
