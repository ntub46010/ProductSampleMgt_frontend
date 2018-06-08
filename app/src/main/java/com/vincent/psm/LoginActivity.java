package com.vincent.psm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.vincent.psm.broadcast_helper.manager.RequestManager;
import com.vincent.psm.data.DataHelper;
import com.vincent.psm.network_helper.MyOkHttp;

import org.json.JSONException;
import org.json.JSONObject;

import static com.vincent.psm.broadcast_helper.data.FirebaseUser.DATABASE_USERS;
import static com.vincent.psm.data.DataHelper.KEY_ACCOUNT;
import static com.vincent.psm.data.DataHelper.KEY_ID;
import static com.vincent.psm.data.DataHelper.KEY_IDENTITY;
import static com.vincent.psm.data.DataHelper.KEY_NAME;
import static com.vincent.psm.data.DataHelper.KEY_PASSWORD;
import static com.vincent.psm.data.DataHelper.KEY_STATUS;
import static com.vincent.psm.data.DataHelper.KEY_SUCCESS;
import static com.vincent.psm.data.DataHelper.KEY_USER_INFO;
import static com.vincent.psm.data.DataHelper.currentTokenIndex;
import static com.vincent.psm.data.DataHelper.loginUserId;
import static com.vincent.psm.data.DataHelper.tokens;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Activity activity;
    private EditText edtAcc, edtPwd;
    private Button btnLogin, btnDeleteToken;
    private CheckBox chkAutoLogin;
    private ProgressBar prgBar;

    private MyOkHttp conn;

    private SharedPreferences sp;
    private Intent it;
    private boolean waitingForToken = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        activity = this;

        edtAcc = findViewById(R.id.edtAccount);
        edtPwd = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnDeleteToken = findViewById(R.id.btnDeleteToken);
        chkAutoLogin = findViewById(R.id.chkAutoLogin);
        prgBar = findViewById(R.id.prgBar);

        btnLogin.setOnClickListener(this);
        btnDeleteToken.setOnClickListener(this);

        sp = getSharedPreferences(getString(R.string.sp_filename), MODE_PRIVATE);
        if (sp.getBoolean(getString(R.string.sp_auto_login), false))
            login(sp.getString(getString(R.string.sp_login_user), ""), sp.getString(getString(R.string.sp_login_password), ""), true);
    }

    private void login(final String account, final String password, final boolean isNormalLogin) {
        displayWidget(false);

        conn = new MyOkHttp(activity, new MyOkHttp.TaskListener() {
            @Override
            public void onFinished(JSONObject resObj) throws JSONException{
                if (resObj.getBoolean(KEY_STATUS)) {
                    if (resObj.getBoolean(KEY_SUCCESS)) {
                        if (isNormalLogin) {
                            JSONObject obj = resObj.getJSONObject(KEY_USER_INFO);
                            loginUserId = obj.getString(KEY_ID);
                            it = new Intent(activity, MainActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString(KEY_NAME, obj.getString(KEY_NAME));
                            bundle.putString(KEY_IDENTITY, obj.getString(KEY_IDENTITY));
                            it.putExtras(bundle);

                            writeAutoLoginRecord(account, password);
                            accessToken(account);
                        }else {
                            FirebaseDatabase.getInstance().getReference()
                                    .child(DATABASE_USERS)
                                    .child(account)
                                    .removeValue();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(5000);
                                    }catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    hdrDeleteToken.sendMessage(hdrDeleteToken.obtainMessage());
                                }
                            }).start();
                        }
                    }else {
                        Toast.makeText(activity, "帳號或密碼錯誤", Toast.LENGTH_SHORT).show();
                        displayWidget(true);
                    }
                }else {
                    Toast.makeText(activity, "伺服器發生例外", Toast.LENGTH_SHORT).show();
                }
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
        sp.edit()
                .putString(getString(R.string.sp_login_user), account)
                .apply();
        if (chkAutoLogin.isChecked()) {
            sp.edit()
                    .putBoolean(getString(R.string.sp_auto_login), true)
                    .putString(getString(R.string.sp_login_password), password)
                    .apply();
        }
    }

    private void accessToken(String account) {
        initTrdTimer();
        RequestManager.getInstance(activity).insertUser(account);
        RequestManager.getInstance(activity).getTokensById(account);
        waitingForToken = true;
        initTrdWaitToken(); //等待從Firebase取得本裝置所有Token，確認索引後再行登入
    }

    private void initTrdTimer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(30000);
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
                hdrTimer.sendMessage(hdrTimer.obtainMessage());
            }
        }).start();
    }

    @SuppressLint("HandlerLeak")
    private Handler hdrTimer = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            waitingForToken = false;
        }
    };

    private void initTrdWaitToken() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
                hdrWaitToken.sendMessage(hdrWaitToken.obtainMessage());
            }
        }).start();
    }

    @SuppressLint("HandlerLeak")
    private Handler hdrWaitToken = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (tokens != null && !tokens.isEmpty()) {
                String currentToken = getSharedPreferences(getString(R.string.sp_filename), MODE_PRIVATE)
                        .getString(getString(R.string.sp_token), "");
                for (int i = 0; i < tokens.size(); i++) {
                    if (tokens.get(i).equals(currentToken)) {
                        currentTokenIndex = i;
                        break;
                    }
                }
                displayWidget(true);
                startActivity(it);
                waitingForToken = false;
            }else {
                if (waitingForToken)
                    initTrdWaitToken();
                else {
                    Toast.makeText(activity, "連線逾時，請重新登入\n若多次失敗可嘗試清除Token", Toast.LENGTH_SHORT).show();
                    displayWidget(true);
                    waitingForToken = false;
                }
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler hdrDeleteToken = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Toast.makeText(activity, "清除完成，請再嘗試登入", Toast.LENGTH_SHORT).show();
            displayWidget(true);
        }
    };

    private void displayWidget(boolean showUp) {
        prgBar.setVisibility(showUp ? View.GONE : View.VISIBLE);
        btnLogin.setVisibility(showUp ? View.VISIBLE : View.INVISIBLE);
        btnDeleteToken.setVisibility(showUp ? View.VISIBLE : View.INVISIBLE);
        edtAcc.setEnabled(showUp);
        edtPwd.setEnabled(showUp);
        chkAutoLogin.setEnabled(showUp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                login(edtAcc.getText().toString(), edtPwd.getText().toString(), true);
                break;
            case R.id.btnDeleteToken:
                login(edtAcc.getText().toString(), edtPwd.getText().toString(), false);
                break;
        }
    }

    @Override
    public void onDestroy() {
        if (conn != null)
            conn.cancel();
        super.onDestroy();
    }
}