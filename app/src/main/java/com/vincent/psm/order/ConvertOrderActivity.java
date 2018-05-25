package com.vincent.psm.order;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.vincent.psm.R;
import com.vincent.psm.data.Contact;
import com.vincent.psm.data.Customer;
import com.vincent.psm.network_helper.MyOkHttp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.vincent.psm.data.DataHelper.KEY_CONTACTS;
import static com.vincent.psm.data.DataHelper.KEY_CONTACT_PERSON;
import static com.vincent.psm.data.DataHelper.KEY_CONTACT_PHONE;
import static com.vincent.psm.data.DataHelper.KEY_CUSTOMERS;
import static com.vincent.psm.data.DataHelper.KEY_CUSTOMER_ADDRESS;
import static com.vincent.psm.data.DataHelper.KEY_CUSTOMER_NAME;
import static com.vincent.psm.data.DataHelper.KEY_CUSTOMER_PHONE;
import static com.vincent.psm.data.DataHelper.KEY_DELIVER_FEE;
import static com.vincent.psm.data.DataHelper.KEY_DELIVER_PLACE;
import static com.vincent.psm.data.DataHelper.KEY_ID;
import static com.vincent.psm.data.DataHelper.KEY_PRE_DELIVER_DATE;
import static com.vincent.psm.data.DataHelper.KEY_PRODUCTS_JSON;
import static com.vincent.psm.data.DataHelper.KEY_PS;
import static com.vincent.psm.data.DataHelper.KEY_SALES;
import static com.vincent.psm.data.DataHelper.KEY_SALES_NAME;
import static com.vincent.psm.data.DataHelper.KEY_STATUS;
import static com.vincent.psm.data.DataHelper.KEY_SUCCESS;
import static com.vincent.psm.data.DataHelper.KEY_TOTAL;
import static com.vincent.psm.data.DataHelper.loginUserId;

public class ConvertOrderActivity extends OrderEditActivity {
    private String productsJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layout = R.layout.activity_convert_order;
        toolbarTitle = "建立訂單";
        context = this;
        super.onCreate(savedInstanceState);

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

        conn = new MyOkHttp(ConvertOrderActivity.this, new MyOkHttp.TaskListener() {
            @Override
            public void onFinished(JSONObject resObj) throws JSONException {
                if (resObj.length() == 0) {
                    Toast.makeText(context, "沒有網路連線", Toast.LENGTH_SHORT).show();
                    prgBar.setVisibility(View.GONE);
                    return;
                }
                if (resObj.getBoolean(KEY_STATUS)) {
                    if (resObj.getBoolean(KEY_SUCCESS)) {
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
                        showData();
                    }else {
                        Toast.makeText(context, "沒有任何客戶", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, "伺服器發生例外", Toast.LENGTH_SHORT).show();
                }
            }
        });
        conn.execute(getString(R.string.link_show_customers));
    }

    @Override
    protected void showData() {
        aetCustomerName.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, customerNames));
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
                loadContactData(customers.get(index).getId());
            }
        });

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

    @Override
    protected void loadContactData(int id) {
        conn = new MyOkHttp(ConvertOrderActivity.this, new MyOkHttp.TaskListener() {
            @Override
            public void onFinished(JSONObject resObj) throws JSONException {
                if (resObj.length() == 0) {
                    Toast.makeText(context, "沒有網路連線", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (resObj.getBoolean(KEY_STATUS)) {
                    if(resObj.getBoolean(KEY_SUCCESS)) {
                        JSONArray aryContactPerson = resObj.getJSONArray(KEY_CONTACTS);

                        contacts = new ArrayList<>();
                        contactPersons = new ArrayList<>();
                        contactPersons.add("請選擇");
                        for (int i = 0; i < aryContactPerson.length(); i++) {
                            JSONObject obj = aryContactPerson.getJSONObject(i);
                            contacts.add(new Contact(
                                    obj.getInt(KEY_ID),
                                    obj.getString(KEY_CONTACT_PERSON),
                                    obj.getString(KEY_CONTACT_PHONE)
                            ));
                            contactPersons.add(obj.getString(KEY_CONTACT_PERSON) + "／" + obj.getString(KEY_CONTACT_PHONE));
                        }
                        showContactData();
                    }else {
                        Toast.makeText(context, "沒有聯絡人", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, "伺服器發生例外", Toast.LENGTH_SHORT).show();
                }
            }
        });
        try {
            JSONObject reqObj = new JSONObject();
            reqObj.put(KEY_ID, id);
            conn.execute(getString(R.string.link_show_contacts), reqObj.toString());
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void showContactData() {
        adpContact = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, contactPersons);
        spnContact.setAdapter(adpContact);
    }

    @Override
    protected void uploadOrder() {
        if (!isInfoValid())
            return;

        conn = new MyOkHttp(ConvertOrderActivity.this, new MyOkHttp.TaskListener() {
            @Override
            public void onFinished(JSONObject resObj) throws JSONException {
                if (resObj.length() == 0) {
                    Toast.makeText(context, "沒有網路連線", Toast.LENGTH_SHORT).show();
                    prgBar.setVisibility(View.GONE);
                    return;
                }
                if (resObj.getBoolean(KEY_STATUS)) {
                    if (resObj.getBoolean(KEY_SUCCESS)) {
                        Toast.makeText(context, "建立成功", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(context, "建立訂單失敗", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, "伺服器發生例外", Toast.LENGTH_SHORT).show();
                }
            }
        });
        try {
            JSONObject reqObj = new JSONObject();
            reqObj.put(KEY_CUSTOMER_NAME, order.getCustomerName());
            reqObj.put(KEY_CUSTOMER_PHONE, order.getCustomerPhone());
            reqObj.put(KEY_CUSTOMER_ADDRESS, order.getCustomerAddress());
            reqObj.put(KEY_CONTACT_PERSON, order.getContactPerson());
            reqObj.put(KEY_CONTACT_PHONE, order.getContactPhone());
            reqObj.put(KEY_DELIVER_FEE, order.getDeliverFee());
            reqObj.put(KEY_TOTAL, order.getTotal());
            reqObj.put(KEY_PRE_DELIVER_DATE, order.getPredictDeliverDate());
            reqObj.put(KEY_DELIVER_PLACE, order.getDeliverPlace());
            reqObj.put(KEY_SALES, loginUserId);
            reqObj.put(KEY_PS, order.getPs());
            reqObj.put(KEY_PRODUCTS_JSON, new JSONArray(productsJson));
            Toast.makeText(context, reqObj.getString(KEY_PRODUCTS_JSON), Toast.LENGTH_LONG).show();
            //conn.execute(getString(R.string.link_create_order), reqObj.toString());
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
