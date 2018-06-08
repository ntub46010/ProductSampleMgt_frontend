package com.vincent.psm.profile;

import android.app.Activity;
import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vincent.psm.R;
import com.vincent.psm.data.User;
import com.vincent.psm.data.Verifier;
import com.vincent.psm.network_helper.MyOkHttp;

import org.json.JSONException;
import org.json.JSONObject;

import static com.vincent.psm.data.DataHelper.KEY_EMAIL;
import static com.vincent.psm.data.DataHelper.KEY_NAME;
import static com.vincent.psm.data.DataHelper.KEY_PASSWORD;
import static com.vincent.psm.data.DataHelper.KEY_PHONE;
import static com.vincent.psm.data.DataHelper.KEY_STATUS;
import static com.vincent.psm.data.DataHelper.KEY_SUCCESS;
import static com.vincent.psm.data.DataHelper.KEY_USER_ID;
import static com.vincent.psm.data.DataHelper.getMD5;

public class ProfileUpdateActivity extends AppCompatActivity {
    private Activity activity;

    private EditText edtName, edtPhone, edtEmail, edtOldPwd, edtNewPwd, edtNewPwd2;
    private ImageView btnSubmit;

    private MyOkHttp conn;
    private Dialog dlgUpload;
    private String userId, pwd;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);
        activity = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView txtBarTitle = toolbar.findViewById(R.id.txtToolbarTitle);
        txtBarTitle.setText("編輯個人資料");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        edtOldPwd = findViewById(R.id.edtOldPwd);
        edtNewPwd = findViewById(R.id.edtNewPwd);
        edtNewPwd2 = findViewById(R.id.edtNewPwd2);
        btnSubmit = findViewById(R.id.btnSubmit);

        Bundle bundle = getIntent().getExtras();
        userId = bundle.getString(KEY_USER_ID);
        pwd = bundle.getString(KEY_PASSWORD);
        edtName.setText(bundle.getString(KEY_NAME));
        edtPhone.setText(bundle.getString(KEY_PHONE));
        edtEmail.setText(bundle.getString(KEY_EMAIL));

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });

        prepareDialog();
    }

    private boolean isInfoValid() {
        user = null;
        user = new User(
                edtName.getText().toString(),
                edtPhone.getText().toString(),
                edtEmail.getText().toString(),
                edtOldPwd.getText().toString(),
                edtNewPwd.getText().toString(),
                edtNewPwd2.getText().toString()
        );
        Verifier v = new Verifier(activity);
        StringBuffer errMsg = new StringBuffer();

        errMsg.append(v.chkName("姓名", user.getName()));
        errMsg.append(v.chkPhone("電話", user.getPhone()));
        errMsg.append(v.chkEmail(user.getEmail()));

        if (!user.getOldPwd().equals("")) {
            if (!getMD5(user.getOldPwd()).equals(pwd))
                errMsg.append("原密碼錯誤");
            else
                errMsg.append(v.chkNewPwd(user.getNewPwd(), user.getNewPwd2()));
        }

        if (errMsg.length() != 0) {
            v.getDialog("編輯個人資料", errMsg.toString()).show();
            return false;
        }else
            return true;
    }

    private void updateProfile() {
        if (!isInfoValid())
            return;

        conn = new MyOkHttp(activity, new MyOkHttp.TaskListener() {
            @Override
            public void onFinished(JSONObject resObj) throws JSONException {
                dlgUpload.dismiss();
                if (resObj.getBoolean(KEY_STATUS)) {
                    if (resObj.getBoolean(KEY_SUCCESS)) {
                        Toast.makeText(activity, "編輯成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }else {
                        Toast.makeText(activity, "編輯失敗", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(activity, "伺服器發生例外", Toast.LENGTH_SHORT).show();
                }
            }
        });
        try {
            JSONObject reqObj = new JSONObject();
            reqObj.put(KEY_USER_ID, userId);
            reqObj.put(KEY_PASSWORD, user.getOldPwd().equals("") ? pwd : getMD5(user.getNewPwd()));
            reqObj.put(KEY_NAME, user.getName());
            reqObj.put(KEY_PHONE, user.getPhone());
            reqObj.put(KEY_EMAIL, user.getEmail());
            conn.execute(getString(R.string.link_edit_profile), reqObj.toString());
            dlgUpload.show();
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void prepareDialog() {
        dlgUpload = new Dialog(activity);
        dlgUpload.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlgUpload.setContentView(R.layout.dlg_uploading);
        dlgUpload.setCancelable(false);
        TextView txtUploadHint = dlgUpload.findViewById(R.id.txtHint);
        txtUploadHint.setText("上傳中...");
    }

    @Override
    public void onDestroy() {
        if (conn != null)
            conn.cancel();
        super.onDestroy();
    }
}
