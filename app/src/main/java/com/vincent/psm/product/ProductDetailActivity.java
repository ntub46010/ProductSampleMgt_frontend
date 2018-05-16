package com.vincent.psm.product;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.vincent.psm.R;
import com.vincent.psm.data.Tile;
import com.vincent.psm.network_helper.GetBitmapTask;
import com.vincent.psm.network_helper.MyOkHttp;

import org.json.JSONException;
import org.json.JSONObject;

import static com.vincent.psm.data.DataHelper.KEY_COLOR;
import static com.vincent.psm.data.DataHelper.KEY_ID;
import static com.vincent.psm.data.DataHelper.KEY_LENGTH;
import static com.vincent.psm.data.DataHelper.KEY_MATERIAL;
import static com.vincent.psm.data.DataHelper.KEY_NAME;
import static com.vincent.psm.data.DataHelper.KEY_ONSALE;
import static com.vincent.psm.data.DataHelper.KEY_PHOTO;
import static com.vincent.psm.data.DataHelper.KEY_PRICE;
import static com.vincent.psm.data.DataHelper.KEY_PRODUCT_INFO;
import static com.vincent.psm.data.DataHelper.KEY_PS;
import static com.vincent.psm.data.DataHelper.KEY_SAFE_STOCK;
import static com.vincent.psm.data.DataHelper.KEY_STATUS;
import static com.vincent.psm.data.DataHelper.KEY_SUCCESS;
import static com.vincent.psm.data.DataHelper.KEY_Stock;
import static com.vincent.psm.data.DataHelper.KEY_THICK;
import static com.vincent.psm.data.DataHelper.KEY_WIDTH;

public class ProductDetailActivity extends AppCompatActivity {
    private Context context;
    private ScrollView layProductDetail;
    private ImageView imgProduct;
    private TextView txtProductName, txtPrice, txtId, txtMaterial, txtColor, txtSize, txtPs, txtStock, txtSafeStock;
    private ProgressBar prgBar;
    private FloatingActionButton fabCart;

    private String id;

    private MyOkHttp conn;
    private GetBitmapTask getBitmap;
    private Tile tile;

    private boolean isShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        context = this;

        Bundle bundle = getIntent().getExtras();
        id = bundle.getString(KEY_ID);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(bundle.getString(KEY_NAME));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        layProductDetail = findViewById(R.id.layProductDetail);
        imgProduct = findViewById(R.id.imgProduct);
        txtProductName = findViewById(R.id.txtProductName);
        txtPrice = findViewById(R.id.txtProductPrice);
        txtId = findViewById(R.id.txtId);
        txtMaterial = findViewById(R.id.txtMaterial);
        txtColor = findViewById(R.id.txtColor);
        txtSize = findViewById(R.id.txtSize);
        txtPs = findViewById(R.id.txtPs);
        txtStock = findViewById(R.id.txtStock);
        txtSafeStock = findViewById(R.id.txtSafeStock);
        prgBar = findViewById(R.id.prgBar);
        fabCart = findViewById(R.id.fabCart);

        layProductDetail.setVisibility(View.INVISIBLE);
        fabCart.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isShown)
            loadData();
    }

    private void loadData() {
        isShown = false;
        prgBar.setVisibility(View.VISIBLE);

        conn = new MyOkHttp(ProductDetailActivity.this, new MyOkHttp.TaskListener() {
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
                            JSONObject obj = resObj.getJSONObject(KEY_PRODUCT_INFO);
                            tile = new Tile(
                                    obj.getString(KEY_ID),
                                    obj.getString(KEY_PHOTO),
                                    obj.getString(KEY_NAME),
                                    obj.getString(KEY_MATERIAL),
                                    obj.getString(KEY_COLOR),
                                    obj.getString(KEY_LENGTH),
                                    obj.getString(KEY_WIDTH),
                                    obj.getString(KEY_THICK),
                                    obj.getString(KEY_PRICE),
                                    obj.getString(KEY_PS),
                                    obj.getString(KEY_Stock),
                                    obj.getString(KEY_SAFE_STOCK),
                                    obj.getInt(KEY_ONSALE) == 1
                            );
                            getBitmap = new GetBitmapTask(getString(R.string.link_image), new GetBitmapTask.TaskListener() {
                                @Override
                                public void onFinished() {
                                    showData();
                                }
                            });
                            getBitmap.execute(tile);
                        }else {
                            Toast.makeText(context, "商品不存在", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(context, "伺服器發生例外", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        try {
            JSONObject reqObj = new JSONObject();
            reqObj.put(KEY_ID, id);
            conn.execute(getString(R.string.link_show_product_detail), reqObj.toString());
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showData() {
        imgProduct.setImageBitmap(tile.getImg());
        txtProductName.setText(tile.getName());
        txtPrice.setText("$ " + tile.getPrice());
        txtId.setText(tile.getId());
        txtMaterial.setText(tile.getMaterial());
        txtColor.setText(tile.getColor());
        txtSize.setText(String.format("%s x %s x %s mm", tile.getLength(), tile.getWidth(), tile.getThick()));
        txtPs.setText(tile.getPs());
        txtStock.setText(tile.getStock());
        txtSafeStock.setText(tile.getSafeStock());

        layProductDetail.setVisibility(View.VISIBLE);
        fabCart.setVisibility(View.VISIBLE);
        prgBar.setVisibility(View.GONE);
        isShown = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (conn != null)
            conn.cancel();
        if (getBitmap != null)
            getBitmap.cancel(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tile = null;
    }
}
