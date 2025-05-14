package org.example.kaisse.model;

import org.bson.Document;

public class User {
    private String name;
    private String password;
    private String job;
    private Double workTime;
    private Boolean isAdmin;
    private Integer age;

    public User(String name, String password, String job, Double workTime, Boolean isAdmin, Integer age) {
        this.name = name;
        this.password = password;
        this.job = job;
        this.workTime = workTime;
        this.isAdmin = isAdmin;
        this.age = age;
    }

    public static User createFromDocument(Document doc) {
        return new User(
                doc.getString("name"),
                doc.getString("password"),
                doc.getString("job"),
                doc.getDouble("workTime"),
                doc.getBoolean("isAdmin"),
                doc.getInteger("age")
        );
    }

    // Converts the User to a Document
    public Document convertToDocument() {
        return new Document("name", this.name)
                .append("password", this.password)
                .append("job", this.job)
                .append("workTime", this.workTime)
                .append("isAdmin", this.isAdmin)
                .append("age", this.age);
    }

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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
