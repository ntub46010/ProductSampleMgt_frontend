package com.vincent.psm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.vincent.psm.network_helper.MyOkHttp;

import org.json.JSONException;
import org.json.JSONObject;

import static com.vincent.psm.DataHelper.KEY_ACCOUNT;
import static com.vincent.psm.DataHelper.KEY_PASSWORD;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edtAcc, edtPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtAcc = findViewById(R.id.edtAccount);
        edtPwd = findViewById(R.id.edtPassword);
    }

    private void login(String account, String password) {
        MyOkHttp conn = new MyOkHttp(LoginActivity.this, new MyOkHttp.TaskListener() {
            @Override
            public void onFinished(final String result) {
                Intent it  = new Intent(LoginActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("JSON", result);
                it.putExtras(bundle);
                startActivity(it);
            }
        });
        try {
            JSONObject reqObj = new JSONObject();
            reqObj.put(KEY_ACCOUNT, account);
            reqObj.put(KEY_PASSWORD, password);
            conn.execute(getString(R.string.link_login), reqObj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                login(edtAcc.getText().toString(), edtPwd.getText().toString());
                break;
        }
    }
}
