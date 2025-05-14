package org.example.kaisse.model;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Dish {
    private final ObjectId id;
    private String name;
    private String description;
    private Double price;
    private String image;
    private ArrayList<Ingredient> ingredients;

    public Dish(ObjectId id, String name, String description, Double price, String image) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
    }

    public Dish(ObjectId id, String name, String description, Double price, String image, ArrayList<Ingredient> ingredients) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
        this.ingredients = ingredients;
    }

    public static Dish createFromDocument(Document doc) {
        return new Dish(
                doc.getObjectId("_id"),
                doc.getString("name"),
                doc.getString("description"),
                doc.getDouble("price"),
                doc.getString("image"),
                doc.getList("ingredients", Document.class)
                        .stream()
                        .map(Ingredient::createFromDocument)
                        .collect(Collectors.toCollection(ArrayList::new))
        );
    }

    public Document convertToDocument() {
        return new Document()
                .append("name", name)
                .append("description", description)
                .append("price", price)
                .append("image", image)
                .append("ingredients", ingredients.stream()
                    .map(Ingredient::convertToDocument)
                    .toList());
    }

    public ObjectId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }
}
