package com.github.tanyuushaa;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import javafx.scene.control.Label;


@SpringBootApplication
public class Main extends Application {
    public static void main(String[] args) {
        new Thread(() -> SpringApplication.run(Main.class)).start();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Label label = new Label("Warehouse");
        Scene scene = new Scene(label, 400, 100);
        primaryStage.setTitle("Warehouse");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
    }
}