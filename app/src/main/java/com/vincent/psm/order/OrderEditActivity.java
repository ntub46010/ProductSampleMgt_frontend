package com.vincent.psm.order;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.vincent.psm.R;
import com.vincent.psm.data.Contact;
import com.vincent.psm.data.Customer;
import com.vincent.psm.data.Order;
import com.vincent.psm.data.Verifier;
import com.vincent.psm.network_helper.MyOkHttp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.vincent.psm.data.DataHelper.KEY_CONTACTS;
import static com.vincent.psm.data.DataHelper.KEY_CONTACT_PERSON;
import static com.vincent.psm.data.DataHelper.KEY_CONTACT_PHONE;
import static com.vincent.psm.data.DataHelper.KEY_CUSTOMER_ADDRESS;
import static com.vincent.psm.data.DataHelper.KEY_CUSTOMER_NAME;
import static com.vincent.psm.data.DataHelper.KEY_CUSTOMER_PHONE;
import static com.vincent.psm.data.DataHelper.KEY_DELIVER_FEE;
import static com.vincent.psm.data.DataHelper.KEY_DELIVER_PLACE;
import static com.vincent.psm.data.DataHelper.KEY_ID;
import static com.vincent.psm.data.DataHelper.KEY_PRE_DELIVER_DATE;
import static com.vincent.psm.data.DataHelper.KEY_PRODUCT_TOTAL;
import static com.vincent.psm.data.DataHelper.KEY_PS;
import static com.vincent.psm.data.DataHelper.KEY_STATUS;
import static com.vincent.psm.data.DataHelper.KEY_SUCCESS;

public abstract class OrderEditActivity extends AppCompatActivity {
    protected Context context;
    protected int layout;
    protected String toolbarTitle;

    protected LinearLayout layEditContent;
    protected AutoCompleteTextView aetCustomerName;
    protected EditText edtCustomerPhone, edtCustomerAddress, edtContactPerson, edtContactPhone, edtProductTotal, edtDeliverFee,
                        edtPreDeliverDate, edtDeliverPlace, edtPs, edtSales;
    protected Spinner spnContact;
    protected RadioButton rdoSelectContact, rdoNewContact;

    protected ProgressBar prgBar;
    protected ImageView btnSubmit;

    protected MyOkHttp conn;
    protected JSONObject reqObj;

    protected Order order;
    private String contactPerson, contactPhone;
    protected String actualDeliverDate;

    protected ArrayList<String> customerNames, contactPersons;
    protected ArrayList<Customer> customers;
    protected ArrayList<Contact> contacts;
    protected ArrayAdapter<String> adpContact;

    protected boolean isShown = false;

    protected abstract void loadData();
    protected abstract void showData();
    protected abstract void uploadOrder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView txtBarTitle = toolbar.findViewById(R.id.txtToolbarTitle);
        txtBarTitle.setText(toolbarTitle);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        layEditContent = findViewById(R.id.layEditContent);
        aetCustomerName = findViewById(R.id.aetCustomerName);
        edtCustomerPhone = findViewById(R.id.edtCustomerPhone);
        edtCustomerAddress = findViewById(R.id.edtCustomerAddress);
        edtContactPerson = findViewById(R.id.edtContactName);
        edtContactPhone = findViewById(R.id.edtContactPhone);
        edtProductTotal = findViewById(R.id.edtProductTotal);
        edtDeliverFee = findViewById(R.id.edtDeliverFee);
        edtPreDeliverDate = findViewById(R.id.edtPreDeliverDate);
        edtDeliverPlace = findViewById(R.id.edtDeliverPlace);
        edtPs = findViewById(R.id.edtPs);
        edtSales = findViewById(R.id.edtSales);
        spnContact = findViewById(R.id.spnContact);
        rdoSelectContact = findViewById(R.id.rdoSelectContact);
        rdoNewContact = findViewById(R.id.rdoNewContact);
        prgBar = findViewById(R.id.prgBar);
        btnSubmit = findViewById(R.id.btnSubmit);

        spnContact.setEnabled(false);
        spnContact.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String s = parent.getSelectedItem().toString();
                if (s.equals("請選擇")) {
                    contactPerson = "";
                    contactPhone = "";
                    edtContactPerson.setText("");
                    edtContactPhone.setText("");
                }else {
                    String[] contact = s.split("／");
                    contactPerson = contact[0];
                    contactPhone = contact[1];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadOrder();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isShown)
            loadData();
    }

    protected void loadContactData(Activity activity, int id) {
        conn = new MyOkHttp(activity, new MyOkHttp.TaskListener() {
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

    private void showContactData() {
        adpContact = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, contactPersons);
        spnContact.setAdapter(adpContact);
    }

    protected boolean isInfoValid() {
        Verifier v = new Verifier(context);
        StringBuffer errMsg = new StringBuffer();

        if (rdoNewContact.isChecked()) {
            contactPerson = edtContactPerson.getText().toString();
            contactPhone = edtContactPhone.getText().toString();
        }

        String productTotal = edtProductTotal.getText().toString();
        String deliverFee = edtDeliverFee.getText().toString();
        productTotal = productTotal.equals("") ? "12345678" : productTotal; //預設超出範圍，使驗證失敗
        deliverFee = deliverFee.equals("") ? "0" : deliverFee; //預設為0

        order = null;
        order = new Order(
                aetCustomerName.getText().toString(),
                edtCustomerPhone.getText().toString(),
                edtCustomerAddress.getText().toString(),
                contactPerson,
                contactPhone,
                Integer.parseInt(productTotal),
                Integer.parseInt(deliverFee),
                edtPreDeliverDate.getText().toString(),
                actualDeliverDate,
                edtDeliverPlace.getText().toString(),
                edtPs.getText().toString()
        );

        errMsg.append(v.chkName("客戶名稱", order.getCustomerName()));
        errMsg.append(v.chkPhone("客戶電話", order.getCustomerPhone()));
        errMsg.append(v.chkAddress(order.getCustomerAddress()));
        errMsg.append(v.chkName("聯絡人名稱", order.getContactPerson()));
        errMsg.append(v.chkPhone("聯絡人電話", order.getContactPhone()));
        errMsg.append(v.chkProductTotal(String.valueOf(order.getTotal())));
        errMsg.append(v.chkDeliverFee(String.valueOf(order.getDeliverFee())));
        //預計到貨日、實際到貨日暫略
        errMsg.append(v.chkAddress(order.getDeliverPlace()));
        errMsg.append(v.chkPs(order.getPs()));

        if (order.getCustomerAddress().equals("") && order.getDeliverPlace().equals(""))
            errMsg.append("客戶地址與送貨地點需填寫至少一個\n");

        if (errMsg.length() != 0) {
            v.getDialog(toolbarTitle, errMsg.substring(0, errMsg.length() - 1)).show();
            return false;
        }else {
            if (order.getDeliverPlace().equals("")) //送貨地點預設為客戶公司地址
                order.setDeliverPlace(order.getCustomerAddress());
            return true;
        }
    }

    protected void setUploadReqObj() throws JSONException {
        reqObj = new JSONObject();
        reqObj.put(KEY_CUSTOMER_NAME, order.getCustomerName());
        reqObj.put(KEY_CUSTOMER_PHONE, order.getCustomerPhone());
        reqObj.put(KEY_CUSTOMER_ADDRESS, order.getCustomerAddress());
        reqObj.put(KEY_CONTACT_PERSON, order.getContactPerson());
        reqObj.put(KEY_CONTACT_PHONE, order.getContactPhone());
        reqObj.put(KEY_PRODUCT_TOTAL, order.getTotal());
        reqObj.put(KEY_DELIVER_FEE, order.getDeliverFee());
        reqObj.put(KEY_PRE_DELIVER_DATE, order.getPredictDeliverDate());
        reqObj.put(KEY_DELIVER_PLACE, order.getDeliverPlace());
        reqObj.put(KEY_PS, order.getPs());
    }

    public void onRadioSelect (View view) {
        switch (view.getId()) {
            case R.id.rdoSelectContact:
                rdoNewContact.setChecked(false);
                spnContact.setEnabled(true);
                edtContactPerson.setEnabled(false);
                edtContactPhone.setEnabled(false);
                break;

            case R.id.rdoNewContact:
                rdoSelectContact.setChecked(false);
                spnContact.setEnabled(false);
                edtContactPerson.setEnabled(true);
                edtContactPhone.setEnabled(true);
                break;
        }
    }

    @Override
    public void onDestroy() {
        if (conn != null)
            conn.cancel();

        super.onDestroy();
    }
}
