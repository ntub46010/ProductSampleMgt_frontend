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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vincent.psm.R;
import com.vincent.psm.adapter.ProductDisplayAdapter;
import com.vincent.psm.data.Cart;
import com.vincent.psm.data.Tile;
import com.vincent.psm.data.Verifier;
import com.vincent.psm.network_helper.MyOkHttp;
import com.vincent.psm.order.ConvertOrderActivity;
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
import static com.vincent.psm.data.DataHelper.KEY_PRODUCTS_JSON;
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
    private EditText edtCartName, edtCustomerName, edtCustomerPhone, edtContactPerson, edtContactPhone;

    private MyOkHttp conn;
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
        cartId = bundle.getString(KEY_CART_ID);
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
                prepareOrder();
            }
        });
        btnSubmit.setImageResource(R.drawable.icon_arrow_right);

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

        fabInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCartInfo();
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
        fabAdd.hide();
        fabInfo.hide();
        btnSubmit.setVisibility(View.GONE);
        if (showPrgBar)
            prgBar.setVisibility(View.VISIBLE);

        tiles = new ArrayList<>();
        conn = new MyOkHttp(CartDetailActivity.this, new MyOkHttp.TaskListener() {
            @Override
            public void onFinished(JSONObject resObj) throws JSONException {
                if (resObj.length() == 0) {
                    Toast.makeText(context, "沒有網路連線", Toast.LENGTH_SHORT).show();
                    prgBar.setVisibility(View.GONE);
                    return;
                }
                if (resObj.getBoolean(KEY_STATUS)) {
                    //購物車摘要
                    if (resObj.has(KEY_CART_INFO)) {
                        JSONObject objCartInfo = resObj.getJSONObject(KEY_CART_INFO);
                        cart = new Cart(
                                objCartInfo.getString(KEY_SALES_NAME),
                                objCartInfo.getString(KEY_CUSTOMER_NAME),
                                objCartInfo.getString(KEY_CUSTOMER_PHONE),
                                objCartInfo.getString(KEY_CONTACT_PERSON),
                                objCartInfo.getString(KEY_CONTACT_PHONE),
                                objCartInfo.getInt(KEY_TOTAL)
                        );
                    }else {
                        Toast.makeText(context, "購物車不存在", Toast.LENGTH_SHORT).show();
                        prgBar.setVisibility(View.GONE);
                        return;
                    }

                    if (resObj.getBoolean(KEY_SUCCESS)) { //若無KEY_CART_INFO，後續也不必執行
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
                        Toast.makeText(context, "購物車內無產品", Toast.LENGTH_SHORT).show();
                        showData();
                    }
                }else {
                    Toast.makeText(context, "伺服器發生例外", Toast.LENGTH_SHORT).show();
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
        btnSubmit.setVisibility(View.VISIBLE);
        prgBar.setVisibility(View.GONE);
        recyProduct.setVisibility(View.VISIBLE);
        tiles = null;
        isShown = true;
    }

    private void showCartInfo() {
        LayoutInflater inflater = LayoutInflater.from(context);
        TableLayout layout = (TableLayout) inflater.inflate(R.layout.dlg_cart_summary, null);
        TextView txtSales = layout.findViewById(R.id.txtSalesName);
        TextView txtCustomerName = layout.findViewById(R.id.txtCustomerName);
        TextView txtCustomerPhone = layout.findViewById(R.id.txtCustomerPhone);
        TextView txtContactPerson = layout.findViewById(R.id.txtContactPerson);
        TextView txtContactPhone = layout.findViewById(R.id.txtContactPhone);
        TextView txtTotal = layout.findViewById(R.id.txtTotal);

        txtSales.setText(cart.getSalesName());
        txtCustomerName.setText(cart.getCustomerName());
        txtCustomerPhone.setText(cart.getCustomerPhone());
        txtContactPerson.setText(cart.getContactPerson());
        txtContactPhone.setText(cart.getContactPhone());
        txtTotal.setText("$ " + Comma(String.valueOf(cart.getTotal())));

        AlertDialog.Builder msgbox = new AlertDialog.Builder(context);
        msgbox.setTitle("購物車摘要")
                .setView(layout)
                .setCancelable(true)
                .setPositiveButton("確定", null)
                .setNegativeButton("編輯摘要", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        prepareEditCartInfo();
                    }
                })
                .setNeutralButton("刪除購物車", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        prepareDeleteCart();
                    }
                })
                .show();
    }

    private void prepareEditCartInfo() {
        LayoutInflater inflater = LayoutInflater.from(context);
        TableLayout layout = (TableLayout) inflater.inflate(R.layout.dlg_create_cart, null);

        edtCartName = layout.findViewById(R.id.edtCartName);
        edtCustomerName = layout.findViewById(R.id.edtCustomerName);
        edtCustomerPhone = layout.findViewById(R.id.edtCustomerPhone);
        edtContactPerson = layout.findViewById(R.id.edtContactPerson);
        edtContactPhone = layout.findViewById(R.id.edtContactPhone);

        edtCartName.setText(cartName);
        edtCustomerName.setText(cart.getCustomerName());
        edtCustomerPhone.setText(cart.getCustomerPhone());
        edtContactPerson.setText(cart.getContactPerson());
        edtContactPhone.setText(cart.getContactPhone());

        AlertDialog.Builder dlgCreateCart = new AlertDialog.Builder(context);
        dlgCreateCart.setTitle("編輯購物車")
                .setMessage("請輸入購物車資料")
                .setView(layout)
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateCartInfo();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void prepareDeleteCart() {
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

    private void updateCartInfo() {
        if (!isInfoValid())
            return;

        conn = new MyOkHttp(CartDetailActivity.this, new MyOkHttp.TaskListener() {
            @Override
            public void onFinished(JSONObject resObj) throws JSONException {
                if (resObj.length() == 0) {
                    Toast.makeText(context, "沒有網路連線", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (resObj.getBoolean(KEY_STATUS)) {
                    if (resObj.getBoolean(KEY_SUCCESS)) {
                        Toast.makeText(context, "編輯成功", Toast.LENGTH_SHORT).show();
                        cartName = cart.getCartName();
                        recyProduct.setVisibility(View.INVISIBLE);
                        adapter.destroy(false);
                        loadData(true);
                    }else {
                        Toast.makeText(context, "編輯失敗", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, "伺服器發生例外", Toast.LENGTH_SHORT).show();
                }
            }
        });
        try {
            JSONObject reqObj = new JSONObject();
            reqObj.put(KEY_CART_ID, cartId);
            reqObj.put(KEY_CART_NAME, cart.getCartName());
            reqObj.put(KEY_CUSTOMER_NAME, cart.getCustomerName());
            reqObj.put(KEY_CUSTOMER_PHONE, cart.getCustomerPhone());
            reqObj.put(KEY_CONTACT_PERSON, cart.getContactPerson());
            reqObj.put(KEY_CONTACT_PHONE, cart.getContactPhone());
            conn.execute(getString(R.string.link_edit_cart), reqObj.toString());
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void deleteCart() {
        conn = new MyOkHttp(CartDetailActivity.this, new MyOkHttp.TaskListener() {
            @Override
            public void onFinished(JSONObject resObj) throws JSONException {
                if (resObj.length() == 0) {
                    Toast.makeText(context, "沒有網路連線", Toast.LENGTH_SHORT).show();
                    prgBar.setVisibility(View.GONE);
                    return;
                }
                if (resObj.getBoolean(KEY_STATUS)) {
                    if (resObj.getBoolean(KEY_SUCCESS)) {
                        if (cartId.equals(defaultCartId))
                            defaultCartId = "";
                        Toast.makeText(context, "已刪除購物車：" + cartName, Toast.LENGTH_SHORT).show();
                        finish();
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
            reqObj.put(KEY_CART_ID, cartId);
            reqObj.put(KEY_SALES_ID, loginUserId);
            conn.execute(getString(R.string.link_delete_cart), reqObj.toString());
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void prepareOrder() {
        //產生產品陣列的JSON
        JSONArray ary = new JSONArray();
        try {
            JSONObject obj;
            for (int i = 0; i < adapter.getItemCount(); i++) {
                obj = new JSONObject();
                obj.put(KEY_ID, adapter.getItem(i).getId());
                obj.put(KEY_AMOUNT, adapter.getItem(i).getAmount());
                ary.put(obj);
            }
        }catch (JSONException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        Intent it = new Intent(context, ConvertOrderActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_SALES_NAME, cart.getSalesName());
        bundle.putString(KEY_CUSTOMER_NAME, cart.getCustomerName());
        bundle.putString(KEY_CUSTOMER_PHONE, cart.getCustomerPhone());
        bundle.putString(KEY_CONTACT_PERSON, cart.getContactPerson());
        bundle.putString(KEY_CONTACT_PHONE, cart.getContactPhone());
        bundle.putInt(KEY_TOTAL, cart.getTotal());
        bundle.putString(KEY_PRODUCTS_JSON, ary.toString());
        it.putExtras(bundle);
        startActivity(it);
    }

    private boolean isInfoValid() {
        Verifier v = new Verifier(context);
        StringBuffer errMsg = new StringBuffer();

        cart = null;
        cart = new Cart(
                edtCartName.getText().toString(),
                edtCustomerName.getText().toString(),
                edtCustomerPhone.getText().toString(),
                edtContactPerson.getText().toString(),
                edtContactPhone.getText().toString()
        );

        errMsg.append(v.chkCartName(cart.getCartName()));
        errMsg.append(v.chkCustomerName(cart.getCustomerName()));
        errMsg.append(v.chkCustomerPhone(cart.getCustomerName()));
        errMsg.append(v.chkContactName(cart.getContactPerson()));
        errMsg.append(v.chkContactPhone(cart.getContactPhone()));

        if (errMsg.length() != 0) {
            v.getDialog("購物車資料錯誤", errMsg.substring(0, errMsg.length() - 1)).show();
            return false;
        }else {
            return true;
        }
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
