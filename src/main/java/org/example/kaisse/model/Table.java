package org.example.kaisse.model;

import org.bson.Document;
import org.bson.types.ObjectId;

public class Table {
    private final ObjectId id;
    private Integer number;
    private Integer seats;
    private String emplacement;
    private Boolean isFree;

    public Table(ObjectId id, Integer number, Integer seats, String emplacement, Boolean isFree) {
        this.id = id;
        this.number = number;
        this.seats = seats;
        this.emplacement = emplacement;
        this.isFree = isFree;
    }


    public static Table createFromDocument(Document doc) {
        return new Table(
                doc.getObjectId("_id"),
                doc.getInteger("number"),
                doc.getInteger("seats"),
                doc.getString("emplacement"),
                doc.getBoolean("isFree")
        );
    }

    public ObjectId getId() {
        return id;
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
