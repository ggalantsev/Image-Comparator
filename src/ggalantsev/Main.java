package ggalantsev;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("View/comparator.fxml"));
        Scene scene = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Image comparator");
        primaryStage.setMinHeight(460);
        primaryStage.setMinWidth(620);
        primaryStage.setWidth(720);
        primaryStage.setHeight(480);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
