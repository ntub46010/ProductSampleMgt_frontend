package com.vincent.psm.product;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.vincent.psm.ImageActivity;
import com.vincent.psm.R;
import com.vincent.psm.broadcast_helper.manager.RequestManager;
import com.vincent.psm.data.Tile;
import com.vincent.psm.network_helper.ImageDownloader;
import com.vincent.psm.network_helper.MyOkHttp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.vincent.psm.data.DataHelper.Comma;
import static com.vincent.psm.data.DataHelper.KEY_AMOUNT;
import static com.vincent.psm.data.DataHelper.KEY_CART_ID;
import static com.vincent.psm.data.DataHelper.KEY_COLOR;
import static com.vincent.psm.data.DataHelper.KEY_ID;
import static com.vincent.psm.data.DataHelper.KEY_CART_AMOUNT;
import static com.vincent.psm.data.DataHelper.KEY_IS_LOWER;
import static com.vincent.psm.data.DataHelper.KEY_LENGTH;
import static com.vincent.psm.data.DataHelper.KEY_MATERIAL;
import static com.vincent.psm.data.DataHelper.KEY_NAME;
import static com.vincent.psm.data.DataHelper.KEY_ONSALE;
import static com.vincent.psm.data.DataHelper.KEY_ORDER_AMOUNT;
import static com.vincent.psm.data.DataHelper.KEY_ORDER_ID;
import static com.vincent.psm.data.DataHelper.KEY_PHOTO;
import static com.vincent.psm.data.DataHelper.KEY_PRICE;
import static com.vincent.psm.data.DataHelper.KEY_PRODUCT_ID;
import static com.vincent.psm.data.DataHelper.KEY_PRODUCT_INFO;
import static com.vincent.psm.data.DataHelper.KEY_PS;
import static com.vincent.psm.data.DataHelper.KEY_ProductAdmin;
import static com.vincent.psm.data.DataHelper.KEY_SAFE_STOCK;
import static com.vincent.psm.data.DataHelper.KEY_STATUS;
import static com.vincent.psm.data.DataHelper.KEY_SUCCESS;
import static com.vincent.psm.data.DataHelper.KEY_STOCK;
import static com.vincent.psm.data.DataHelper.KEY_THICK;
import static com.vincent.psm.data.DataHelper.KEY_WIDTH;
import static com.vincent.psm.data.DataHelper.authority;
import static com.vincent.psm.data.DataHelper.defaultCartId;
import static com.vincent.psm.data.DataHelper.defaultCartName;
import static com.vincent.psm.data.DataHelper.defaultOrderId;
import static com.vincent.psm.data.DataHelper.defaultOrderName;

public class ProductDetailActivity extends AppCompatActivity {
    private Activity activity;
    private ScrollView layProductDetail;
    private ImageView imgProduct;
    private TextView txtProductName, txtPrice, txtId, txtMaterial, txtColor, txtSize, txtPs, txtStock, txtSafeStock;
    private ProgressBar prgBar;
    private FloatingActionButton fabAddCart, fabAddOrder, fabEdit;

    private String id;
    private int currentCartAmount = 0, currentOrderAmount = 0, amount = 0;

    private MyOkHttp conn;
    private ImageDownloader imageLoader;
    private Tile tile;
    private ArrayList<String> productAdmins;

    public static ArrayList<Bitmap> images;
    private boolean isShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        activity = this;

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
        fabAddCart = findViewById(R.id.fabAddCart);
        fabAddOrder = findViewById(R.id.fabAddOrder);
        fabEdit = findViewById(R.id.fabEdit);

        layProductDetail.setVisibility(View.INVISIBLE);

        fabAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart();
            }
        });

        fabAddOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToOrder();
            }
        });

        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(activity, ProductUpdateActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(KEY_ID, id);
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
        prgBar.setVisibility(View.VISIBLE);
        fabAddCart.hide();
        fabAddOrder.hide();
        fabEdit.hide();

        conn = new MyOkHttp(activity, new MyOkHttp.TaskListener() {
            @Override
            public void onFinished(JSONObject resObj) throws JSONException {
                if (resObj.getBoolean(KEY_STATUS)) {
                    if (resObj.getBoolean(KEY_SUCCESS)) {
                        //購物車或訂單內數量
                        currentCartAmount = resObj.getInt(KEY_CART_AMOUNT);
                        currentOrderAmount = resObj.getInt(KEY_ORDER_AMOUNT);

                        //載入產品管理員
                        JSONArray aryAdmin = resObj.getJSONArray(KEY_ProductAdmin);
                        productAdmins = new ArrayList<>();
                        for (int i = 0; i < aryAdmin.length(); i++)
                            productAdmins.add(aryAdmin.getJSONObject(i).getString(KEY_ID));

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
                                obj.getInt(KEY_PRICE),
                                obj.getString(KEY_PS),
                                obj.getInt(KEY_STOCK),
                                obj.getInt(KEY_SAFE_STOCK),
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
                        Toast.makeText(activity, "商品不存在或沒有產品管理員", Toast.LENGTH_SHORT).show();
                        showData();
                    }
                }else {
                    Toast.makeText(activity, "伺服器發生例外", Toast.LENGTH_SHORT).show();
                }
            }
        });
        try {
            JSONObject reqObj = new JSONObject();
            reqObj.put(KEY_ID, id);
            reqObj.put(KEY_CART_ID, defaultCartId);
            reqObj.put(KEY_ORDER_ID, defaultOrderId);
            conn.execute(getString(R.string.link_show_product_detail), reqObj.toString());
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showData() {
        imgProduct.setImageBitmap(tile.getImg());
        txtProductName.setText(tile.getName());
        txtPrice.setText("$ " + Comma(tile.getPrice()));
        txtId.setText(tile.getId());
        txtMaterial.setText(tile.getMaterial());
        txtColor.setText(tile.getColor());
        txtSize.setText(getString(R.string.txt_product_size, tile.getLength(), tile.getWidth(), tile.getThick()));
        txtPs.setText(tile.getPs());
        txtStock.setText(Comma(tile.getStock()));
        txtSafeStock.setText(Comma(tile.getSafeStock()));
        images = new ArrayList<>();
        images.add(tile.getImg());

        if (authority == 1)
            fabAddCart.show();
        else if (authority == 2)
            fabAddOrder.show();
        else if (authority == 3)
            fabEdit.show();

        imgProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(activity, ImageActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("CurrentIndex", 0);
                it.putExtras(bundle);
                startActivity(it);
            }
        });

        layProductDetail.setVisibility(View.VISIBLE);
        prgBar.setVisibility(View.GONE);
        isShown = true;
    }

    private void addToCart() {
        if (defaultCartId.equals("")) {
            Toast.makeText(activity, "請到購物車頁面選擇預設購物車", Toast.LENGTH_SHORT).show();
            return;
        }

        //跳出對話框詢問數量
        LayoutInflater inflater = LayoutInflater.from(activity);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dlg_add_cart_item, null);
        TextView txtAddItemHint = layout.findViewById(R.id.txtAddItemHint);
        txtAddItemHint.setText(getString(R.string.hint_add_item, "購物車", String.valueOf(currentOrderAmount)));
        final EditText edtAmount = layout.findViewById(R.id.edtAmount);

        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle("加入項目至購物車：" + defaultCartName)
                .setView(layout)
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        amount = Integer.valueOf(edtAmount.getText().toString().equals("") ? "-1" : edtAmount.getText().toString());
                        if (amount <= Integer.valueOf(tile.getStock()))
                            uploadToCart();
                        else
                            Toast.makeText(activity, "超出庫存量或未輸入", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void uploadToCart() {
        conn = new MyOkHttp(activity, new MyOkHttp.TaskListener() {
            @Override
            public void onFinished(JSONObject resObj) throws JSONException{
                if (resObj.getBoolean(KEY_STATUS)) {
                    if(resObj.getBoolean(KEY_SUCCESS)) {
                        currentCartAmount = amount; //更新車內已有數量
                        if (currentCartAmount != 0)
                            Toast.makeText(activity, "購物車已更新", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(activity, "已從購物車移除", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(activity, "操作失敗", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(activity, "伺服器發生例外", Toast.LENGTH_SHORT).show();
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

    private void addToOrder() {
        if (defaultOrderId.equals("")) {
            Toast.makeText(activity, "請到訂單頁面選擇預設訂單", Toast.LENGTH_SHORT).show();
            return;
        }

        //跳出對話框詢問數量
        LayoutInflater inflater = LayoutInflater.from(activity);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dlg_add_cart_item, null);
        TextView txtAddItemHint = layout.findViewById(R.id.txtAddItemHint);
        txtAddItemHint.setText(getString(R.string.hint_add_item, "訂單", String.valueOf(currentOrderAmount)));
        final EditText edtAmount = layout.findViewById(R.id.edtAmount);

        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle("加入項目至訂單：" + defaultOrderName)
                .setView(layout)
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        amount = Integer.valueOf(edtAmount.getText().toString().equals("") ? "-1" : edtAmount.getText().toString());
                        if (amount <= Integer.valueOf(tile.getStock()) && amount != -1)
                            uploadToOrder();
                        else
                            Toast.makeText(activity, "超出庫存量或未輸入", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void uploadToOrder() {
        conn = new MyOkHttp(activity, new MyOkHttp.TaskListener() {
            @Override
            public void onFinished(JSONObject resObj) throws JSONException {
                if (resObj.getBoolean(KEY_STATUS)) {
                    if(resObj.getBoolean(KEY_SUCCESS)) {
                        currentOrderAmount = amount; //更新訂單內已有數量
                        if (currentOrderAmount != 0) {
                            Toast.makeText(activity, "訂單已更新", Toast.LENGTH_SHORT).show();

                            //發送庫存不足推播給管理員
                            if (resObj.getBoolean(KEY_IS_LOWER)) {
                                for (String admin : productAdmins) {
                                    RequestManager.getInstance(ProductDetailActivity.this).prepareNotification(
                                            admin,
                                            getString(R.string.title_stock_lower),
                                            getString(R.string.text_stock_lower, tile.getName(), tile.getId(), String.valueOf(tile.getStock())),
                                            null
                                    );
                                }
                            }
                        }else
                            Toast.makeText(activity, "已從訂單移除", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(activity, "操作失敗", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(activity, "伺服器發生例外", Toast.LENGTH_SHORT).show();
                }
            }
        });
        try {
            JSONObject reqObj = new JSONObject();
            reqObj.put(KEY_ORDER_ID, defaultOrderId);
            reqObj.put(KEY_PRODUCT_ID, tile.getId());
            reqObj.put(KEY_AMOUNT, amount);
            conn.execute(getString(R.string.link_add_order_item), reqObj.toString());
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
        tile = null;
        super.onDestroy();
    }
}
