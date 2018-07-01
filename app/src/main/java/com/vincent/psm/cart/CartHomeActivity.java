package com.vincent.psm.cart;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.Toast;

import com.vincent.psm.R;
import com.vincent.psm.adapter.CartListAdapter;
import com.vincent.psm.data.Cart;
import com.vincent.psm.data.Verifier;
import com.vincent.psm.network_helper.MyOkHttp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.vincent.psm.data.DataHelper.KEY_CARTS;
import static com.vincent.psm.data.DataHelper.KEY_CART_ID;
import static com.vincent.psm.data.DataHelper.KEY_CART_NAME;
import static com.vincent.psm.data.DataHelper.KEY_CONTACT_PERSON;
import static com.vincent.psm.data.DataHelper.KEY_CONTACT_PHONE;
import static com.vincent.psm.data.DataHelper.KEY_CUSTOMER_NAME;
import static com.vincent.psm.data.DataHelper.KEY_CUSTOMER_PHONE;
import static com.vincent.psm.data.DataHelper.KEY_ID;
import static com.vincent.psm.data.DataHelper.KEY_SALES;
import static com.vincent.psm.data.DataHelper.KEY_SALES_ID;
import static com.vincent.psm.data.DataHelper.KEY_STATUS;
import static com.vincent.psm.data.DataHelper.KEY_SUCCESS;
import static com.vincent.psm.data.DataHelper.KEY_CREATE_TIME;
import static com.vincent.psm.data.DataHelper.KEY_TOTAL;
import static com.vincent.psm.data.DataHelper.defaultCartId;
import static com.vincent.psm.data.DataHelper.defaultCartName;
import static com.vincent.psm.data.DataHelper.loginUserId;

public class CartHomeActivity extends AppCompatActivity {
    private Activity activity;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView lstCart;
    private FloatingActionButton fabCreate;
    private ProgressBar prgBar;

    private MyOkHttp conn;
    private ArrayList<Cart> carts;
    private CartListAdapter adapter;
    private Cart cart;

    private boolean isShown = false;

    private EditText edtCartName, edtCustomerName, edtCustomerPhone, edtContactPerson, edtContactPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_home);
        activity = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("購物車");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        lstCart = findViewById(R.id.lstCart);
        fabCreate = findViewById(R.id.fabCreate);
        prgBar = findViewById(R.id.prgBar);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(false);
            }
        });
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light);

        lstCart.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cart cart = (Cart) adapter.getItem(position);
                if (cart.getSalesId().equals(loginUserId)) {
                    Intent it = new Intent(activity, CartDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(KEY_CART_ID, cart.getId());
                    bundle.putString(KEY_CART_NAME, cart.getCartName());
                    it.putExtras(bundle);
                    startActivity(it);
                }else
                    Toast.makeText(activity, "這不是您的購物車", Toast.LENGTH_SHORT).show();
            }
        });
        lstCart.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final Cart cart = (Cart) adapter.getItem(position);
                if (cart.getSalesId().equals(loginUserId)) {
                    AlertDialog.Builder msgbox = new AlertDialog.Builder(activity);
                    msgbox.setTitle("購物車")
                            .setMessage("要設定為預設購物車嗎？")
                            .setCancelable(true)
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    defaultCartId = cart.getId();
                                    defaultCartName = cart.getCartName();
                                }
                            })
                            .setNegativeButton("否", null)
                            .show();
                }
                return true;
            }
        });

        fabCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCart();
            }
        });
        fabCreate.hide();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isShown)
            loadData(true);
    }

    private void loadData(boolean showPrgBar) {
        isShown = false;
        swipeRefreshLayout.setEnabled(false);
        if (showPrgBar)
            prgBar.setVisibility(View.VISIBLE);

        carts = new ArrayList<>();
        conn = new MyOkHttp(activity, new MyOkHttp.TaskListener() {
            @Override
            public void onFinished(final JSONObject resObj) throws JSONException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (resObj.getBoolean(KEY_STATUS)) {
                                if (resObj.getBoolean(KEY_SUCCESS)) {
                                    JSONArray ary = resObj.getJSONArray(KEY_CARTS);
                                    for (int i = 0; i < ary.length(); i++) {
                                        JSONObject obj = ary.getJSONObject(i);
                                        carts.add(new Cart(
                                                obj.getString(KEY_ID),
                                                obj.getString(KEY_CART_NAME),
                                                obj.getInt(KEY_TOTAL),
                                                obj.getString(KEY_CREATE_TIME),
                                                obj.getString(KEY_SALES_ID)
                                        ));
                                    }
                                    showData();
                                }else {
                                    Toast.makeText(activity, "沒有購物車", Toast.LENGTH_SHORT).show();
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
            reqObj.put(KEY_SALES_ID, loginUserId);
            conn.execute(getString(R.string.link_list_carts), reqObj.toString());
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showData() {
        adapter = new CartListAdapter(activity, carts);
        lstCart.setAdapter(adapter);

        swipeRefreshLayout.setEnabled(true);
        swipeRefreshLayout.setRefreshing(false);

        fabCreate.show();
        prgBar.setVisibility(View.GONE);
        isShown = true;
    }

    private void createCart() {
        LayoutInflater inflater = LayoutInflater.from(activity);
        TableLayout layout = (TableLayout) inflater.inflate(R.layout.dlg_create_cart, null);

        edtCartName = layout.findViewById(R.id.edtCartName);
        edtCustomerName = layout.findViewById(R.id.edtCustomerName);
        edtCustomerPhone = layout.findViewById(R.id.edtCustomerPhone);
        edtContactPerson = layout.findViewById(R.id.edtContactPerson);
        edtContactPhone = layout.findViewById(R.id.edtContactPhone);

        AlertDialog.Builder dlgCreateCart = new AlertDialog.Builder(this);
        dlgCreateCart.setTitle("建立購物車")
                .setMessage("請輸入購物車資料")
                .setView(layout)
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                uploadNewCart();
            }
        })
                .setNegativeButton("取消", null)
                .show();
    }

    private void uploadNewCart() {
        if (!isInfoValid())
            return;

        conn = new MyOkHttp(CartHomeActivity.this, new MyOkHttp.TaskListener() {
            @Override
            public void onFinished(final JSONObject resObj) throws JSONException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (resObj.getBoolean(KEY_STATUS)) {
                                if (resObj.getBoolean(KEY_SUCCESS)) {
                                    Toast.makeText(activity, "建立成功", Toast.LENGTH_SHORT).show();
                                    loadData(true);
                                }else {
                                    Toast.makeText(activity, "建立購物車失敗", Toast.LENGTH_SHORT).show();
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
            reqObj.put(KEY_CART_NAME, cart.getCartName());
            reqObj.put(KEY_CUSTOMER_NAME, cart.getCustomerName());
            reqObj.put(KEY_CUSTOMER_PHONE, cart.getCustomerPhone());
            reqObj.put(KEY_CONTACT_PERSON, cart.getContactPerson());
            reqObj.put(KEY_CONTACT_PHONE, cart.getContactPhone());
            reqObj.put(KEY_SALES, loginUserId);
            conn.execute(getString(R.string.link_create_cart), reqObj.toString());
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean isInfoValid() {
        Verifier v = new Verifier(activity);
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
        errMsg.append(v.chkCustomerPhone(cart.getCustomerPhone()));
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
        super.onDestroy();
    }
}
