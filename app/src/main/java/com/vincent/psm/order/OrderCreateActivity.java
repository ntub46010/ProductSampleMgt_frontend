package com.vincent.psm.order;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.vincent.psm.R;
import com.vincent.psm.broadcast_helper.manager.RequestManager;
import com.vincent.psm.data.Customer;
import com.vincent.psm.network_helper.MyOkHttp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.vincent.psm.data.DataHelper.KEY_CART_ID;
import static com.vincent.psm.data.DataHelper.KEY_CONTACT_PERSON;
import static com.vincent.psm.data.DataHelper.KEY_CONTACT_PHONE;
import static com.vincent.psm.data.DataHelper.KEY_CUSTOMERS;
import static com.vincent.psm.data.DataHelper.KEY_CUSTOMER_ADDRESS;
import static com.vincent.psm.data.DataHelper.KEY_CUSTOMER_NAME;
import static com.vincent.psm.data.DataHelper.KEY_CUSTOMER_PHONE;
import static com.vincent.psm.data.DataHelper.KEY_ID;
import static com.vincent.psm.data.DataHelper.KEY_IS_LOWER;
import static com.vincent.psm.data.DataHelper.KEY_NAME;
import static com.vincent.psm.data.DataHelper.KEY_ORDER_ID;
import static com.vincent.psm.data.DataHelper.KEY_PRODUCTS;
import static com.vincent.psm.data.DataHelper.KEY_PRODUCTS_JSON;
import static com.vincent.psm.data.DataHelper.KEY_ProductAdmin;
import static com.vincent.psm.data.DataHelper.KEY_SALES;
import static com.vincent.psm.data.DataHelper.KEY_SALES_NAME;
import static com.vincent.psm.data.DataHelper.KEY_STATUS;
import static com.vincent.psm.data.DataHelper.KEY_SUCCESS;
import static com.vincent.psm.data.DataHelper.KEY_TOTAL;
import static com.vincent.psm.data.DataHelper.KEY_WAREHOUSE;
import static com.vincent.psm.data.DataHelper.loginUserId;

public class OrderCreateActivity extends OrderEditActivity {
    private String cartId;
    private String productsJson;
    private ArrayList<String> warehouses, productAdmins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layout = R.layout.activity_convert_order;
        toolbarTitle = "建立訂單";
        activity = this;
        super.onCreate(savedInstanceState);

        cartId = getIntent().getExtras().getString(KEY_CART_ID);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareUpload();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void loadData() {
        layEditContent.setVisibility(View.INVISIBLE);
        btnSubmit.setVisibility(View.GONE);
        prgBar.setVisibility(View.VISIBLE);

        conn = new MyOkHttp(activity, new MyOkHttp.TaskListener() {
            @Override
            public void onFinished(JSONObject resObj) throws JSONException {
                if (resObj.length() == 0) {
                    Toast.makeText(activity, "沒有網路連線", Toast.LENGTH_SHORT).show();
                    prgBar.setVisibility(View.GONE);
                    return;
                }
                if (resObj.getBoolean(KEY_STATUS)) {
                    if (resObj.getBoolean(KEY_SUCCESS)) {
                        //客戶
                        JSONArray aryCustomerName = resObj.getJSONArray(KEY_CUSTOMERS);
                        customers = new ArrayList<>();
                        customerNames = new ArrayList<>();
                        for (int i = 0; i < aryCustomerName.length(); i++) {
                            JSONObject obj = aryCustomerName.getJSONObject(i);
                            customers.add(new Customer(
                                    obj.getInt(KEY_ID),
                                    obj.getString(KEY_CUSTOMER_NAME),
                                    obj.getString(KEY_CUSTOMER_PHONE),
                                    obj.getString(KEY_CUSTOMER_ADDRESS)
                            ));
                            customerNames.add(obj.getString(KEY_CUSTOMER_NAME));
                        }

                        //載入倉管人員
                        JSONArray aryWarehouse = resObj.getJSONArray(KEY_WAREHOUSE);
                        warehouses = new ArrayList<>();
                        for (int i = 0; i < aryWarehouse.length(); i++)
                            warehouses.add(aryWarehouse.getJSONObject(i).getString(KEY_ID));

                        //載入產品管理員
                        JSONArray aryAdmin = resObj.getJSONArray(KEY_ProductAdmin);
                        productAdmins = new ArrayList<>();
                        for (int i = 0; i < aryAdmin.length(); i++)
                            productAdmins.add(aryAdmin.getJSONObject(i).getString(KEY_ID));

                        showData();
                    }else {
                        Toast.makeText(activity, "沒有任何客戶或倉管人員", Toast.LENGTH_SHORT).show();
                        showData();
                    }
                }else {
                    Toast.makeText(activity, "伺服器發生例外", Toast.LENGTH_SHORT).show();
                }
            }
        });
        conn.execute(getString(R.string.link_show_customers_warehouses));
    }

    @Override
    protected void showData() {
        //客戶清單
        aetCustomerName.setAdapter(new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, customerNames));
        aetCustomerName.setThreshold(1);
        aetCustomerName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int index = 0;
                for (int i = 0; i < customers.size(); i++) {
                    if (customers.get(i).getName().equals(aetCustomerName.getText().toString())) {
                        index = i;
                        break;
                    }
                }
                edtCustomerPhone.setText(customers.get(index).getPhone());
                edtCustomerAddress.setText(customers.get(index).getAddress());
                loadContactData(OrderCreateActivity.this, customers.get(index).getId());
            }
        });

        //購物車資料
        Bundle bundle = getIntent().getExtras();
        aetCustomerName.setText(bundle.getString(KEY_CUSTOMER_NAME));
        edtCustomerPhone.setText(bundle.getString(KEY_CUSTOMER_PHONE));
        edtContactPerson.setText(bundle.getString(KEY_CONTACT_PERSON));
        edtContactPhone.setText(bundle.getString(KEY_CONTACT_PHONE));
        edtProductTotal.setText(String.valueOf(bundle.getInt(KEY_TOTAL)));
        edtSales.setText(bundle.getString(KEY_SALES_NAME));
        productsJson = bundle.getString(KEY_PRODUCTS_JSON);

        layEditContent.setVisibility(View.VISIBLE);
        btnSubmit.setVisibility(View.VISIBLE);
        prgBar.setVisibility(View.GONE);
        isShown = true;
    }

    private void prepareUpload() {
        conn = new MyOkHttp(activity, new MyOkHttp.TaskListener() {
            @Override
            public void onFinished(JSONObject resObj) throws JSONException {
                if (resObj.length() == 0) {
                    Toast.makeText(activity, "沒有網路連線", Toast.LENGTH_SHORT).show();
                    prgBar.setVisibility(View.GONE);
                    return;
                }
                if (resObj.getBoolean(KEY_STATUS)) {
                    if (resObj.getBoolean(KEY_SUCCESS)) {
                        uploadOrder();
                    }else {
                        dlgUpload.dismiss();

                        StringBuffer msg = new StringBuffer();
                        msg.append("以下產品庫存量不足：\n");

                        JSONArray ary = resObj.getJSONArray(KEY_PRODUCTS);
                        for (int i = 0; i < ary.length(); i++) {
                            JSONObject obj = ary.getJSONObject(i);
                            msg.append(obj.getString(KEY_NAME)).append("\n");
                        }

                        AlertDialog.Builder msgbox = new AlertDialog.Builder(activity);
                        msgbox.setTitle(toolbarTitle)
                                .setMessage(msg.toString().substring(0, msg.length() - 1))
                                .setCancelable(true)
                                .show();
                    }
                }else {
                    Toast.makeText(activity, "伺服器發生例外", Toast.LENGTH_SHORT).show();
                }
            }
        });
        try {
            JSONObject reqObj = new JSONObject();
            reqObj.put(KEY_CART_ID, cartId);
            conn.execute(getString(R.string.link_list_cart_unenough_item), reqObj.toString());

            txtUploadHint.setText("檢查庫存中...");
            dlgUpload.show();
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void uploadOrder() {
        if (!isInfoValid())
            return;

        conn = new MyOkHttp(activity, new MyOkHttp.TaskListener() {
            @Override
            public void onFinished(JSONObject resObj) throws JSONException {
                dlgUpload.dismiss();
                if (resObj.length() == 0) {
                    Toast.makeText(activity, "沒有網路連線", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (resObj.getBoolean(KEY_STATUS)) {
                    if (resObj.getBoolean(KEY_SUCCESS)) {
                        Toast.makeText(activity, "建立成功", Toast.LENGTH_SHORT).show();

                        //發送新訂單推播給倉管人員
                        for (String warehouse: warehouses) {
                            RequestManager.getInstance(OrderCreateActivity.this).prepareNotification(
                                    warehouse,
                                    getString(R.string.title_new_order),
                                    getString(R.string.text_new_order, order.getCustomerName()),
                                    null
                            );

                            //發送庫存不足推播給管理員
                            if (resObj.getBoolean(KEY_IS_LOWER)) {
                                for (String admin : productAdmins) {
                                    RequestManager.getInstance(OrderCreateActivity.this).prepareNotification(
                                            admin,
                                            getString(R.string.title_stock_lower),
                                            getString(R.string.text_some_stock_lower),
                                            null
                                    );
                                }
                            }
                        }

                        Intent it = new Intent(activity, OrderDetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString(KEY_ORDER_ID, String.valueOf(resObj.getInt(KEY_ORDER_ID)));
                        it.putExtras(bundle);
                        startActivity(it);
                        finish();
                    }else {
                        Toast.makeText(activity, "建立失敗", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(activity, "伺服器發生例外", Toast.LENGTH_SHORT).show();
                }
            }
        });
        try {
            setUploadReqObj();
            reqObj.put(KEY_SALES, loginUserId);
            reqObj.put(KEY_PRODUCTS, new JSONArray(productsJson));
            conn.execute(getString(R.string.link_create_order), reqObj.toString());

            txtUploadHint.setText("上傳中...");
            dlgUpload.show();
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
