package com.example.ticktapp.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import androidx.core.content.FileProvider;
import com.example.ticktapp.R;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;


public class FileDownloadManager {
    private static Context context;
    private static final int timeOut = 600000;
    private static int statusCode = 0;
    private static boolean status = false;
    private static final String TAG_RESPONSE = "/api";
    private static File downloadedFile = null;

    public static void downloadFileFromServer(final Context context, final String tag,
                                              final String postUrl, final String fileName,
                                              final FileDownloadListener responseListener,
                                              final ErrorListener errorListener) {
        final int BUFFER_SIZE = 4096;

        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                InputStream is = null;
                try {
                    URL url = new URL(postUrl);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setReadTimeout(timeOut);
                    con.setConnectTimeout(timeOut);
                    con.setRequestMethod("GET");
                    con.connect();
                    statusCode = con.getResponseCode();
                    if (statusCode == 200 /*|| statusCode ==400*/) {
                        is = con.getInputStream();
                        File appDirPath = new File(Environment.getExternalStorageDirectory() + "/" +context.getString(R.string.app_name));
                        if (!appDirPath.exists())
                            appDirPath.mkdir();

                        downloadedFile = new File(appDirPath.getPath() + "/" + fileName);
                          if(downloadedFile.exists())
                          {
                              status = true;
                              return "success";
                          }
                        FileOutputStream outputStream = new FileOutputStream(downloadedFile);
                        long length = con.getContentLength();
                        long receivedBytes = 0;
                        int bytesRead = -1;
                        byte[] buffer = new byte[BUFFER_SIZE];
                        while ((bytesRead = is.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                            receivedBytes = receivedBytes + bytesRead;
                            int progress = (int) (receivedBytes * 100 / length);
                            Log.i("progress", "" + progress);
                        }
                        outputStream.close();
                        is.close();
                        status = true;
                        return "success";
                    } else {
                        status = false;
                        Log.d(TAG_RESPONSE, "Error: Failed to connect to server: " + statusCode);
                        return "Failed to connect. Please try again";
                    }
                } catch (SocketTimeoutException e) {
                    status = false;
                    Log.d(TAG_RESPONSE, "Error: Request timed out" + statusCode);
                    return "Connection timed out.Please try again";
                } catch (Exception ex) {
                    status = false;
                    Log.d(TAG_RESPONSE, "Error: " + ex.getLocalizedMessage());
                    return "An error occurred. Please try again";
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                    } catch (IOException e) {
                        status = false;
                        Log.d(TAG_RESPONSE, "Error: " + e.getLocalizedMessage());
                        return "An error occurred. Please try again";
                    }
                }
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (status) {
                    if (result != null && result.equalsIgnoreCase("success")) {
                        Log.d(TAG_RESPONSE, "Response :- " + result);
                        responseListener.onResponse(tag, result, downloadedFile);
                    }
                } else {
                    Log.d(TAG_RESPONSE, "Response :- " + result);
                    errorListener.onError(tag, result);
                }
            }
        }.execute();
    }

    public interface FileDownloadListener {
        void onResponse(String tag, String response, File file);
    }

    public interface ResponseListener {
        void onResponse(String tag, String response);
    }

    public interface ProgressListener {
        void onProgress(int progress);
    }

    public interface ErrorListener {
        void onError(String tag, String errorMsg);
    }

    public  static void openFile(Activity context, File url) {

        Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".fileprovider", url);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
            // Word document
            intent.setDataAndType(uri, "application/msword");
        } else if (url.toString().contains(".pdf")) {
            // PDF file
            intent.setDataAndType(uri, "application/pdf");
        } else if (url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
            // Powerpoint file
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if (url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
            // Excel file
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        } else if (url.toString().contains(".zip") || url.toString().contains(".rar")) {
            // WAV audio file
            intent.setDataAndType(uri, "application/x-wav");
        } else if (url.toString().contains(".rtf")) {
            // RTF file
            intent.setDataAndType(uri, "application/rtf");
        } else if (url.toString().contains(".wav") || url.toString().contains(".mp3")) {
            // WAV audio file
            intent.setDataAndType(uri, "audio/x-wav");
        } else if (url.toString().contains(".gif")) {
            // GIF file
            intent.setDataAndType(uri, "image/gif");
        } else if (url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
            // JPG file
            intent.setDataAndType(uri, "image/jpeg");
        } else if (url.toString().contains(".txt")) {
            // Text file
            intent.setDataAndType(uri, "text/plain");
        } else if (url.toString().contains(".3gp") || url.toString().contains(".mpg") || url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
            // Video files
            intent.setDataAndType(uri, "video/*");
        } else {
            //if you want you can also define the intent type for any other file
            //additionally use else clause below, to manage other unknown extensions
            //in this case, Android will show all applications installed on the device
            //so you can choose which application to use
            intent.setDataAndType(uri, "*/*");
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);
    }

}
