package com.vincent.psm;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.vincent.psm.data.DataHelper;
import com.vincent.psm.network_helper.MyOkHttp;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class CartHomeActivity extends AppCompatActivity {
    private Context context;

    private FrameLayout layCart;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView lstCart;
    private FloatingActionButton fabCreate;
    private ProgressBar prgBar;

    private MyOkHttp conn;

    private boolean isShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_home);
        context = this;

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

        layCart = findViewById(R.id.layCart);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        lstCart = findViewById(R.id.lstCart);
        fabCreate = findViewById(R.id.fabCreate);

        prgBar = findViewById(R.id.prgBar);

        fabCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCart();
            }
        });
        fabCreate.setVisibility(View.GONE);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isShown)
            loadData();
    }

    private void loadData() {
        isShown = false;
        layCart.setVisibility(View.GONE);
        prgBar.setVisibility(View.VISIBLE);

        showData();
    }

    private void showData() {
        swipeRefreshLayout.setEnabled(true);
        swipeRefreshLayout.setRefreshing(false);

        fabCreate.setVisibility(View.VISIBLE);
        layCart.setVisibility(View.VISIBLE);
        prgBar.setVisibility(View.GONE);
        isShown = true;
    }

    private void createCart() {
        final EditText edtCartName = new EditText(context);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("新增購物車");
        alert.setMessage("請輸入購物車名稱");
        alert.setView(edtCartName);
        alert.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, edtCartName.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        alert.setNegativeButton("取消", null);
        alert.show();
    }

    @Override
    public void onDestroy() {
        if (conn != null)
            conn.cancel();
        super.onDestroy();
    }
}
