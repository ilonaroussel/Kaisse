module org.example.kaisse {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.driver.core;
    requires org.mongodb.bson;
    requires io.github.cdimascio.dotenv.java;
    requires java.desktop;
    requires jbcrypt;
    requires javafx.swing;
    requires org.apache.pdfbox;

    opens org.example.kaisse to javafx.fxml;
    exports org.example.kaisse;
    opens org.example.kaisse.controller to javafx.fxml;
    exports org.example.kaisse.controller;
    opens org.example.kaisse.controller.components to javafx.fxml;
    exports org.example.kaisse.controller.components;
}