package Scrapers.IMBD;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class IMBD extends Application {

    public static void main(String [] arguments){
        launch(arguments);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("IMBD.fxml"));
        primaryStage.setTitle("IMBD");
        primaryStage.setScene(new Scene(root, 690, 640));
        primaryStage.show();

    }
}
