package org.example.kaisse.model;

import org.bson.Document;

public class Table {
    private Integer number;
    private Integer seats;
    private String emplacement;
    private Boolean isFree;


    public Table(Integer number, Integer seats, String emplacement, Boolean isFree) {
        this.number = number;
        this.seats = seats;
        this.emplacement = emplacement;
        this.isFree = isFree;
    }


    public static Table createFromDocument(Document doc) {
        return new Table(
                Integer.parseInt(doc.get("number").toString()),
                Integer.parseInt(doc.get("seats").toString()),
                (String) doc.get("emplacement"),
                Boolean.parseBoolean(doc.get("isFree").toString())
        );
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getSeats() {
        return seats;
    }

    public void setSeats(Integer seats) {
        this.seats = seats;
    }

    public String getEmplacement() {
        return emplacement;
    }

    public void setEmplacement(String emplacement) {
        this.emplacement = emplacement;
    }

    public Boolean getFree() { return isFree; }

    public void setFree(Boolean free) { isFree = free; }

}
