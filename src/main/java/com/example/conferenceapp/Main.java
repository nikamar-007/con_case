package com.example.conferenceapp;

import com.example.conferenceapp.util.FxUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        /* ---------- главное FXML ---------- */
        Parent root = FXMLLoader.load(
                getClass().getResource("/com/example/conferenceapp/fxml/Main.fxml"));

        Scene scene = new Scene(root);
        FxUtil.applyAppStyles(scene);
        FxUtil.applyAppIcon(primaryStage);

        primaryStage.setTitle("Conference Management System");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1024);
        primaryStage.setMinHeight(768);
        primaryStage.show();
    }

    public static void main(String[] args) { launch(args); }
}
