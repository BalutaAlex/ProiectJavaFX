package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.Data.DatabaseStorage;
import sample.Data.MovieItemStoreText;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Aplicatie Management Filme");
        primaryStage.setScene(new Scene(root, 720, 480));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        DatabaseStorage.getInstance().loadMovieItems(DatabaseStorage.getInstance().getConnection());
    }

    @Override
    public void stop() throws Exception {
        DatabaseStorage.getInstance().storeMovieItems(DatabaseStorage.getInstance().getConnection());
    }
}
