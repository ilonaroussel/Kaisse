package org.example.kaisse.controller;

import com.mongodb.client.MongoCollection;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.bson.Document;
import org.example.kaisse.Main;
import org.example.kaisse.model.Order;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class FinancesController implements Initializable {
    @FXML VBox dataContainer;
    @FXML Label titleLabel;
    @FXML Label winningsLabel;
    @FXML Label spendingLabel;
    @FXML Label benefitLabel;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        titleLabel.setText(String.format(
                "Recettes totales (faites le %s)",
                new SimpleDateFormat("dd/MM/yyyy à HH:mm").format(new Date())
        ));

        MongoCollection<Document> orderCollection = Main.database.getCollection("Order");

        List<Document> orderDocuments = orderCollection
                .find()
                .into(new ArrayList<>());

        List<Order> orders = orderDocuments
                .stream()
                .map(Order::createFromDocument)
                .filter(order -> order.getState().equals("VALIDATED"))
                .toList();

        double winnings = orders
                .stream()
                .map(order -> order
                        .getDishes()
                        .stream()
                        .map(orderDish -> orderDish.getQuantity() * orderDish.getDish().getPrice())
                        .reduce(0.0, Double::sum))
                .reduce(0.0, Double::sum);

        winningsLabel.setText(String.format("%.2f€", winnings));

        double spending = orders
                .stream()
                .map(order -> order.getDishes()
                        .stream()
                        .map(orderDish -> orderDish.getQuantity() * orderDish.getDish().getIngredients()
                                .stream()
                                .map(ingredient -> ingredient.getQuantity() * ingredient.getPrice())
                                .reduce(0.0, Double::sum))
                        .reduce(0.0, Double::sum))
                .reduce(0.0, Double::sum);

        spendingLabel.setText(String.format("%.2f€", spending));

        double benefit = winnings - spending;

        benefitLabel.setText(String.format("%.2f€", benefit));
    }

    private WritableImage convertNodeToWritableImage(Node node) {
        Bounds bounds = node.getLayoutBounds();
        WritableImage image = new WritableImage((int) bounds.getWidth(), (int) bounds.getHeight());
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);

        return node.snapshot(params, image);
    }

    private void saveImageAsPdf(Node node, File outputFile) throws IOException {
        // Convert Node to Image
        WritableImage fxImage = convertNodeToWritableImage(node);
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(fxImage, null);

        // Create PDF
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(new PDRectangle(bufferedImage.getWidth(), bufferedImage.getHeight()));
        document.addPage(page);

        // Convert Image to PDFBox Image
        PDImageXObject pdImage = LosslessFactory.createFromImage(document, bufferedImage);

        // Draw Image on page
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        contentStream.drawImage(pdImage, 0, 0);
        contentStream.close();

        // Save the PDF
        document.save(outputFile);
        document.close();
    }

    public void savePdf() throws IOException {
        String userHome = System.getProperty("user.home");
        File rootFolder = new File(userHome, "Downloads");

        if (!rootFolder.exists()) {
            rootFolder.mkdirs();
        }

        File pdfFolder = new File(rootFolder, "kaisse-finances");

        if (!pdfFolder.exists()) {
            pdfFolder.mkdirs();
        }

        String timestamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm").format(new Date());
        String fileName = "my-grid-" + timestamp + ".pdf";

        File file = new File(pdfFolder, fileName);

        saveImageAsPdf(dataContainer, file);
    }
}
