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
                RequestManager.getInstance(BroadcastActivity.this).insertUser(id);
                break;

            case R.id.btnSend:
                String sendId = edtSendId.getText().toString(); //Firebase的ID
                String title = "BENQ"; //通知標題
                String message = "Hi，你朋友希望你買來送他喔！"; //通知內容
                String photoUrl = "http://image.yipee.cc/index/2013/12/BenQ-G2F-產品圖_1-copy.jpg"; //大圖示的URL
                RequestManager.getInstance(BroadcastActivity.this).prepareNotification(sendId, title, message, photoUrl); //開始發送推播
                break;
        }
    }
}
