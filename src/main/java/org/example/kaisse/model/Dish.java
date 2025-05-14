package org.example.kaisse.model;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

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
        ArrayList<Ingredient> ingredients = new ArrayList<>();
        List<Document> ingredientDocs = doc.getList("ingredients", Document.class);

        if (ingredientDocs != null) {
            for (Document ingDoc : ingredientDocs) {
                String name = ingDoc.getString("name");
                Integer quantity = ingDoc.getInteger("quantity");
                double price = ingDoc.getDouble("price");
                ingredients.add(new Ingredient(name, quantity, price));
            }
        }

        return new Dish(
                doc.getObjectId("_id"),
                doc.getString("name"),
                doc.getString("description"),
                doc.get("price", Double.class),
                doc.getString("image"),
                ingredients
        );
    }


    public Document transformDishIntoDocument() {
        Document doc = new Document()
                .append("name", name)
                .append("description", description)
                .append("price", price)
                .append("image", image);

        if (ingredients != null && !ingredients.isEmpty()) {
            List<Document> ingredientsDocs = ingredients.stream()
                    .map(Ingredient::transformIngredientsIntoDocument)
                    .toList();
            doc.append("ingredients", ingredientsDocs);
        }

        return doc;
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
