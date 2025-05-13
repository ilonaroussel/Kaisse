package org.example.kaisse.model;

import org.bson.Document;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Order {
    private String state;
    private Table table;
    private LocalDate date;
    private ArrayList<Dish> dishes;

    public Order(String state, Table table, LocalDate date, ArrayList<Dish> dishes) {
        this.state = state;
        this.table = table;
        this.date = date;
        this.dishes = dishes;
    }

    public static Order createFromDocument(Document doc) {
        return new Order(
                (String) doc.get("state"),
                Table.createFromDocument(((List<Document>) doc.get("table")).getFirst()),
                ((Date) doc.get("date")).toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                ((List<Document>) doc.get("dishes")).stream().map(Dish::createFromDocument).collect(Collectors.toCollection(ArrayList::new))
        );
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public ArrayList<Dish> getDishes() {
        return dishes;
    }

    public void setDishes(ArrayList<Dish> dishes) {
        this.dishes = dishes;
    }
}
