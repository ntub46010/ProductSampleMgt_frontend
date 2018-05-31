package com.vincent.psm.order;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.vincent.psm.R;
import com.vincent.psm.adapter.OrderDetailListAdapter;
import com.vincent.psm.data.Order;
import com.vincent.psm.data.Tile;
import com.vincent.psm.network_helper.MyOkHttp;
import com.vincent.psm.product.ProductDetailActivity;

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
import static com.vincent.psm.data.DataHelper.KEY_SALES_NAME;
import static com.vincent.psm.data.DataHelper.KEY_STATUS;
import static com.vincent.psm.data.DataHelper.KEY_SUBTOTAL;
import static com.vincent.psm.data.DataHelper.KEY_SUCCESS;
import static com.vincent.psm.data.DataHelper.KEY_THICK;
import static com.vincent.psm.data.DataHelper.KEY_WIDTH;

public class OrderDetailActivity extends AppCompatActivity {
    private Context context;

    private GridLayout layOrderInfo;
    private TextView txtCustomerName, txtCustomerPhone, txtContactPerson, txtProductTotal, txtDeliverFee, txtCondition,
                    txtPreDeliverDate, txtActDeliverDate, txtDeliverPlace, txtPs, txtSalesName;
    private ListView lstProduct;
    private FloatingActionButton fabUpdate;
    private ProgressBar prgBar;

    private MyOkHttp conn;

    private ArrayList<Tile> tiles;
    private OrderDetailListAdapter adapter;
    private Order order;
    private String orderId;

    private boolean isShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        context = this;
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
        prgBar = findViewById(R.id.prgBar);

        lstProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent it = new Intent(context, ProductDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(KEY_ID, ((Tile) adapter.getItem(position)).getId());
                it.putExtras(bundle);
                startActivity(it);
            }
        });

        fabUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(context, OrderUpdateActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(KEY_ORDER_ID, orderId);
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
        layOrderInfo.setVisibility(View.INVISIBLE);
        prgBar.setVisibility(View.VISIBLE);
        conn = new MyOkHttp(OrderDetailActivity.this, new MyOkHttp.TaskListener() {
            @Override
            public void onFinished(JSONObject resObj) throws JSONException {
                if (resObj.length() == 0) {
                    Toast.makeText(context, "沒有網路連線", Toast.LENGTH_SHORT).show();
                    prgBar.setVisibility(View.GONE);
                    return;
                }
                if (resObj.getBoolean(KEY_STATUS)) {
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
                                objOrderInfo.getString(KEY_SALES_NAME)
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
                                    obj.getString(KEY_PRICE),
                                    obj.getInt(KEY_AMOUNT),
                                    obj.getInt(KEY_SUBTOTAL)
                            ));
                        }
                        showData();
                    }else {
                        Toast.makeText(context, "訂單不存在", Toast.LENGTH_SHORT).show();
                        prgBar.setVisibility(View.GONE);
                    }
                }else {
                    Toast.makeText(context, "伺服器發生例外", Toast.LENGTH_SHORT).show();
                }
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
        txtCustomerName.setText(order.getCustomerName());
        txtCustomerPhone.setText(order.getCustomerPhone());
        txtContactPerson.setText(getString(R.string.txt_contact, order.getContactPerson(), order.getContactPhone()));
        txtProductTotal.setText("$ " + Comma(String.valueOf(order.getTotal())));
        txtDeliverFee.setText("$ " + Comma(String.valueOf(order.getDeliverFee())));
        txtCondition.setText(order.getCondition());
        txtPreDeliverDate.setText(order.getPredictDeliverDate());
        txtActDeliverDate.setText(order.getActualDeliverDate());
        txtDeliverPlace.setText(order.getDeliverPlace());
        txtPs.setText(order.getPs());
        txtSalesName.setText(order.getSalesName());

        //產品明細
        adapter = new OrderDetailListAdapter(context, tiles);
        lstProduct.setAdapter(adapter);

        prgBar.setVisibility(View.GONE);
        layOrderInfo.setVisibility(View.VISIBLE);
        fabUpdate.show();
        isShown = true;
    }

    @Override
    public void onDestroy() {
        if (conn != null)
            conn.cancel();
        super.onDestroy();
    }
}
