package com.vincent.psm.Cart;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.vincent.psm.R;
import com.vincent.psm.adapter.ProductDisplayAdapter;
import com.vincent.psm.data.Tile;
import com.vincent.psm.network_helper.MyOkHttp;
import com.vincent.psm.product.ProductHomeActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.vincent.psm.data.DataHelper.KEY_AMOUNT;
import static com.vincent.psm.data.DataHelper.KEY_CART_ID;
import static com.vincent.psm.data.DataHelper.KEY_CART_NAME;
import static com.vincent.psm.data.DataHelper.KEY_ID;
import static com.vincent.psm.data.DataHelper.KEY_NAME;
import static com.vincent.psm.data.DataHelper.KEY_PHOTO;
import static com.vincent.psm.data.DataHelper.KEY_PRODUCTS;
import static com.vincent.psm.data.DataHelper.KEY_STATUS;
import static com.vincent.psm.data.DataHelper.KEY_SUBTOTAL;
import static com.vincent.psm.data.DataHelper.KEY_SUCCESS;

public class CartDetailActivity extends AppCompatActivity {
    private Context context;

    private RecyclerView recyProduct;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton fabAdd, fabInfo;
    private ProgressBar prgBar;

    private MyOkHttp conn;
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
        toolbar.setTitle("購物車：" + cartName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyProduct = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        fabAdd = findViewById(R.id.fabAdd);
        fabInfo = findViewById(R.id.fabInfo);
        prgBar = findViewById(R.id.prgBar);

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
                startActivity(new Intent(context, ProductHomeActivity.class));
            }
        });
        fabInfo.hide();

        fabInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        conn = new MyOkHttp(CartDetailActivity.this, new MyOkHttp.TaskListener() {
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
                            Toast.makeText(context, "沒有產品", Toast.LENGTH_SHORT).show();
                        }
                        showData();
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
            conn.execute(getString(R.string.link_show_cart), reqObj.toString());
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

    @Override
    public void onDestroy() {
        if (conn != null)
            conn.cancel();
        if (adapter != null) {
            adapter.destroy(true);
            adapter = null;
        }
        System.gc();
        super.onDestroy();
    }
}
