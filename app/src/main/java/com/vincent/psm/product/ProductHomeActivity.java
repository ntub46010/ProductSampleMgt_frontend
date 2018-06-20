package com.vincent.psm.product;

import android.app.Activity;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.vincent.psm.data.DataHelper.KEY_ONSALE;
import static com.vincent.psm.data.DataHelper.KEY_ID;
import static com.vincent.psm.data.DataHelper.KEY_LENGTH;
import static com.vincent.psm.data.DataHelper.KEY_NAME;
import static com.vincent.psm.data.DataHelper.KEY_PHOTO;
import static com.vincent.psm.data.DataHelper.KEY_PRICE;
import static com.vincent.psm.data.DataHelper.KEY_PRODUCTS;
import static com.vincent.psm.data.DataHelper.KEY_STATUS;
import static com.vincent.psm.data.DataHelper.KEY_SUCCESS;
import static com.vincent.psm.data.DataHelper.KEY_THICK;
import static com.vincent.psm.data.DataHelper.KEY_WIDTH;

public class ProductHomeActivity extends AppCompatActivity {
    private Activity activity;
    private RecyclerView recyProduct;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton fabTop, fabSearch;
    private ProgressBar prgBar;

    private MyOkHttp conn;

    private ArrayList<Tile> tiles;
    private ProductDisplayAdapter adapter;

    private boolean isShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_home);
        activity = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("瀏覽產品");
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
        prgBar = findViewById(R.id.prgBar);
        fabTop = findViewById(R.id.fabTop);
        fabSearch = findViewById(R.id.fabSearch);

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(false);
            }
        });

        fabTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    adapter.destroy(false);
                    recyProduct.scrollToPosition(0);
                }catch (Exception e) {
                    Toast.makeText(activity, "沒有商品，不能往上", Toast.LENGTH_SHORT).show();
                }
            }
        });

        fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity, ProductSearchActivity.class));
            }
        });

        fabTop.hide();
        fabSearch.hide();
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

        conn = new MyOkHttp(activity, new MyOkHttp.TaskListener() {
            @Override
            public void onFinished(final JSONObject resObj) throws JSONException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resObj.length() == 0) {
                            Toast.makeText(activity, "沒有網路連線", Toast.LENGTH_SHORT).show();
                            prgBar.setVisibility(View.GONE);
                            return;
                        }
                        try {
                            if (resObj.getBoolean(KEY_STATUS)) {
                                if(resObj.getBoolean(KEY_SUCCESS)) {
                                    tiles = new ArrayList<>();
                                    JSONArray ary = resObj.getJSONArray(KEY_PRODUCTS);
                                    for (int i = 0; i < ary.length(); i++) {
                                        JSONObject obj = ary.getJSONObject(i);
                                        tiles.add(new Tile(
                                                obj.getString(KEY_ID),
                                                obj.getString(KEY_PHOTO),
                                                obj.getString(KEY_NAME),
                                                obj.getString(KEY_LENGTH),
                                                obj.getString(KEY_WIDTH),
                                                obj.getString(KEY_THICK),
                                                obj.getString(KEY_PRICE)
                                        ));
                                    }
                                }else {
                                    Toast.makeText(activity, "沒有產品", Toast.LENGTH_SHORT).show();
                                }
                                showData();
                            }else {
                                Toast.makeText(activity, "伺服器發生例外", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        try {
            JSONObject reqObj = new JSONObject();
            reqObj.put(KEY_ONSALE, true);
            conn.setSafely(true);
            conn.execute(getString(R.string.link_list_products), reqObj.toString());
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showData() {
        recyProduct.setHasFixedSize(true);
        recyProduct.setLayoutManager(new LinearLayoutManager(activity));

        adapter = new ProductDisplayAdapter(getResources(), activity, tiles, 10);
        adapter.setBackgroundColor(getResources(), R.color.card_product);
        recyProduct.setAdapter(adapter);
        swipeRefreshLayout.setEnabled(true);
        swipeRefreshLayout.setRefreshing(false);

        recyProduct.setVisibility(View.VISIBLE);
        fabTop.show();
        fabSearch.show();
        prgBar.setVisibility(View.GONE);
        tiles = null;
        isShown = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (conn != null)
            conn.cancel();
    }

    @Override
    public void onDestroy() {
        if (adapter != null) {
            adapter.destroy(true);
            adapter = null;
        }
        System.gc();
        super.onDestroy();
    }
}
