package com.martin.downloadmanager;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;

public class DownloadController implements Initializable {

    public TextField tfUrl;
    public TextField setTime;
    public Label lbStatus;
    public ProgressBar pbProgress;
    private String urlText;
    private DownloadTask downloadTask;
    private String urlFinal;
    private File file;
    private ExecutorService executor;
    private boolean suspender = false;
    private static final Logger logger = LogManager.getLogger(DownloadController.class);
    public Timer timer;
    public TimerTask timerTask;
    public boolean pauseBoolean = false;

    public DownloadController(String urlText, ExecutorService executor) {
        logger.info("Download " + urlText + " is created");
        this.urlText = urlText;
        this.executor = executor;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tfUrl.setText(urlText);
    }

    @FXML
    public void start(ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            file = fileChooser.showSaveDialog(tfUrl.getScene().getWindow());
            if (file == null)
                return;

            try {
                Thread.sleep(setTimeDownload() * 1000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }

            downloadTask = new DownloadTask(urlText, file);
            pbProgress.progressProperty().unbind();
            pbProgress.progressProperty().bind(downloadTask.progressProperty());

            downloadTask.stateProperty().addListener((observableValue, oldState, newState) -> {
                System.out.println(observableValue.toString());
                if (newState == Worker.State.SUCCEEDED) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("Download has finished");
                    alert.show();
                }
                if (newState == Worker.State.CANCELLED) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setContentText("Download has canceled");
                    alert.show();
                }
            });

            downloadTask.messageProperty().addListener((observableValue, oldValue, newValue) -> lbStatus.setText(newValue));
            executor.execute(downloadTask);


        } catch (MalformedURLException murle) {
            murle.printStackTrace();
            logger.error("URL not found", murle.fillInStackTrace());
        }
    }

    @FXML
    public void stop(ActionEvent event) {
        stop();
    }

    @FXML
    public void pause (ActionEvent actionEvent)  {
        downloadTask.interruptor(true);
    }

    @FXML
    public void reload (ActionEvent actionEvent) {
        downloadTask.interruptor(false);
    }

    public void stop() {
        if (downloadTask != null) {
            downloadTask.cancel();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("Do you want to delete the file?");
            alert.showAndWait();
            ButtonType result = alert.getResult();
            if (result == ButtonType.OK) {
                delete();
            }
        }
    }

    public void close() {
        if (downloadTask != null) {
            downloadTask.cancel();
        }
    }

    public int setTimeDownload() {
        if (setTime.getText().equals("")) {
            return 0;
        } else {
            int setTime = Integer.parseInt(this.setTime.getText());
            return setTime;
        }
    }

    public void delete() {
        if (file != null) {
            file.delete();
        }
        logger.info("File " + urlText + " is deleted");
    }
}

