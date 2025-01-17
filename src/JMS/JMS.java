package JMS;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JMS extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("jms.fxml"));
        primaryStage.setTitle("JMS");
        primaryStage.setScene(new Scene(root, 690, 640));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
