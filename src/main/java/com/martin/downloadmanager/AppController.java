package com.martin.downloadmanager;

import com.martin.downloadmanager.util.R;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class AppController {
    public TextField tfUrl;
    public TextField tfMaxNumber;
    public Button btDownload;
    public TabPane tpDownloads;
    public static int countDownloads;
    public static ExecutorService executor;
    public boolean interruptor;
    public Label lbRegister;

    private Map<String, DownloadController> allDownloads;

    public AppController() {
        allDownloads = new HashMap<>();
        interruptor = false;
    }

    @FXML
    public void download(ActionEvent event) {
        instanceExecute();
        String urlText = tfUrl.getText();
        tfUrl.clear();
        tfUrl.requestFocus();
        download(urlText);
    }

    private void download(String url) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(R.getUI("download.fxml"));

            DownloadController downloadController = new DownloadController(url, executor);
            loader.setController(downloadController);
            BorderPane bPane = loader.load();

            String filename = url.substring(url.lastIndexOf("/") + 1);
            Tab tab = new Tab(filename, bPane);
            tab.setOnClosed(event -> downloadController.close());
            tpDownloads.getTabs().add(tab);
            allDownloads.put(url, downloadController);

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }


    @FXML
    public void stopAllDownloads() {
        for (DownloadController downloadController : allDownloads.values()) {
            downloadController.stop();
        }
        tpDownloads.getTabs().clear();
    }

    @FXML
    public void readDLC() {
        instanceExecute();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("Do you want to read DLC?");
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

    public void instanceExecute() {
        if (tfMaxNumber.getText().equals("")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Select the max number of downloads");
            alert.show();
        }
        if (!interruptor) {
            executor = Executors.newFixedThreadPool(Integer.parseInt(tfMaxNumber.getText()));
            interruptor = true;
        }
    }

    public String readFile() {
        String texto = "";
        try {
            BufferedReader bf = new BufferedReader(new FileReader("C:\\Users\\r_mar\\IdeaProjects\\downladmanager\\downloadmanager.log"));
            String temp = "";
            String bfRead;
            while ((bfRead = bf.readLine()) != null) {
                temp = temp + bfRead;
            }
            texto = temp;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return texto;

    }

    public void registro() {

        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(R.getUI("registro.fxml"));
            lbRegister = loader.load();

            Scene scene = new Scene(lbRegister);
            stage.setScene(scene);
            stage.setTitle("Registro");
            stage.show();
            lbRegister.setText(readFile());

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
