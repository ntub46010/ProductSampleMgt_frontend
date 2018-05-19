package com.vincent.psm;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.vincent.psm.adapter.MainFunctionAdapter;
import com.vincent.psm.cart.CartHomeActivity;
import com.vincent.psm.product.ProductHomeActivity;
import com.vincent.psm.product.ProductMgtActivity;

import static com.vincent.psm.data.DataHelper.KEY_IDENTITY;
import static com.vincent.psm.data.DataHelper.KEY_NAME;

public class MainActivity extends AppCompatActivity {
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        TextView txtGreeting = findViewById(R.id.txtGreeting);
        GridView grdFunction = findViewById(R.id.grdFunction);

        try {
            Bundle bundle = getIntent().getExtras();
            txtGreeting.setText(getString(R.string.txt_greeting, bundle.getString(KEY_NAME), bundle.getString(KEY_IDENTITY)));
        }catch (Exception e) {
            e.printStackTrace();
        }

        grdFunction.setAdapter(new MainFunctionAdapter(context));
        grdFunction.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                executeFunction(position);
            }
        });
    }

    private void executeFunction(int position) {
        switch (position) {
            case 0:
                context.startActivity(new Intent(context, ProductHomeActivity.class));
                break;
            case 1:
                context.startActivity(new Intent(context, ProductMgtActivity.class));
                break;
            case 2:
                context.startActivity(new Intent(context, CartHomeActivity.class));
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                AlertDialog.Builder msgbox = new AlertDialog.Builder(context);
                msgbox.setTitle(getString(R.string.app_name))
                        .setMessage("確定要登出嗎")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                writeAutoLoginRecord();
                                finish();
                            }
                        })
                        .setNegativeButton("否", null)
                        .show();
                break;
            case 6:
                context.startActivity(new Intent(context, BroadcastActivity.class));
                break;
            case 7:
                context.startActivity(new Intent(context, ImageUploadActivity.class));
                break;
        }
    }

    private void writeAutoLoginRecord() {
        getSharedPreferences(getString(R.string.sp_filename), MODE_PRIVATE).edit()
                .putBoolean(getString(R.string.sp_auto_login), false)
                .remove(getString(R.string.sp_login_user))
                .remove(getString(R.string.sp_login_password))
                .apply();
    }
}
