package com.martin.downloadmanager;

import com.martin.downloadmanager.util.R;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;


public class AppController {
    public TextField tfUrl;
    public Button btDownload;
    public TabPane tpDownloads;
    public static int countDownloads;

    private Map<String, DownloadController> allDownloads;

    public AppController() {
        allDownloads = new HashMap<>();
    }

    @FXML
    public void download(ActionEvent event) {

        String urlText = tfUrl.getText();
        tfUrl.clear();
        tfUrl.requestFocus();
        download(urlText);

    }

    private void download(String url) {
        if (countDownloads < 10) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(R.getUI("download.fxml"));

                DownloadController downloadController = new DownloadController(url);
                loader.setController(downloadController);
                BorderPane bPane = loader.load();

                String filename = url.substring(url.lastIndexOf("/") + 1);
                Tab tab = new Tab(filename, bPane);
                tab.setOnClosed(event -> {
                    downloadController.stop();
                    countDownloads--;
                });
                tpDownloads.getTabs().add(tab);
                allDownloads.put(url, downloadController);
                countDownloads++;

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("No se pueden realizar mas de 10 descargar simultaneamente");
            alert.show();
        }
    }

    @FXML
    public void stopAllDownloads() {
        for (DownloadController downloadController : allDownloads.values()) {
            downloadController.stop();
            countDownloads--;
        }
        tpDownloads.getTabs().clear();
    }

    @FXML
    public void readDLC() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("Quieres leer el DLC?");
        alert.showAndWait();
        ButtonType result = alert.getResult();
        if (result == ButtonType.OK) {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(null);
            try {
                List<String> urls = Files.readAllLines(file.toPath());
                urls.forEach(url -> AppController.this.download(url));
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}
