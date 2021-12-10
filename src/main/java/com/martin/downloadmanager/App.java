package com.martin.downloadmanager;

import com.martin.downloadmanager.util.R;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class App  extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(R.getUI("downloadmanager.fxml"));
        loader.setController(new AppController());
        BorderPane borderPane = loader.load();

        Scene scene = new Scene(borderPane);
        stage.setScene(scene);
        stage.setTitle("Downloader");
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }
}
