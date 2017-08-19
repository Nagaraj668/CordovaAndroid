package com.android.cordova.app.async;

/**
 * Created by NAGARAJ SRJ on 8/19/2017.
 */

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Background Async Task to download file
 */
public class DownloadFileFromURL extends AsyncTask<String, String, String> {

    private DownloadedCallback downloadedCallback;

    public void setDownloadedCallback(DownloadedCallback downloadedCallback) {
        this.downloadedCallback = downloadedCallback;
    }

    /**
     * Before starting background thread
     * Show Progress Bar Dialog
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    /**
     * Downloading file in background thread
     */
    @Override
    protected String doInBackground(String... f_url) {
        int count;
        String pdfLocalPath = "";
        String fileName = null;
        try {
            String fileURL = f_url[0];
            URL url = new URL(fileURL);
            String paths[] = fileURL.split("/");
            fileName = paths[paths.length - 1];
            URLConnection conection = url.openConnection();
            conection.connect();
            // getting file length
            int lenghtOfFile = conection.getContentLength();

            // input stream to read file - with 8k buffer
            InputStream input = new BufferedInputStream(url.openStream(), 8192);

            // Output stream to write file
            pdfLocalPath = "/sdcard/" + fileName;
            OutputStream output = new FileOutputStream(pdfLocalPath);

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called
                publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                // writing data to file
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }

        return fileName;
    }

    /**
     * Updating progress bar
     */
    protected void onProgressUpdate(String... progress) {
        // setting progress percentage
        Log.i("onProgressUpdate->", Integer.parseInt(progress[0]) + "");
    }

    /**
     * After completing background task
     * Dismiss the progress dialog
     **/
    @Override
    protected void onPostExecute(String file_url) {
        String imagePath = Environment.getExternalStorageDirectory().toString() + "/" + file_url;
        // setting downloaded into image view
        Log.i("downloaded,imagePath->", imagePath);

        downloadedCallback.onDownloadComplete(imagePath);
    }

    public interface DownloadedCallback {
        void onDownloadComplete(String path);
    }
}