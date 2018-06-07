package com.vincent.psm.profile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.vincent.psm.R;
import com.vincent.psm.data.User;
import com.vincent.psm.network_helper.MyOkHttp;

import org.json.JSONException;
import org.json.JSONObject;

import static com.vincent.psm.data.DataHelper.KEY_AUTHORITY;
import static com.vincent.psm.data.DataHelper.KEY_EMAIL;
import static com.vincent.psm.data.DataHelper.KEY_IDENTITY;
import static com.vincent.psm.data.DataHelper.KEY_NAME;
import static com.vincent.psm.data.DataHelper.KEY_PASSWORD;
import static com.vincent.psm.data.DataHelper.KEY_PHONE;
import static com.vincent.psm.data.DataHelper.KEY_PROFILE;
import static com.vincent.psm.data.DataHelper.KEY_SALES_ID;
import static com.vincent.psm.data.DataHelper.KEY_STATUS;
import static com.vincent.psm.data.DataHelper.KEY_SUCCESS;
import static com.vincent.psm.data.DataHelper.KEY_USER_ID;
import static com.vincent.psm.data.DataHelper.loginUserId;

public class ProfileActivity extends AppCompatActivity {
    private Activity activity;

    private ImageView btnUpdate;
    private CardView cardView;
    private TextView txtName, txtPhone, txtEmail, txtIdentity;
    private ProgressBar prgBar;

    private MyOkHttp conn;

    private String userId;
    private User user;
    private boolean isShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        activity = this;
        userId = getIntent().getExtras().getString(KEY_SALES_ID);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView txtBarTitle = toolbar.findViewById(R.id.txtToolbarTitle);
        txtBarTitle.setText("個人檔案");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnUpdate = findViewById(R.id.btnSubmit);
        cardView = findViewById(R.id.cardView);
        txtName = findViewById(R.id.txtName);
        txtPhone = findViewById(R.id.txtPhone);
        txtEmail = findViewById(R.id.txtEmail);
        txtIdentity = findViewById(R.id.txtIdentity);
        prgBar = findViewById(R.id.prgBar);

        btnUpdate.setImageResource(R.drawable.icon_edit_white_small);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isShown)
            loadData();
    }

    private void loadData() {
        isShown = false;
        cardView.setVisibility(View.INVISIBLE);
        btnUpdate.setVisibility(View.GONE);
        prgBar.setVisibility(View.VISIBLE);

        user = null;
        conn = new MyOkHttp(activity, new MyOkHttp.TaskListener() {
            @Override
            public void onFinished(JSONObject resObj) throws JSONException {
                if (resObj.getBoolean(KEY_STATUS)) {
                    if (resObj.getBoolean(KEY_SUCCESS)) {
                        JSONObject obj = resObj.getJSONObject(KEY_PROFILE);
                        user = new User(
                                obj.getString(KEY_NAME),
                                obj.getString(KEY_PHONE),
                                obj.getString(KEY_EMAIL),
                                obj.getInt(KEY_AUTHORITY),
                                obj.getString(KEY_IDENTITY),
                                obj.getString(KEY_PASSWORD)
                        );
                    }else {
                        Toast.makeText(activity, "使用者不存在", Toast.LENGTH_SHORT).show();
                    }
                    showData();
                }else {
                    Toast.makeText(activity, "伺服器發生例外", Toast.LENGTH_SHORT).show();
                }
            }
        });
        try {
            JSONObject reqObj = new JSONObject();
            reqObj.put(KEY_USER_ID, userId);
            conn.execute(getString(R.string.link_show_profile), reqObj.toString());
        }catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void showData() {
        txtName.setText(user.getName());
        txtPhone.setText(user.getPhone());
        txtEmail.setText(user.getEmail());
        txtIdentity.setText(user.getIdentity());

        if (userId.equals(loginUserId)) {
            btnUpdate.setVisibility(View.VISIBLE);
            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(activity, ProfileUpdateActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(KEY_USER_ID, userId);
                    bundle.putString(KEY_NAME, user.getName());
                    bundle.putString(KEY_PHONE, user.getPhone());
                    bundle.putString(KEY_EMAIL, user.getEmail());
                    bundle.putString(KEY_PASSWORD, user.getOldPwd());
                    it.putExtras(bundle);
                    startActivity(it);
                }
            });
        }else {
            txtPhone.setTextColor(Color.parseColor("#5050FF"));
            txtPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent();
                    it.setAction(Intent.ACTION_DIAL);
                    it.setData(Uri.parse("tel:" + user.getPhone()));
                    startActivity(it);
                }
            });

            txtEmail.setTextColor(Color.parseColor("#5050FF"));
            txtEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent (Intent.ACTION_VIEW , Uri.parse("mailto:" + user.getEmail()));
                    startActivity(it);
                }
            });
        }

        cardView.setVisibility(View.VISIBLE);
        prgBar.setVisibility(View.GONE);
        isShown = true;
    }

    @Override
    public void onDestroy() {
        if (conn != null)
            conn.cancel();
        super.onDestroy();
    }
}
