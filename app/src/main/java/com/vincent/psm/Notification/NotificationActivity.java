package com.vincent.psm.Notification;

import android.app.Activity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vincent.psm.R;
import com.vincent.psm.adapter.NotificationListAdapter;
import com.vincent.psm.data.Notification;
import com.vincent.psm.network_helper.MyOkHttp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.vincent.psm.data.DataHelper.KEY_ACCOUNT;
import static com.vincent.psm.data.DataHelper.KEY_CONTENT;
import static com.vincent.psm.data.DataHelper.KEY_CREATE_TIME;
import static com.vincent.psm.data.DataHelper.KEY_ID;
import static com.vincent.psm.data.DataHelper.KEY_NOTIFICATIONS;
import static com.vincent.psm.data.DataHelper.KEY_STATUS;
import static com.vincent.psm.data.DataHelper.KEY_SUCCESS;
import static com.vincent.psm.data.DataHelper.KEY_TITLE;
import static com.vincent.psm.data.DataHelper.KEY_USER_ID;
import static com.vincent.psm.data.DataHelper.loginUserId;

public class NotificationActivity extends AppCompatActivity {
    private Activity activity;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView lstNotification;
    private ProgressBar prgBar;

    private MyOkHttp conn;
    private ArrayList<Notification> notifications;
    private NotificationListAdapter adapter;

    private String userId;
    private boolean isShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        activity = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("通知");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        lstNotification = findViewById(R.id.lstNotification);
        prgBar = findViewById(R.id.prgBar);

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(false);
            }
        });

        lstNotification.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                showNotification(position);
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && !bundle.getString(KEY_ACCOUNT).equals("")) { //由推播進入
            userId = bundle.getString(KEY_ACCOUNT);
        }else //非經由推播進入
            userId = loginUserId;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isShown)
            loadData(true);
    }

    private void loadData(boolean showPrgBar) {
        isShown = false;
        if (showPrgBar)
            prgBar.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setEnabled(false);

        conn = new MyOkHttp(activity, new MyOkHttp.TaskListener() {
            @Override
            public void onFinished(JSONObject resObj) throws JSONException {
                if (resObj.length() == 0) {
                    Toast.makeText(activity, "沒有網路連線", Toast.LENGTH_SHORT).show();
                    prgBar.setVisibility(View.GONE);
                    return;
                }
                notifications = new ArrayList<>();
                if (resObj.getBoolean(KEY_STATUS)) {
                    if (resObj.getBoolean(KEY_SUCCESS)) {
                        JSONArray ary = resObj.getJSONArray(KEY_NOTIFICATIONS);
                        for (int i = 0; i < ary.length(); i++) {
                            JSONObject obj = ary.getJSONObject(i);
                            notifications.add(new Notification(
                                    obj.getString(KEY_ID),
                                    obj.getString(KEY_TITLE),
                                    obj.getString(KEY_CONTENT),
                                    obj.getString(KEY_CREATE_TIME)
                            ));
                        }
                    }else {
                        Toast.makeText(activity, "沒有通知", Toast.LENGTH_SHORT).show();
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
            conn.execute(getString(R.string.link_list_notifications), reqObj.toString());
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showData() {
        adapter = new NotificationListAdapter(activity, notifications);
        lstNotification.setAdapter(adapter);

        lstNotification.setVisibility(View.VISIBLE);
        prgBar.setVisibility(View.GONE);
        swipeRefreshLayout.setEnabled(true);
        swipeRefreshLayout.setRefreshing(false);

        isShown = true;
    }

    private void showNotification(int position) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        TableLayout layout = (TableLayout) inflater.inflate(R.layout.dlg_notification, null);
        TextView txtTitle = layout.findViewById(R.id.txtTitle);
        TextView txtContent = layout.findViewById(R.id.txtContent);
        TextView txtTime = layout.findViewById(R.id.txtCreateTime);

        Notification notify = (Notification) adapter.getItem(position);
        txtTitle.setText(notify.getTitle());
        txtContent.setText(notify.getContent());
        txtTime.setText(notify.getCreateTime().replace("-", "/"));

        AlertDialog.Builder msgbox = new AlertDialog.Builder(activity);
        msgbox.setTitle("通知")
                .setView(layout)
                .setCancelable(true)
                .setPositiveButton("確定", null)
                .show();
    }

    @Override
    public void onDestroy() {
        if (conn != null)
            conn.cancel();
        super.onDestroy();
    }
}
