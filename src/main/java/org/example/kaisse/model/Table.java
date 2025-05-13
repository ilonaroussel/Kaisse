package org.example.kaisse.model;

import org.bson.Document;
import org.bson.types.ObjectId;

public class Table {
    private ObjectId id;
    private Integer number;
    private Integer seats;
    private String emplacement;

    public Table(ObjectId id, Integer number, Integer seats, String emplacement) {
        this.id = id;
        this.number = number;
        this.seats = seats;
        this.emplacement = emplacement;
    }

    public static Table createFromDocument(Document doc) {
        return new Table(
                (ObjectId) doc.get("_id"),
                Integer.parseInt(doc.get("number").toString()),
                Integer.parseInt(doc.get("seats").toString()),
                (String) doc.get("emplacement")
        );
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
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
}
