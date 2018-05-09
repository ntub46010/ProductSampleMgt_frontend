package com.vincent.psm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.vincent.psm.product.ProductHomeActivity;
import com.vincent.psm.product.ProductMgtActivity;

import static com.vincent.psm.data.DataHelper.KEY_IDENTITY;
import static com.vincent.psm.data.DataHelper.KEY_NAME;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnProductHome = findViewById(R.id.btn1);
        Button btnProductMgt = findViewById(R.id.btn2);
        Button btnCart = findViewById(R.id.btn3);
        Button btnOrder = findViewById(R.id.btn4);
        Button btnNotification = findViewById(R.id.btn5);
        Button btnBroadcast = findViewById(R.id.btn10);
        Button btnUpload = findViewById(R.id.btn11);

        btnProductHome.setOnClickListener(this);
        btnProductMgt.setOnClickListener(this);
        btnCart.setOnClickListener(this);
        btnOrder.setOnClickListener(this);
        btnNotification.setOnClickListener(this);
        btnBroadcast.setOnClickListener(this);
        btnUpload.setOnClickListener(this);

        try {
            Bundle bundle = getIntent().getExtras();
            TextView txtGreeting = findViewById(R.id.txtGreeting);
            txtGreeting.setText(getString(R.string.txt_greeting, bundle.getString(KEY_NAME), bundle.getString(KEY_IDENTITY)));
        }catch (Exception e) {

        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1:
                startActivity(new Intent(this, ProductHomeActivity.class));
                break;
            case R.id.btn2:
                startActivity(new Intent(this, ProductMgtActivity.class));
                break;
            case R.id.btn3:
                break;
            case R.id.btn4:
                break;
            case R.id.btn5:
                break;
            case R.id.btn10:
                startActivity(new Intent(this, BroadcastActivity.class));
                break;
            case R.id.btn11:
                startActivity(new Intent(this, ImageUploadActivity.class));
                break;
        }
    }
}
