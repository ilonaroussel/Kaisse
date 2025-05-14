package org.example.kaisse.model;

public class User {

    public User(String name, String password, String job, Double workTime, Boolean isAdmin) {
        this.name = name;
        this.password = password;
        this.job = job;
        this.workTime = workTime;
        this.isAdmin = isAdmin;
    }

    private String name;
    private String password;
    private String job;
    private Double workTime;
    private Boolean isAdmin;

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

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
}
