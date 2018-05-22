package com.vincent.psm.product;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
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
import com.vincent.psm.adapter.StockListAdapter;
import com.vincent.psm.data.Tile;
import com.vincent.psm.network_helper.MyOkHttp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.vincent.psm.data.DataHelper.KEY_ONSALE;
import static com.vincent.psm.data.DataHelper.KEY_ID;
import static com.vincent.psm.data.DataHelper.KEY_NAME;
import static com.vincent.psm.data.DataHelper.KEY_PHOTO;
import static com.vincent.psm.data.DataHelper.KEY_PRODUCTS;
import static com.vincent.psm.data.DataHelper.KEY_STATUS;
import static com.vincent.psm.data.DataHelper.KEY_SUCCESS;

public class ProductMgtActivity extends AppCompatActivity {
    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView lstProduct;
    private FloatingActionButton fabPost;
    private ProgressBar prgBar;

    private MyOkHttp conn;
    private ArrayList<Tile> tiles;
    private StockListAdapter adapter;

    private boolean isShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_mgt);
        context = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("產品管理");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        lstProduct = findViewById(R.id.lstProduct);
        fabPost = findViewById(R.id.fab_post);
        prgBar = findViewById(R.id.prgBar);

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.destroy(false);
                adapter = null;
                loadData(false);
            }
        });

        lstProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //跳出商品編輯畫面
                adapter.pressPosition = position;
                adapter.dialog.show();
            }
        });

        fabPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProductMgtActivity.this, ProductPostActivity.class));
            }
        });
        fabPost.hide();
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
        conn = new MyOkHttp(ProductMgtActivity.this, new MyOkHttp.TaskListener() {
            @Override
            public void onFinished(JSONObject resObj) throws JSONException {
                if (resObj.length() == 0) {
                    Toast.makeText(context, "沒有網路連線", Toast.LENGTH_SHORT).show();
                    prgBar.setVisibility(View.GONE);
                    return;
                }
                if (resObj.getBoolean(KEY_STATUS)) {
                    if (resObj.getBoolean(KEY_SUCCESS)) {
                        tiles = new ArrayList<>();
                        JSONArray ary = resObj.getJSONArray(KEY_PRODUCTS);
                        for (int i=0; i<ary.length(); i++) {
                            JSONObject obj = ary.getJSONObject(i);
                            tiles.add(new Tile(
                                    obj.getString(KEY_ID),
                                    obj.getString(KEY_PHOTO),
                                    obj.getString(KEY_NAME),
                                    obj.getInt(KEY_ONSALE) == 1
                            ));
                        }
                        showData();
                    }else {
                        Toast.makeText(context, "沒有產品", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, "伺服器發生例外", Toast.LENGTH_SHORT).show();
                }
            }
        });
        try {
            JSONObject reqObj = new JSONObject();
            reqObj.put(KEY_ONSALE, false);
            conn.execute(getString(R.string.link_list_products), reqObj.toString());
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showData() {
        adapter = new StockListAdapter(getResources(), context, tiles, R.layout.lst_stock, 10);
        lstProduct.setAdapter(adapter);

        tiles = null;
        lstProduct.setVisibility(View.VISIBLE);
        fabPost.show();
        prgBar.setVisibility(View.GONE);
        swipeRefreshLayout.setEnabled(true);
        swipeRefreshLayout.setRefreshing(false);

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
