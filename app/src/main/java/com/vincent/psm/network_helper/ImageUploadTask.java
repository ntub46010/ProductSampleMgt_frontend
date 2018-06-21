package com.vincent.psm.network_helper;

import android.graphics.Bitmap;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageUploadTask {
    private String uploadURL;
    private String photoName = null;
    private int serverResponseCode = 0;

    private InputStream fileInputStream;
    private ByteArrayOutputStream outputStream;

    public ImageUploadTask(String uploadURL) {
        this.uploadURL = uploadURL;
    }

    public int uploadPhotoByBitmap(Bitmap bitmap) {
        if (bitmap == null ) {
            return -1 ;
        }
        String fileName = "jpg";

        try {
            // open a URL connection to the Servlet
            outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);

            byte[] bitmapOutputByteArray = outputStream.toByteArray();
            fileInputStream = new ByteArrayInputStream(bitmapOutputByteArray);

            return uploadPhoto(fileName);
        }catch (MalformedURLException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int uploadPhotoByUri(String sourceFileUri) {
        if (sourceFileUri == null ) {
            return -1 ;
        }
        outputStream = new ByteArrayOutputStream();
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {
            return 0;
        }else {
            try {
                // open a URL connection to the Servlet
                fileInputStream = new FileInputStream(sourceFile);

                return uploadPhoto(sourceFileUri);
            }catch (MalformedURLException e) {
                e.printStackTrace();
            }catch (FileNotFoundException e) {
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    private int uploadPhoto(String source) throws IOException {
        URL url = new URL(uploadURL);
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoInput(true); // Allow Inputs
        conn.setDoOutput(true); // Allow Outputs
        conn.setUseCaches(false); // Don't use a Cached Copy
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("ENCTYPE", "multipart/form-data");
        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
        conn.setRequestProperty("uploaded_file", source);

        DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
        dos.writeBytes(twoHyphens + boundary + lineEnd);
        dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"" + source + "\"" + lineEnd);
        dos.writeBytes(lineEnd);

        // create a buffer of  maximum size
        int bytesAvailable = fileInputStream.available();
        int maxBufferSize = 1 * 1024 * 1024;
        int bufferSize = Math.min(bytesAvailable, maxBufferSize);
        byte[] buffer = new byte[bufferSize];

        // read file and write it into form...
        int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        while (bytesRead > 0) {
            dos.write(buffer, 0, bufferSize);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        }

        // send multipart form data necessary after file data...
        dos.writeBytes(lineEnd);
        dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

        //將上傳後的圖片檔名回傳至data
        conn.connect();
        InputStream inputStream = conn.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
        photoName = bufferedReader.readLine();

        inputStream.close();
        if (outputStream != null)
            outputStream.close();
        fileInputStream.close();
        dos.flush();
        dos.close();

        // Responses from the server (code and message)
        return conn.getResponseCode();
    }

    public String getPhotoName() {
        return photoName;
    }

}
