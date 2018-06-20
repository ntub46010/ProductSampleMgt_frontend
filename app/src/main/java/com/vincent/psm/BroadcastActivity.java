package com.vincent.psm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.vincent.psm.broadcast_helper.manager.RequestManager;

public class BroadcastActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edtRegId, edtSendId;
    private Button btnRegister, btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast);

        edtRegId = findViewById(R.id.edtRegId);
        edtSendId = findViewById(R.id.edtSendId);
        btnRegister = findViewById(R.id.btnRegister);
        btnSend = findViewById(R.id.btnSend);

        btnRegister.setOnClickListener(this);
        btnSend.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnRegister:
                String id = edtRegId.getText().toString();
                RequestManager.getInstance(BroadcastActivity.this).insertUser(id); //在Firebase註冊ID
                break;

            case R.id.btnSend:
                String sendId = edtSendId.getText().toString(); //Firebase的ID
                String title = "Firebase推播"; //通知標題
                String message = "看到這個訊息，代表推播成功囉！"; //通知內容
                String photoUrl = "https://firebase.google.com/_static/images/firebase/touchicon-180.png"; //大圖示的URL
                RequestManager.getInstance(BroadcastActivity.this)
                        .prepareNotification(sendId, title, message, photoUrl); //開始發送推播
                break;
        }
    }
}
