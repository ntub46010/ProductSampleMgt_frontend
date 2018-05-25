package com.vincent.psm.product;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.vincent.psm.R;
import com.vincent.psm.data.Tile;
import com.vincent.psm.network_helper.ImageDownloader;
import com.vincent.psm.network_helper.MyOkHttp;

import org.json.JSONException;
import org.json.JSONObject;

import static com.vincent.psm.data.DataHelper.Comma;
import static com.vincent.psm.data.DataHelper.KEY_AMOUNT;
import static com.vincent.psm.data.DataHelper.KEY_CART_ID;
import static com.vincent.psm.data.DataHelper.KEY_COLOR;
import static com.vincent.psm.data.DataHelper.KEY_ID;
import static com.vincent.psm.data.DataHelper.KEY_CART_AMOUNT;
import static com.vincent.psm.data.DataHelper.KEY_LENGTH;
import static com.vincent.psm.data.DataHelper.KEY_MATERIAL;
import static com.vincent.psm.data.DataHelper.KEY_NAME;
import static com.vincent.psm.data.DataHelper.KEY_ONSALE;
import static com.vincent.psm.data.DataHelper.KEY_PHOTO;
import static com.vincent.psm.data.DataHelper.KEY_PRICE;
import static com.vincent.psm.data.DataHelper.KEY_PRODUCT_ID;
import static com.vincent.psm.data.DataHelper.KEY_PRODUCT_INFO;
import static com.vincent.psm.data.DataHelper.KEY_PS;
import static com.vincent.psm.data.DataHelper.KEY_SAFE_STOCK;
import static com.vincent.psm.data.DataHelper.KEY_STATUS;
import static com.vincent.psm.data.DataHelper.KEY_SUCCESS;
import static com.vincent.psm.data.DataHelper.KEY_STOCK;
import static com.vincent.psm.data.DataHelper.KEY_THICK;
import static com.vincent.psm.data.DataHelper.KEY_WIDTH;
import static com.vincent.psm.data.DataHelper.defaultCartId;
import static com.vincent.psm.data.DataHelper.defaultCartName;

public class ProductDetailActivity extends AppCompatActivity {
    private Context context;
    private ScrollView layProductDetail;
    private ImageView imgProduct;
    private TextView txtProductName, txtPrice, txtId, txtMaterial, txtColor, txtSize, txtPs, txtStock, txtSafeStock;
    private ProgressBar prgBar;
    private FloatingActionButton fabCart;

    private String id;
    private int cartAmount = 0, amount = 0;

    private MyOkHttp conn;
    private ImageDownloader imageLoader;
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

        fabCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart();
            }
        });
        fabCart.hide();
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
            public void onFinished(JSONObject resObj) throws JSONException {
                if (resObj.length() == 0) {
                    Toast.makeText(context, "沒有網路連線", Toast.LENGTH_SHORT).show();
                    prgBar.setVisibility(View.GONE);
                    return;
                }
                if (resObj.getBoolean(KEY_STATUS)) {
                    if (resObj.getBoolean(KEY_SUCCESS)) {
                        //檢查是否在購物車
                        cartAmount = resObj.getInt(KEY_CART_AMOUNT);

                        //產品詳情
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
                                obj.getString(KEY_STOCK),
                                obj.getString(KEY_SAFE_STOCK),
                                obj.getInt(KEY_ONSALE) == 1
                        );
                        imageLoader = new ImageDownloader(getString(R.string.link_image), new ImageDownloader.TaskListener() {
                            @Override
                            public void onFinished() {
                                showData();
                            }
                        });
                        imageLoader.execute(tile);
                    }else {
                        Toast.makeText(context, "商品不存在", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, "伺服器發生例外", Toast.LENGTH_SHORT).show();
                }
            }
        });
        try {
            JSONObject reqObj = new JSONObject();
            reqObj.put(KEY_ID, id);
            reqObj.put(KEY_CART_ID, defaultCartId);
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
        txtSize.setText(getString(R.string.txt_product_size, tile.getLength(), tile.getWidth(), tile.getThick()));
        txtPs.setText(tile.getPs());
        txtStock.setText(tile.getStock());
        txtSafeStock.setText(tile.getSafeStock());

        layProductDetail.setVisibility(View.VISIBLE);
        fabCart.show();
        prgBar.setVisibility(View.GONE);
        isShown = true;
    }

    private void addToCart() {
        if (defaultCartId.equals("")) {
            Toast.makeText(context, "請到購物車頁面選擇預設購物車", Toast.LENGTH_SHORT).show();
            return;
        }

        //跳出對話框詢問數量
        final EditText edtAmount = new EditText(context);
        edtAmount.setInputType(InputType.TYPE_CLASS_NUMBER);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("加入項目至：" + defaultCartName)
                .setMessage("請輸入數量，若要從購物車移除，則輸入0\n\n目前數量：" + Comma(String.valueOf(cartAmount)))
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        amount = Integer.valueOf(edtAmount.getText().toString());
                        if (amount <= Integer.valueOf(tile.getStock()))
                            uploadToCart();
                        else
                            Toast.makeText(context, "超出庫存量", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", null);
        AlertDialog dialog = alert.create();
        dialog.setView(edtAmount, 40, 0, 40, 0);
        dialog.show();
    }

    private void uploadToCart() {
        conn = new MyOkHttp(ProductDetailActivity.this, new MyOkHttp.TaskListener() {
            @Override
            public void onFinished(JSONObject resObj) throws JSONException{
                if (resObj.length() == 0) {
                    Toast.makeText(context, "沒有網路連線", Toast.LENGTH_SHORT).show();
                    prgBar.setVisibility(View.GONE);
                    return;
                }
                if (resObj.getBoolean(KEY_STATUS)) {
                    if(resObj.getBoolean(KEY_SUCCESS)) {
                        cartAmount = amount; //更新車內已有數量
                        if (cartAmount != 0)
                            Toast.makeText(context, "購物車已更新", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(context, "已從購物車移除", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(context, "操作失敗", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, "伺服器發生例外", Toast.LENGTH_SHORT).show();
                }
            }
        });
        try {
            JSONObject reqObj = new JSONObject();
            reqObj.put(KEY_CART_ID, defaultCartId);
            reqObj.put(KEY_PRODUCT_ID, tile.getId());
            reqObj.put(KEY_AMOUNT, amount);
            conn.execute(getString(R.string.link_add_cart_item), reqObj.toString());
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (conn != null)
            conn.cancel();
        if (imageLoader != null)
            imageLoader.cancel(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tile = null;
    }
}
