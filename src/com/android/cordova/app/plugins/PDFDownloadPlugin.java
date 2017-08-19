package com.android.cordova.app.plugins;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.android.cordova.app.MainActivity;
import com.android.cordova.app.async.DownloadFileFromURL;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;

/**
 * Created by NAGARAJ SRJ on 8/19/2017.
 */

public class PDFDownloadPlugin extends CordovaPlugin implements DownloadFileFromURL.DownloadedCallback {

    private static final int PDF_DOWNLOAD = 0;
    private static final int PDF_READ = 1;
    private static final String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final String READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final int WRITE_REQ_CODE = 0;
    private static final int READ_REQ_CODE = 1;
    CallbackContext callbackContext;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        boolean flag = false;
        this.callbackContext = callbackContext;
        switch (Integer.parseInt(action)) {
            case PDF_DOWNLOAD: {
                Log.d("args:", args.toString());
                download();
                break;
            }
        }
        return flag;
    }

    private void download() {
        if (cordova.hasPermission(WRITE_EXTERNAL_STORAGE)) {
            DownloadFileFromURL downloadFileFromURL = new DownloadFileFromURL();
            downloadFileFromURL.setDownloadedCallback(this);
            downloadFileFromURL.execute("http://192.168.0.7/pdf/resume.pdf");
        } else {
            getWritePermission(WRITE_REQ_CODE);
        }
    }

    private void readPDF() {
        if (cordova.hasPermission(READ_EXTERNAL_STORAGE)) {

        } else {
            getReadPermission(READ_REQ_CODE);
        }
    }

    protected void getWritePermission(int requestCode) {
        cordova.requestPermission(this, requestCode, WRITE_EXTERNAL_STORAGE);
    }

    protected void getReadPermission(int requestCode) {
        cordova.requestPermission(this, requestCode, WRITE_EXTERNAL_STORAGE);
    }

    public void onRequestPermissionResult(int requestCode, String[] permissions,
                                          int[] grantResults) throws JSONException {
        for (int r : grantResults) {
            if (r == PackageManager.PERMISSION_DENIED) {
                this.callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR,
                        "PERMISSION_DENIED_ERROR"));
                return;
            }
        }

        switch (requestCode) {
            case WRITE_REQ_CODE:
                download();
                break;
            case READ_REQ_CODE:
                readPDF();
                break;
        }
    }

    @Override
    public void onDownloadComplete(String path) {
        try {
            File pdfFile = new File(path);  // -> filename = maven.pdf
            Uri urlPath = Uri.fromFile(pdfFile);
            Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
            pdfIntent.setDataAndType(urlPath, "application/pdf");
            pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            try {
                cordova.getActivity().startActivity(pdfIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(cordova.getActivity().getApplicationContext(), "No Application available to view PDF", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
