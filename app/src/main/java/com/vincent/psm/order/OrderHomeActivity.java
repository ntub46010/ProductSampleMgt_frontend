package com.vincent.psm.order;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.vincent.psm.R;
import com.vincent.psm.adapter.OrderListAdapter;
import com.vincent.psm.data.Order;
import com.vincent.psm.network_helper.MyOkHttp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.vincent.psm.data.DataHelper.KEY_CONDITION;
import static com.vincent.psm.data.DataHelper.KEY_CUSTOMER_NAME;
import static com.vincent.psm.data.DataHelper.KEY_ORDERS;
import static com.vincent.psm.data.DataHelper.KEY_ORDER_ID;
import static com.vincent.psm.data.DataHelper.KEY_PRE_DELIVER_DATE;
import static com.vincent.psm.data.DataHelper.KEY_PRODUCT_TOTAL;
import static com.vincent.psm.data.DataHelper.KEY_STATUS;
import static com.vincent.psm.data.DataHelper.KEY_SUCCESS;
import static com.vincent.psm.data.DataHelper.defaultOrderId;
import static com.vincent.psm.data.DataHelper.defaultOrderName;

public class OrderHomeActivity extends AppCompatActivity {
    private Activity activity;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView lstOrder;
    private ProgressBar prgBar;

    private MyOkHttp conn;
    private ArrayList<Order> orders;
    private OrderListAdapter adapter;

    private boolean isShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_home);
        activity = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("訂單一覽");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        lstOrder = findViewById(R.id.lstOrder);
        prgBar = findViewById(R.id.prgBar);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(false);
            }
        });
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light);

        lstOrder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent it = new Intent(activity, OrderDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(KEY_ORDER_ID, orders.get(position).getId());
                it.putExtras(bundle);
                startActivity(it);
            }
        });
        lstOrder.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Order order = (Order) adapter.getItem(position);
                AlertDialog.Builder msgbox = new AlertDialog.Builder(activity);
                msgbox.setTitle("訂單")
                        .setMessage("要設定為預設訂單嗎？")
                        .setCancelable(true)
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                defaultOrderId = order.getId();
                                defaultOrderName = order.getCustomerName();
                            }
                        })
                        .setNegativeButton("否", null)
                        .show();
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isShown)
            loadData(true);
    }

    private void loadData(boolean showPrgBar) {
        isShown = false;
        swipeRefreshLayout.setEnabled(false);
        if (showPrgBar)
            prgBar.setVisibility(View.VISIBLE);

        orders = new ArrayList<>();
        conn = new MyOkHttp(activity, new MyOkHttp.TaskListener() {
            @Override
            public void onFinished(final JSONObject resObj) throws JSONException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (resObj.getBoolean(KEY_STATUS)) {
                                if (resObj.getBoolean(KEY_SUCCESS)) {
                                    JSONArray ary = resObj.getJSONArray(KEY_ORDERS);
                                    for (int i = 0; i < ary.length(); i++) {
                                        JSONObject obj = ary.getJSONObject(i);
                                        orders.add(new Order(
                                                obj.getString(KEY_ORDER_ID),
                                                obj.getString(KEY_CUSTOMER_NAME),
                                                obj.getInt(KEY_PRODUCT_TOTAL),
                                                obj.getString(KEY_PRE_DELIVER_DATE),
                                                obj.getString(KEY_CONDITION)
                                        ));
                                    }
                                    showData();
                                }else {
                                    Toast.makeText(activity, "沒有訂單", Toast.LENGTH_SHORT).show();
                                    prgBar.setVisibility(View.GONE);
                                }
                            }else {
                                Toast.makeText(activity, "伺服器發生例外", Toast.LENGTH_SHORT).show();
                            }
                        }catch (JSONException e) {
                            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        try {
            JSONObject reqObj = new JSONObject();
            reqObj.put(KEY_CONDITION, 0);
            conn.execute(getString(R.string.link_list_orders), reqObj.toString());
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showData() {
        adapter = new OrderListAdapter(activity, orders);
        lstOrder.setAdapter(adapter);

        swipeRefreshLayout.setEnabled(true);
        swipeRefreshLayout.setRefreshing(false);

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
