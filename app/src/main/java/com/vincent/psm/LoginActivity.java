package com.vincent.psm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.vincent.psm.data.DataHelper;
import com.vincent.psm.network_helper.MyOkHttp;

import org.json.JSONException;
import org.json.JSONObject;

import static com.vincent.psm.data.DataHelper.KEY_ACCOUNT;
import static com.vincent.psm.data.DataHelper.KEY_ID;
import static com.vincent.psm.data.DataHelper.KEY_IDENTITY;
import static com.vincent.psm.data.DataHelper.KEY_NAME;
import static com.vincent.psm.data.DataHelper.KEY_PASSWORD;
import static com.vincent.psm.data.DataHelper.KEY_STATUS;
import static com.vincent.psm.data.DataHelper.KEY_SUCCESS;
import static com.vincent.psm.data.DataHelper.KEY_USER_INFO;
import static com.vincent.psm.data.DataHelper.loginUserId;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Context context;
    private EditText edtAcc, edtPwd;
    private Button btnLogin;
    private CheckBox chkAutoLogin;
    private ProgressBar prgBar;

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;

        edtAcc = findViewById(R.id.edtAccount);
        edtPwd = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        chkAutoLogin = findViewById(R.id.chkAutoLogin);
        prgBar = findViewById(R.id.prgBar);

        btnLogin.setOnClickListener(this);

        sp = getSharedPreferences(getString(R.string.sp_filename), MODE_PRIVATE);
        if (sp.getBoolean(getString(R.string.sp_auto_login), false))
            login(sp.getString(getString(R.string.sp_login_user), ""), sp.getString(getString(R.string.sp_login_password), ""));
    }

    private void login(final String account, final String password) {
        MyOkHttp conn = new MyOkHttp(LoginActivity.this, new MyOkHttp.TaskListener() {
            @Override
            public void onFinished(final String result) {
                try {
                    JSONObject resObj = new JSONObject(result);
                    if (resObj.getBoolean(KEY_STATUS)) {
                        if (resObj.getBoolean(KEY_SUCCESS)) {
                            JSONObject obj = resObj.getJSONObject(KEY_USER_INFO);
                            loginUserId = obj.getString(KEY_ID);
                            Intent it  = new Intent(LoginActivity.this, MainActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString(KEY_NAME, obj.getString(KEY_NAME));
                            bundle.putString(KEY_IDENTITY, obj.getString(KEY_IDENTITY));
                            it.putExtras(bundle);

                            writeAutoLoginRecord(account, password);
                            startActivity(it);
                        }else {
                            Toast.makeText(context, "帳號或密碼錯誤", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(context, "伺服器發生例外", Toast.LENGTH_SHORT).show();
                    }
                }catch (JSONException e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                btnLogin.setVisibility(View.VISIBLE);
                prgBar.setVisibility(View.INVISIBLE);
            }
        });
        try {
            JSONObject reqObj = new JSONObject();
            reqObj.put(KEY_ACCOUNT, account);
            reqObj.put(KEY_PASSWORD, DataHelper.getMD5(password));
            conn.execute(getString(R.string.link_login), reqObj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void writeAutoLoginRecord(String account, String password) {
        if (chkAutoLogin.isChecked()) {
            sp.edit()
                    .putBoolean(getString(R.string.sp_auto_login), true)
                    .putString(getString(R.string.sp_login_user), account)
                    .putString(getString(R.string.sp_login_password), password)
                    .apply();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                btnLogin.setVisibility(View.INVISIBLE);
                prgBar.setVisibility(View.VISIBLE);
                login(edtAcc.getText().toString(), edtPwd.getText().toString());
                break;
        }
    }
}
;