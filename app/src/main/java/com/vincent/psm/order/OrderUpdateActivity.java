package com.vincent.psm.order;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.vincent.psm.R;
import com.vincent.psm.broadcast_helper.manager.RequestManager;
import com.vincent.psm.data.Customer;
import com.vincent.psm.data.Order;
import com.vincent.psm.data.Condition;
import com.vincent.psm.network_helper.MyOkHttp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.vincent.psm.data.DataHelper.KEY_ACT_DELIVER_DATE;
import static com.vincent.psm.data.DataHelper.KEY_CONDITION;
import static com.vincent.psm.data.DataHelper.KEY_CONDITIONS;
import static com.vincent.psm.data.DataHelper.KEY_CONTACT_PERSON;
import static com.vincent.psm.data.DataHelper.KEY_CONTACT_PHONE;
import static com.vincent.psm.data.DataHelper.KEY_CUSTOMERS;
import static com.vincent.psm.data.DataHelper.KEY_CUSTOMER_ADDRESS;
import static com.vincent.psm.data.DataHelper.KEY_CUSTOMER_NAME;
import static com.vincent.psm.data.DataHelper.KEY_CUSTOMER_PHONE;
import static com.vincent.psm.data.DataHelper.KEY_DELIVER_FEE;
import static com.vincent.psm.data.DataHelper.KEY_DELIVER_PLACE;
import static com.vincent.psm.data.DataHelper.KEY_ID;
import static com.vincent.psm.data.DataHelper.KEY_ORDER_ID;
import static com.vincent.psm.data.DataHelper.KEY_ORDER_INFO;
import static com.vincent.psm.data.DataHelper.KEY_PRE_DELIVER_DATE;
import static com.vincent.psm.data.DataHelper.KEY_PRODUCT_TOTAL;
import static com.vincent.psm.data.DataHelper.KEY_PS;
import static com.vincent.psm.data.DataHelper.KEY_SALES;
import static com.vincent.psm.data.DataHelper.KEY_SALES_ID;
import static com.vincent.psm.data.DataHelper.KEY_SALES_NAME;
import static com.vincent.psm.data.DataHelper.KEY_STATUS;
import static com.vincent.psm.data.DataHelper.KEY_SUCCESS;

public class OrderUpdateActivity extends OrderEditActivity {
    private TextView txtId;
    private Spinner spnOrderCondition;
    private LinearLayout layActDeliverDate;
    private EditText edtActDeliverDate;

    private String orderId, salesId;
    private int conditionId, newConditionId;

    private ArrayList<Condition> conditions;
    private ArrayList<String> conditionNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layout = R.layout.activity_order_update;
        toolbarTitle = "編輯訂單";
        super.onCreate(savedInstanceState);

        context = this;
        Bundle bundle = getIntent().getExtras();
        orderId = bundle.getString(KEY_ORDER_ID);

        txtId = findViewById(R.id.txtId);
        spnOrderCondition = findViewById(R.id.spnOrderCondition);
        layActDeliverDate = findViewById(R.id.layActDeliverDate);
        edtActDeliverDate = findViewById(R.id.edtActDeliverDate);

        spnOrderCondition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    newConditionId = conditions.get(position).getId();
                    if (conditions.get(position).getCondition().equals("已完成"))
                        layActDeliverDate.setVisibility(View.VISIBLE);
                    else
                        layActDeliverDate.setVisibility(View.GONE);
                }catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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

        conn = new MyOkHttp(OrderUpdateActivity.this, new MyOkHttp.TaskListener() {
            @Override
            public void onFinished(JSONObject resObj) throws JSONException {
                if (resObj.length() == 0) {
                    Toast.makeText(context, "沒有網路連線", Toast.LENGTH_SHORT).show();
                    prgBar.setVisibility(View.GONE);
                    return;
                }
                if (resObj.getBoolean(KEY_STATUS)) {
                    //訂單摘要
                    if (resObj.has(KEY_ORDER_INFO)) {
                        JSONObject objOrderInfo = resObj.getJSONObject(KEY_ORDER_INFO);
                        order = new Order(
                                objOrderInfo.getString(KEY_ORDER_ID),
                                objOrderInfo.getString(KEY_CUSTOMER_NAME),
                                objOrderInfo.getString(KEY_CUSTOMER_PHONE),
                                objOrderInfo.getString(KEY_CONTACT_PERSON),
                                objOrderInfo.getString(KEY_CONTACT_PHONE),
                                objOrderInfo.getInt(KEY_PRODUCT_TOTAL),
                                objOrderInfo.getInt(KEY_DELIVER_FEE),
                                objOrderInfo.getString(KEY_CONDITION),
                                objOrderInfo.getString(KEY_PRE_DELIVER_DATE),
                                objOrderInfo.getString(KEY_ACT_DELIVER_DATE),
                                objOrderInfo.getString(KEY_DELIVER_PLACE),
                                objOrderInfo.getString(KEY_PS),
                                objOrderInfo.getString(KEY_SALES_NAME)
                        );
                        order.setCustomerAddress(objOrderInfo.getString(KEY_CUSTOMER_ADDRESS));
                        salesId = objOrderInfo.getString(KEY_SALES_ID);

                    }else {
                        Toast.makeText(context, "訂單不存在", Toast.LENGTH_SHORT).show();
                        prgBar.setVisibility(View.GONE);
                        return;
                    }

                    if (resObj.getBoolean(KEY_SUCCESS)) {
                        //客戶清單
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

                        //訂單狀態
                        JSONArray aryConditions = resObj.getJSONArray(KEY_CONDITIONS);
                        conditions = new ArrayList<>();
                        conditions.add(new Condition(
                                0,
                                "請選擇"
                        ));
                        conditionNames = new ArrayList<>();
                        conditionNames.add("請選擇");
                        for (int i = 0; i < aryConditions.length(); i++) {
                            JSONObject obj = aryConditions.getJSONObject(i);
                            conditions.add(new Condition(
                                    obj.getInt(KEY_ID),
                                    obj.getString(KEY_CONDITION)
                            ));
                            conditionNames.add(obj.getString(KEY_CONDITION));
                        }

                        showData();
                    }else {
                        Toast.makeText(context, "沒有任何客戶或訂單狀態", Toast.LENGTH_SHORT).show();
                        showData();
                    }
                }else {
                    Toast.makeText(context, "伺服器發生例外", Toast.LENGTH_SHORT).show();
                }
            }
        });
        try {
            JSONObject reqObj = new JSONObject();
            reqObj.put(KEY_ORDER_ID, orderId);
            conn.execute(getString(R.string.link_show_editing_order), reqObj.toString());
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void showData() {
        txtId.setText(order.getId());

        //訂單狀態
        spnOrderCondition.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, conditionNames));
        for (int i = 0; i < conditionNames.size(); i++) {
            if (conditionNames.get(i).equals(order.getCondition())) {
                conditionId = i;
                spnOrderCondition.setSelection(i);
                break;
            }
        }

        //訂單摘要
        aetCustomerName.setText(order.getCustomerName());
        edtCustomerPhone.setText(order.getCustomerPhone());
        edtCustomerAddress.setText(order.getCustomerAddress());
        edtProductTotal.setText(String.valueOf(order.getTotal()));
        edtDeliverFee.setText(String.valueOf(order.getDeliverFee()));
        edtPreDeliverDate.setText(order.getPredictDeliverDate());
        edtActDeliverDate.setText(order.getActualDeliverDate());
        edtDeliverPlace.setText(order.getDeliverPlace());
        edtPs.setText(order.getPs());
        edtSales.setText(order.getSalesName());

        //聯絡人(還不能自動指向Spinner)
        edtContactPerson.setText(order.getContactPerson());
        edtContactPhone.setText(order.getContactPhone());
        rdoSelectContact.setChecked(false);
        rdoNewContact.setChecked(true);

        //客戶清單
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
                loadContactData(OrderUpdateActivity.this, customers.get(index).getId());
            }
        });

        layEditContent.setVisibility(View.VISIBLE);
        btnSubmit.setVisibility(View.VISIBLE);
        prgBar.setVisibility(View.GONE);
        isShown = true;
    }

    @Override
    protected void uploadOrder() {
        if (layActDeliverDate.getVisibility() == View.GONE)
            actualDeliverDate = null;
        else
            actualDeliverDate = edtActDeliverDate.getText().toString();

        if (!isInfoValid())
            return;

        if (newConditionId == 0) {
            Toast.makeText(context, "未選擇訂單狀態", Toast.LENGTH_SHORT).show();
            return;
        }

        conn = new MyOkHttp(OrderUpdateActivity.this, new MyOkHttp.TaskListener() {
            @Override
            public void onFinished(JSONObject resObj) throws JSONException {
                if (resObj.length() == 0) {
                    Toast.makeText(context, "沒有網路連線", Toast.LENGTH_SHORT).show();
                    prgBar.setVisibility(View.GONE);
                    return;
                }
                if (resObj.getBoolean(KEY_STATUS)) {
                    if (resObj.getBoolean(KEY_SUCCESS)) {
                        Toast.makeText(context, "編輯成功", Toast.LENGTH_SHORT).show();

                        //發送新訂單狀態給負責業務
                        if (newConditionId != conditionId) {
                            RequestManager.getInstance(OrderUpdateActivity.this).prepareNotification(
                                    salesId,
                                    "訂單狀態更新",
                                    getString(R.string.txt_order_condition_changed, order.getCustomerName(), conditionNames.get(newConditionId)),
                                    null
                            );
                        }

                        finish();
                    }else {
                        Toast.makeText(context, "編輯失敗", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, "伺服器發生例外", Toast.LENGTH_SHORT).show();
                }
            }
        });
        try {
            setUploadReqObj();
            reqObj.put(KEY_ORDER_ID, orderId);
            reqObj.put(KEY_SALES, salesId);
            reqObj.put(KEY_CONDITION, newConditionId);
            reqObj.put(KEY_ACT_DELIVER_DATE, order.getActualDeliverDate());
            conn.execute(getString(R.string.link_edit_order), reqObj.toString());
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
