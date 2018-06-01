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
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.vincent.psm.adapter.MainFunctionAdapter;
import com.vincent.psm.cart.CartHomeActivity;
import com.vincent.psm.order.OrderHomeActivity;
import com.vincent.psm.product.ProductHomeActivity;
import com.vincent.psm.product.ProductMgtActivity;

import static com.vincent.psm.broadcast_helper.data.FirebaseUser.DATABASE_USERS;
import static com.vincent.psm.data.DataHelper.KEY_IDENTITY;
import static com.vincent.psm.data.DataHelper.KEY_NAME;
import static com.vincent.psm.data.DataHelper.authority;
import static com.vincent.psm.data.DataHelper.currentTokenIndex;
import static com.vincent.psm.data.DataHelper.loginUserId;

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
                startActivity(new Intent(context, ProductHomeActivity.class));
                break;
            case 1:
                startActivity(new Intent(context, ProductMgtActivity.class));
                /*
                if (authority == 3)
                    startActivity(new Intent(context, ProductMgtActivity.class));
                 else
                    Toast.makeText(context, getString(R.string.txt_authority_limit, "產品管理"), Toast.LENGTH_SHORT).show();
                 */
                break;
            case 2:
                startActivity(new Intent(context, CartHomeActivity.class));
                /*
                if (authority == 1)
                    startActivity(new Intent(context, CartHomeActivity.class));
                 else
                    Toast.makeText(context, getString(R.string.txt_authority_limit, "業務"), Toast.LENGTH_SHORT).show();
                */
                break;
            case 3:
                startActivity(new Intent(context, OrderHomeActivity.class));
                /*
                if (authority == 1 || authority == 2)
                    startActivity(new Intent(context, OrderHomeActivity.class));
                 else
                    Toast.makeText(context, getString(R.string.txt_authority_limit, "業務、倉管"), Toast.LENGTH_SHORT).show();
                */
                break;
            case 4:
                startActivity(new Intent(context, NotificationActivity.class));
                break;
            case 5:
                break;
            case 6:
                AlertDialog.Builder msgbox = new AlertDialog.Builder(context);
                msgbox.setTitle(getString(R.string.app_name))
                        .setMessage("確定要登出嗎")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseDatabase.getInstance().getReference()
                                        .child(DATABASE_USERS)
                                        .child(loginUserId)
                                        .child("deviceList")
                                        .child(String.valueOf(currentTokenIndex))
                                        .removeValue(); //刪除原本token

                                writeAutoLoginRecord();
                                finish();
                            }
                        })
                        .setNegativeButton("否", null)
                        .show();
                break;
            case 7:
                startActivity(new Intent(context, BroadcastActivity.class));
                break;
            case 8:
                startActivity(new Intent(context, ImageUploadActivity.class));
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
