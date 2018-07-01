package com.vincent.psm.order;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.vincent.psm.profile.ProfileActivity;
import com.vincent.psm.R;
import com.vincent.psm.adapter.OrderItemListAdapter;
import com.vincent.psm.data.Order;
import com.vincent.psm.data.Tile;
import com.vincent.psm.network_helper.MyOkHttp;
import com.vincent.psm.product.ProductDetailActivity;
import com.vincent.psm.product.ProductHomeActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.vincent.psm.data.DataHelper.Comma;
import static com.vincent.psm.data.DataHelper.KEY_ACT_DELIVER_DATE;
import static com.vincent.psm.data.DataHelper.KEY_AMOUNT;
import static com.vincent.psm.data.DataHelper.KEY_CONDITION;
import static com.vincent.psm.data.DataHelper.KEY_CONTACT_PERSON;
import static com.vincent.psm.data.DataHelper.KEY_CONTACT_PHONE;
import static com.vincent.psm.data.DataHelper.KEY_CUSTOMER_NAME;
import static com.vincent.psm.data.DataHelper.KEY_CUSTOMER_PHONE;
import static com.vincent.psm.data.DataHelper.KEY_DELIVER_FEE;
import static com.vincent.psm.data.DataHelper.KEY_DELIVER_PLACE;
import static com.vincent.psm.data.DataHelper.KEY_ID;
import static com.vincent.psm.data.DataHelper.KEY_LENGTH;
import static com.vincent.psm.data.DataHelper.KEY_NAME;
import static com.vincent.psm.data.DataHelper.KEY_ORDER_ID;
import static com.vincent.psm.data.DataHelper.KEY_ORDER_INFO;
import static com.vincent.psm.data.DataHelper.KEY_PRE_DELIVER_DATE;
import static com.vincent.psm.data.DataHelper.KEY_PRICE;
import static com.vincent.psm.data.DataHelper.KEY_PRODUCTS;
import static com.vincent.psm.data.DataHelper.KEY_PRODUCT_ID;
import static com.vincent.psm.data.DataHelper.KEY_PRODUCT_TOTAL;
import static com.vincent.psm.data.DataHelper.KEY_PS;
import static com.vincent.psm.data.DataHelper.KEY_SALES_ID;
import static com.vincent.psm.data.DataHelper.KEY_SALES_NAME;
import static com.vincent.psm.data.DataHelper.KEY_STATUS;
import static com.vincent.psm.data.DataHelper.KEY_SUBTOTAL;
import static com.vincent.psm.data.DataHelper.KEY_SUCCESS;
import static com.vincent.psm.data.DataHelper.KEY_THICK;
import static com.vincent.psm.data.DataHelper.KEY_WIDTH;
import static com.vincent.psm.data.DataHelper.authority;
import static com.vincent.psm.data.DataHelper.defaultOrderId;
import static com.vincent.psm.data.DataHelper.defaultOrderName;

public class OrderDetailActivity extends AppCompatActivity {
    private Activity activity;

    private GridLayout layOrderInfo;
    private TextView txtId, txtCustomerName, txtCustomerPhone, txtContactPerson, txtProductTotal, txtDeliverFee, txtCondition,
                    txtPreDeliverDate, txtActDeliverDate, txtDeliverPlace, txtPs, txtSalesName;
    private ListView lstProduct;
    private FloatingActionButton fabUpdate, fabAdd;
    private ProgressBar prgBar;

    private MyOkHttp conn;

    private ArrayList<Tile> tiles;
    private OrderItemListAdapter adapter;
    private Order order;
    private String orderId;

    private boolean isShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        activity = this;
        final Bundle bundle = getIntent().getExtras();
        orderId = bundle.getString(KEY_ORDER_ID);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("訂單詳情");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        layOrderInfo = findViewById(R.id.layOrderInfo);
        txtId = findViewById(R.id.txtOrderId);
        txtCustomerName = findViewById(R.id.txtCustomerName);
        txtCustomerPhone = findViewById(R.id.txtCustomerPhone);
        txtContactPerson = findViewById(R.id.txtContactPerson);
        txtProductTotal = findViewById(R.id.txtTotal);
        txtDeliverFee = findViewById(R.id.txtDeliverFee);
        txtCondition = findViewById(R.id.txtCondition);
        txtPreDeliverDate = findViewById(R.id.txtPreDeliverDate);
        txtActDeliverDate = findViewById(R.id.txtActDeliverDate);
        txtDeliverPlace = findViewById(R.id.txtDeliverPlace);
        txtPs = findViewById(R.id.txtPs);
        txtSalesName = findViewById(R.id.txtSalesName);
        lstProduct = findViewById(R.id.lstProduct);
        fabUpdate = findViewById(R.id.fabUpdate);
        fabAdd = findViewById(R.id.fabAdd);
        prgBar = findViewById(R.id.prgBar);

        lstProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (authority == 2) {
                    defaultOrderId = orderId;
                    defaultOrderName = order.getCustomerName();

                    if (!orderId.equals(defaultOrderId))
                        Toast.makeText(activity, "已選取訂單：" + order.getCustomerName(), Toast.LENGTH_SHORT).show();
                }

                Intent it = new Intent(activity, ProductDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(KEY_ID, ((Tile) adapter.getItem(position)).getId());
                bundle.putString(KEY_NAME, ((Tile) adapter.getItem(position)).getName());
                it.putExtras(bundle);
                startActivity(it);
            }
        });

        fabUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(activity, OrderUpdateActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(KEY_ORDER_ID, orderId);
                it.putExtras(bundle);
                startActivity(it);
            }
        });

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!orderId.equals(defaultOrderId))
                    Toast.makeText(activity, "已選取訂單：" + order.getCustomerName(), Toast.LENGTH_SHORT).show();

                defaultOrderId = orderId;
                defaultOrderName = order.getCustomerName();
                startActivity(new Intent(activity, ProductHomeActivity.class));
            }
        });

        txtCustomerPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent();
                it.setAction(Intent.ACTION_DIAL);
                it.setData(Uri.parse("tel:" + order.getCustomerPhone()));
                startActivity(it);
            }
        });

        txtContactPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent();
                it.setAction(Intent.ACTION_DIAL);
                it.setData(Uri.parse("tel:" + order.getContactPhone()));
                startActivity(it);
            }
        });

        txtSalesName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(activity, ProfileActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(KEY_SALES_ID, order.getSalesId());
                it.putExtras(bundle);
                startActivity(it);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isShown)
            loadData();
    }

    private void loadData() {
        isShown = false;
        fabUpdate.hide();
        fabAdd.hide();
        layOrderInfo.setVisibility(View.INVISIBLE);
        prgBar.setVisibility(View.VISIBLE);
        conn = new MyOkHttp(OrderDetailActivity.this, new MyOkHttp.TaskListener() {
            @Override
            public void onFinished(final JSONObject resObj) throws JSONException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {if (resObj.getBoolean(KEY_STATUS)) {
                            if (resObj.getBoolean(KEY_SUCCESS)) {
                                JSONObject objOrderInfo = resObj.getJSONObject(KEY_ORDER_INFO);
                                order = new Order(
                                        objOrderInfo.getString(KEY_ORDER_ID),
                                        objOrderInfo.getString(KEY_CUSTOMER_NAME),
                                        objOrderInfo.getString(KEY_CUSTOMER_PHONE),
                                        objOrderInfo.getString(KEY_CONTACT_PERSON),
                                        objOrderInfo.getString(KEY_CONTACT_PHONE),
                                        objOrderInfo.getInt(KEY_PRODUCT_TOTAL),
                                        objOrderInfo.getInt(KEY_DELIVER_FEE),
                                        objOrderInfo.getString(KEY_CONDITION),
                                        objOrderInfo.getString(KEY_PRE_DELIVER_DATE),
                                        objOrderInfo.getString(KEY_ACT_DELIVER_DATE),
                                        objOrderInfo.getString(KEY_DELIVER_PLACE),
                                        objOrderInfo.getString(KEY_PS),
                                        objOrderInfo.getString(KEY_SALES_NAME),
                                        objOrderInfo.getString(KEY_SALES_ID)
                                );

                                JSONArray ary = resObj.getJSONArray(KEY_PRODUCTS);
                                tiles = new ArrayList<>();
                                for (int i = 0; i < ary.length(); i++) {
                                    JSONObject obj = ary.getJSONObject(i);
                                    tiles.add(new Tile(
                                            obj.getString(KEY_PRODUCT_ID),
                                            obj.getString(KEY_NAME),
                                            obj.getString(KEY_LENGTH),
                                            obj.getString(KEY_WIDTH),
                                            obj.getString(KEY_THICK),
                                            obj.getInt(KEY_PRICE),
                                            obj.getInt(KEY_AMOUNT),
                                            obj.getInt(KEY_SUBTOTAL)
                                    ));
                                }
                                showData();
                            }else {
                                Toast.makeText(activity, "訂單不存在", Toast.LENGTH_SHORT).show();
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
            reqObj.put(KEY_ORDER_ID, orderId);
            conn.execute(getString(R.string.link_show_order), reqObj.toString());
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showData() {
        //訂單摘要
        txtId.setText(orderId);
        txtCustomerName.setText(order.getCustomerName());
        txtCustomerPhone.setText(order.getCustomerPhone());
        txtContactPerson.setText(getString(R.string.txt_contact, order.getContactPerson(), order.getContactPhone()));
        txtProductTotal.setText("$ " + Comma(order.getTotal()));
        txtDeliverFee.setText("$ " + Comma(order.getDeliverFee()));
        txtCondition.setText(order.getCondition());
        txtPreDeliverDate.setText(order.getPredictDeliverDate());
        txtActDeliverDate.setText(order.getActualDeliverDate());
        txtDeliverPlace.setText(order.getDeliverPlace());
        txtPs.setText(order.getPs());
        txtSalesName.setText(order.getSalesName());

        switch (order.getCondition()) {
            case "待處理":
                txtCondition.setTextColor(Color.parseColor("#FF5050"));
                break;
            case "已完成":
                txtCondition.setTextColor(Color.parseColor("#00A000"));
                break;
            default:
                txtCondition.setTextColor(Color.parseColor("#CC9900"));
                break;
        }

        //產品明細
        adapter = new OrderItemListAdapter(activity, tiles);
        lstProduct.setAdapter(adapter);


        if (authority == 2) {
            fabUpdate.show();
            fabAdd.show();
        }

        prgBar.setVisibility(View.GONE);
        layOrderInfo.setVisibility(View.VISIBLE);
        isShown = true;
    }

    @Override
    public void onDestroy() {
        if (conn != null)
            conn.cancel();
        super.onDestroy();
    }
}
