package com.martin.downloadmanager;

import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DownloadTask extends Task<Integer> {

    private URL url;
    private File file;
    private long seconds;
    private long speed;

    private static final Logger logger = LogManager.getLogger(DownloadTask.class);

    public DownloadTask(String urlText, File file) throws MalformedURLException {
        this.url = new URL(urlText);
        this.file = file;
    }

    @Override
    protected Integer call() throws Exception {
        long incio = System.currentTimeMillis();
        logger.trace("Download " + url.toString() + " has started");
        updateMessage("Connecting with the server . . .");

        URLConnection urlConnection = url.openConnection();
        double fileSize = urlConnection.getContentLength();
        BufferedInputStream in = new BufferedInputStream(url.openStream());
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        byte dataBuffer[] = new byte[1024];
        int bytesRead;
        int totalRead = 0;
        double downloadProgress = 0;

        while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
            downloadProgress = (double) (totalRead / fileSize);
            updateProgress(downloadProgress, 1);
            int totalReadMb = (int) Math.round(totalRead/(Math.pow(1024, 2)));
            if (seconds != 0){
                speed = (long) (totalRead/(Math.pow(1024, 2))/seconds);
            } else{
                speed = 0;
            }

            updateMessage(downloadProgress * 100 + " %   --   "
                    + totalReadMb + " mbytes --  "
                    + speed + " MB/s "
                    );

            seconds = (System.currentTimeMillis() - incio) / 1000;
            fileOutputStream.write(dataBuffer, 0, bytesRead);
            totalRead += bytesRead;

            if (isCancelled()) {
                fileOutputStream.close();
                logger.trace("Download " + url.toString() + " canceled");
                return null;
            }
        }

        updateProgress(1, 1);
        updateMessage("100 %");
        fileOutputStream.close();
        logger.trace("Download " + url.toString() + " finished");
        return null;
    }
}