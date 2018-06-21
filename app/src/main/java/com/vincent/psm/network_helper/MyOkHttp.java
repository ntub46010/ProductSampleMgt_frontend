package com.vincent.psm.network_helper;

import android.app.Activity;
import android.os.NetworkOnMainThreadException;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyOkHttp {
    private Activity activity;
    private OkHttpClient client;
    private Map<String, String> header = null;
    private Call call;
    private boolean safely = false;

    // 宣告一個接收回傳結果的程式必須實作的介面
    public interface TaskListener {
        void onFinished(JSONObject resObj) throws JSONException;
    }

    private TaskListener taskListener;

    public MyOkHttp(Activity activity, TaskListener taskListener) {
        this.activity = activity;
        this.taskListener = taskListener;
    }

    public void execute(String... sendingData) {
        client = new OkHttpClient();

        if (header == null)
            call = getCall(sendingData);
        else
            call = getHeaderCall(header, sendingData);

        sendRequest();
    }

    private Call getCall(String... sendingData) {
        Request request = null;
        if (sendingData.length == 1) { //POST一個帶參數的網址
            RequestBody formBody = new FormBody.Builder().build();
            request = new Request.Builder()
                    .url(sendingData[0])
                    .post(formBody)
                    .build();

        }else if (sendingData.length == 2) { //POST一組JSON字串到某網址
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), sendingData[1]);
            request = new Request.Builder()
                    .url(sendingData[0])
                    .post(requestBody)
                    .build();
        }
        return client.newCall(request);
    }

    private Call getHeaderCall(Map<String, String> headerMap, String[] sendingData) {
        Headers header = Headers.of(headerMap);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), sendingData[1]);
        Request request = new Request.Builder()
                .url(sendingData[0])
                .headers(header)
                .post(requestBody)
                .build();
        return client.newCall(request);
    }

    private void sendRequest() {
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, final Response response) {
                //連線成功
                if (safely) { //主程式需呼叫runOnUiThread
                    accessResponse(response);
                }else {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            accessResponse(response);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, final IOException e) {
                //連線失敗
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //連線失敗
                        Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
                e.printStackTrace();
            }
        });
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }

    public void setSafely(boolean safely) {
        this.safely = safely;
    }

    private void accessResponse(Response response) {
        try {
            taskListener.onFinished(new JSONObject(response.body().string()));
        }catch (JSONException e) {
            //Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
        }catch (IOException e) {
            //Toast.makeText(activity, "IOException\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }catch (NetworkOnMainThreadException e) {
            //Toast.makeText(activity, "NetworkOnMainThreadException\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void cancel() {
        if (client != null)
            client.dispatcher().cancelAll();
    }

}
