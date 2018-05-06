package com.vincent.psm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import static com.vincent.psm.data.DataHelper.KEY_USER_INFO;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        Bundle bundle = getIntent().getExtras();
        TextView textView = findViewById(R.id.textView);
        textView.setText(bundle.getString(KEY_USER_INFO));
        */
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1:
                startActivity(new Intent(this, ProductHomeActivity.class));
                break;
        }
    }
}
