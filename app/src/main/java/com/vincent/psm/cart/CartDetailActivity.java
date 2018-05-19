package com.vincent.psm.cart;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.vincent.psm.R;
import com.vincent.psm.adapter.ProductDisplayAdapter;
import com.vincent.psm.data.Cart;
import com.vincent.psm.data.Tile;
import com.vincent.psm.network_helper.MyOkHttp;
import com.vincent.psm.product.ProductHomeActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.vincent.psm.data.DataHelper.Comma;
import static com.vincent.psm.data.DataHelper.KEY_AMOUNT;
import static com.vincent.psm.data.DataHelper.KEY_CART_ID;
import static com.vincent.psm.data.DataHelper.KEY_CART_INFO;
import static com.vincent.psm.data.DataHelper.KEY_CART_NAME;
import static com.vincent.psm.data.DataHelper.KEY_CONTACT_PERSON;
import static com.vincent.psm.data.DataHelper.KEY_CONTACT_PHONE;
import static com.vincent.psm.data.DataHelper.KEY_CUSTOMER_NAME;
import static com.vincent.psm.data.DataHelper.KEY_CUSTOMER_PHONE;
import static com.vincent.psm.data.DataHelper.KEY_ID;
import static com.vincent.psm.data.DataHelper.KEY_NAME;
import static com.vincent.psm.data.DataHelper.KEY_PHOTO;
import static com.vincent.psm.data.DataHelper.KEY_PRODUCTS;
import static com.vincent.psm.data.DataHelper.KEY_SALES_ID;
import static com.vincent.psm.data.DataHelper.KEY_SALES_NAME;
import static com.vincent.psm.data.DataHelper.KEY_STATUS;
import static com.vincent.psm.data.DataHelper.KEY_SUBTOTAL;
import static com.vincent.psm.data.DataHelper.KEY_SUCCESS;
import static com.vincent.psm.data.DataHelper.KEY_TOTAL;
import static com.vincent.psm.data.DataHelper.defaultCartId;
import static com.vincent.psm.data.DataHelper.defaultCartName;
import static com.vincent.psm.data.DataHelper.loginUserId;

public class CartDetailActivity extends AppCompatActivity {
    private Context context;

    protected ImageView btnSubmit;
    private RecyclerView recyProduct;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton fabAdd, fabInfo;
    private ProgressBar prgBar;

    private MyOkHttp conDownload, conUpload;
    private Cart cart;
    private ArrayList<Tile> tiles;
    private ProductDisplayAdapter adapter;

    private String cartId, cartName;
    private boolean isShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_detail);
        context = this;
        Bundle bundle = getIntent().getExtras();
        cartId = bundle.getString(KEY_ID);
        cartName = bundle.getString(KEY_CART_NAME);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView txtBarTitle = toolbar.findViewById(R.id.txtToolbarTitle);
        txtBarTitle.setText("購物車：" + cartName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSubmit = findViewById(R.id.btnSubmit);
        recyProduct = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        fabAdd = findViewById(R.id.fabAdd);
        fabInfo = findViewById(R.id.fabInfo);
        prgBar = findViewById(R.id.prgBar);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(false);
            }
        });
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cartId.equals(defaultCartId))
                    Toast.makeText(context, "已選取購物車：" + cartName, Toast.LENGTH_SHORT).show();

                defaultCartId = cartId;
                defaultCartName = cartName;
                startActivity(new Intent(context, ProductHomeActivity.class));
            }
        });
        fabAdd.hide();

        fabInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder msgbox = new AlertDialog.Builder(context);
                msgbox.setTitle("購物車摘要")
                        .setMessage(getString(R.string.cart_info,
                                cart.getSalesName(),
                                cart.getCustomerName(),
                                cart.getCustomerPhone(),
                                cart.getContactPerson(),
                                cart.getContactPhone(),
                                "$ " + Comma(String.valueOf(cart.getTotal()))))
                        .setCancelable(true)
                        .setPositiveButton("確定", null)
                        .setNegativeButton("刪除購物車", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AlertDialog.Builder dlgDelete = new AlertDialog.Builder(context);
                                dlgDelete.setTitle("刪除購物車")
                                        .setMessage("真的要刪除購物車：" + cartName + "\n此舉無法復原")
                                        .setCancelable(true)
                                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                deleteCart();
                                            }
                                        })
                                        .setNegativeButton("否", null)
                                        .show();
                            }
                        })
                        .show();
            }
        });
        fabInfo.hide();
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

        tiles = new ArrayList<>();
        conDownload = new MyOkHttp(CartDetailActivity.this, new MyOkHttp.TaskListener() {
            @Override
            public void onFinished(String result) {
                if (result == null) {
                    Toast.makeText(context, "沒有網路連線", Toast.LENGTH_SHORT).show();
                    prgBar.setVisibility(View.GONE);
                    return;
                }
                try {
                    JSONObject resObj = new JSONObject(result);
                    if (resObj.getBoolean(KEY_STATUS)) {
                        if(resObj.getBoolean(KEY_SUCCESS)) {
                            //購物車摘要
                            JSONObject objCartInfo = resObj.getJSONObject(KEY_CART_INFO);
                            cart = new Cart(
                                    objCartInfo.getString(KEY_SALES_NAME),
                                    objCartInfo.getString(KEY_CUSTOMER_NAME),
                                    objCartInfo.getString(KEY_CUSTOMER_PHONE),
                                    objCartInfo.getString(KEY_CONTACT_PERSON),
                                    objCartInfo.getString(KEY_CONTACT_PHONE),
                                    objCartInfo.getInt(KEY_TOTAL)
                            );

                            //購物車明細
                            JSONArray ary = resObj.getJSONArray(KEY_PRODUCTS);
                            for (int i = 0; i < ary.length(); i++) {
                                JSONObject obj = ary.getJSONObject(i);
                                tiles.add(new Tile(
                                        obj.getString(KEY_ID),
                                        obj.getString(KEY_PHOTO),
                                        obj.getString(KEY_NAME),
                                        obj.getInt(KEY_AMOUNT),
                                        obj.getInt(KEY_SUBTOTAL)
                                ));
                            }
                            showData();
                        }else {
                            Toast.makeText(context, "沒有購物車", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(context, "伺服器發生例外", Toast.LENGTH_SHORT).show();
                    }
                }catch (JSONException e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        try {
            JSONObject reqObj = new JSONObject();
            reqObj.put(KEY_CART_ID, cartId);
            conDownload.execute(getString(R.string.link_show_cart), reqObj.toString());
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showData() {
        recyProduct.setHasFixedSize(true);
        recyProduct.setLayoutManager(new LinearLayoutManager(context));

        adapter = new ProductDisplayAdapter(getResources(), context, tiles, 10);
        adapter.setBackgroundColor(getResources(), R.color.card_product);
        recyProduct.setAdapter(adapter);
        swipeRefreshLayout.setEnabled(true);
        swipeRefreshLayout.setRefreshing(false);
        recyProduct.setVisibility(View.VISIBLE);

        fabAdd.show();
        fabInfo.show();
        prgBar.setVisibility(View.GONE);
        //tiles = null;
        isShown = true;
    }

    private void deleteCart() {
        conUpload = new MyOkHttp(CartDetailActivity.this, new MyOkHttp.TaskListener() {
            @Override
            public void onFinished(String result) {
                if (result == null) {
                    Toast.makeText(context, "沒有網路連線", Toast.LENGTH_SHORT).show();
                    prgBar.setVisibility(View.GONE);
                    return;
                }
                try {
                    JSONObject resObj = new JSONObject(result);
                    if (resObj.getBoolean(KEY_STATUS)) {
                        if (resObj.getBoolean(KEY_SUCCESS)) {
                            Toast.makeText(context, "已刪除購物車：" + cartName, Toast.LENGTH_SHORT).show();
                            finish();
                        }else {
                            Toast.makeText(context, "商品不存在", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(context, "伺服器發生例外", Toast.LENGTH_SHORT).show();
                    }
                }catch (JSONException e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        try {
            JSONObject reqObj = new JSONObject();
            reqObj.put(KEY_CART_ID, cartId);
            reqObj.put(KEY_SALES_ID, loginUserId);
            conUpload.execute(getString(R.string.link_delete_cart), reqObj.toString());
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        if (conDownload != null)
            conDownload.cancel();
        if (conUpload != null)
            conUpload.cancel();
        if (adapter != null) {
            adapter.destroy(true);
            adapter = null;
        }
        System.gc();
        super.onDestroy();
    }
}
