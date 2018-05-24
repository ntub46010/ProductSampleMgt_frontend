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

import static com.vincent.psm.data.DataHelper.KEY_CONTACT_PERSON;
import static com.vincent.psm.data.DataHelper.KEY_CONTACT_PERSONS;
import static com.vincent.psm.data.DataHelper.KEY_CONTACT_PHONE;
import static com.vincent.psm.data.DataHelper.KEY_CUSTOMERS;
import static com.vincent.psm.data.DataHelper.KEY_CUSTOMER_ADDRESS;
import static com.vincent.psm.data.DataHelper.KEY_CUSTOMER_NAME;
import static com.vincent.psm.data.DataHelper.KEY_CUSTOMER_PHONE;
import static com.vincent.psm.data.DataHelper.KEY_ID;
import static com.vincent.psm.data.DataHelper.KEY_SALES_NAME;
import static com.vincent.psm.data.DataHelper.KEY_STATUS;
import static com.vincent.psm.data.DataHelper.KEY_SUCCESS;
import static com.vincent.psm.data.DataHelper.KEY_TOTAL;

public class ConvertOrderActivity extends OrderEditActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layout = R.layout.activity_convert_order;
        toolbarTitle = "建立訂單";
        context = this;
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        aetCustomerName.setText(bundle.getString(KEY_CUSTOMER_NAME));
        edtCustomerPhone.setText(bundle.getString(KEY_CUSTOMER_PHONE));
        edtNewContact.setText(bundle.getString(KEY_CONTACT_PERSON));
        edtContactPhone.setText(bundle.getString(KEY_CUSTOMER_PHONE));
        edtProductTotal.setText(String.valueOf(bundle.getInt(KEY_TOTAL)));
        edtSales.setText(bundle.getString(KEY_SALES_NAME));
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

        conDownload = new MyOkHttp(ConvertOrderActivity.this, new MyOkHttp.TaskListener() {
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
        conDownload.execute(getString(R.string.link_show_customers));
    }

    @Override
    protected void showData() {

        //客戶
        aetCustomerName.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, customerNames));
        aetCustomerName.setThreshold(1);


        aetCustomerName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                edtCustomerAddress.setText(customers.get(position).getAddress());
                //loadContactData(customers.get(position).getId());
            }
        });

        layEditContent.setVisibility(View.VISIBLE);
        btnSubmit.setVisibility(View.VISIBLE);
        prgBar.setVisibility(View.GONE);
        isShown = true;

    }

    @Override
    protected void loadContactData(int id) {
        conDownload = new MyOkHttp(ConvertOrderActivity.this, new MyOkHttp.TaskListener() {
            @Override
            public void onFinished(JSONObject resObj) throws JSONException {
                if (resObj.length() == 0) {
                    Toast.makeText(context, "沒有網路連線", Toast.LENGTH_SHORT).show();
                    prgBar.setVisibility(View.GONE);
                    return;
                }
                if (resObj.getBoolean(KEY_STATUS)) {
                    if(resObj.getBoolean(KEY_SUCCESS)) {
                        JSONArray aryContactPerson = resObj.getJSONArray(KEY_CONTACT_PERSONS);

                        contacts = new ArrayList<>();
                        contactPersons = new ArrayList<>();
                        for (int i = 0; i < aryContactPerson.length(); i++) {
                            JSONObject obj = aryContactPerson.getJSONObject(i);
                            contacts.add(new Contact(
                                    obj.getInt(KEY_ID),
                                    obj.getString(KEY_CONTACT_PERSON),
                                    obj.getString(KEY_CONTACT_PHONE)
                            ));
                            contactPersons.add(obj.getString(KEY_CONTACT_PHONE));
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
            conDownload.execute(getString(R.string.link_show_contacts), reqObj.toString());
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void showContactData() {

    }
}
