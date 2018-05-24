package com.vincent.psm.order;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vincent.psm.R;
import com.vincent.psm.data.Contact;
import com.vincent.psm.data.Customer;
import com.vincent.psm.network_helper.MyOkHttp;

import java.util.ArrayList;

public abstract class OrderEditActivity extends AppCompatActivity {
    protected Context context;
    protected int layout;
    protected String toolbarTitle;

    protected LinearLayout layEditContent;
    protected AutoCompleteTextView aetCustomerName;
    protected EditText edtCustomerPhone, edtCustomerAddress, edtNewContact, edtContactPhone, edtProductTotal, edtDeliverFee, edtPreDeliverDate, edtDeliverPlace, edtPs, edtSales;

    protected ProgressBar prgBar;
    protected ImageView btnSubmit;

    protected MyOkHttp conDownload;

    protected ArrayList<String> customerNames, contactPersons;
    protected ArrayList<Customer> customers;
    protected ArrayList<Contact> contacts;

    protected boolean isShown = false;


    protected abstract void loadData();
    protected abstract void showData();
    protected abstract void loadContactData(int id);
    protected abstract void showContactData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout);

        layEditContent = findViewById(R.id.layEditContent);
        aetCustomerName = findViewById(R.id.aetCustomerName);
        edtCustomerPhone = findViewById(R.id.edtCustomerPhone);
        edtCustomerAddress = findViewById(R.id.edtCustomerAddress);
        edtNewContact = findViewById(R.id.edtContactName);
        edtContactPhone = findViewById(R.id.edtContactPhone);
        edtProductTotal = findViewById(R.id.edtProductTotal);
        edtDeliverFee = findViewById(R.id.edtDeliverFee);
        edtPreDeliverDate = findViewById(R.id.edtPreDeliverDate);
        edtDeliverPlace = findViewById(R.id.edtDeliverPlace);
        edtPs = findViewById(R.id.edtPs);
        edtSales = findViewById(R.id.edtSales);
        prgBar = findViewById(R.id.prgBar);
        btnSubmit = findViewById(R.id.btnSubmit);

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
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isShown)
            loadData();
    }

    @Override
    public void onDestroy() {
        if (conDownload != null)
            conDownload.cancel();


        super.onDestroy();
    }
}
