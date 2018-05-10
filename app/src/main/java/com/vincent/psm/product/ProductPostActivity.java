package com.vincent.psm.product;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.vincent.psm.R;
import com.vincent.psm.data.AlbumImageProvider;
import com.vincent.psm.network_helper.MyOkHttp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.vincent.psm.data.AlbumImageProvider.REQUEST_ALBUM;
import static com.vincent.psm.data.AlbumImageProvider.REQUEST_CROP;
import static com.vincent.psm.data.DataHelper.KEY_COLOR;
import static com.vincent.psm.data.DataHelper.KEY_COLORS;
import static com.vincent.psm.data.DataHelper.KEY_MATERIAL;
import static com.vincent.psm.data.DataHelper.KEY_MATERIALS;
import static com.vincent.psm.data.DataHelper.KEY_STATUS;
import static com.vincent.psm.data.DataHelper.KEY_SUCCESS;

public class ProductPostActivity extends AppCompatActivity {
    private Context context;

    private FrameLayout layProductPost;
    private ImageView imgProduct;
    private RadioButton rdoSelectMaterial, rdoNewMaterial, rdoSelectColor, rdoNewColor;
    private Spinner spnMaterial, spnColor;
    private EditText edtMaterial, edtColor;
    private ProgressBar prgBar;

    private AlbumImageProvider provider;
    private MyOkHttp conn;
    private ArrayList<String> materials, colors;

    private boolean isShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_post);
        context = this;

        layProductPost = findViewById(R.id.layProductPost);
        imgProduct = findViewById(R.id.imgProduct);
        rdoSelectMaterial = findViewById(R.id.rdoSelectMaterial);
        rdoNewMaterial = findViewById(R.id.rdoNewMaterial);
        rdoSelectColor = findViewById(R.id.rdoSelectColor);
        rdoNewColor = findViewById(R.id.rdoNewColor);
        spnMaterial = findViewById(R.id.spnMaterial);
        spnColor = findViewById(R.id.spnColor);
        edtMaterial = findViewById(R.id.edtMaterial);
        edtColor = findViewById(R.id.edtColor);
        prgBar = findViewById(R.id.prgBar);

        imgProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                provider = new AlbumImageProvider(ProductPostActivity.this, 3, 4, 600, 800, new AlbumImageProvider.TaskListener() {
                    @Override
                    public void onFinished(Bitmap bitmap) {
                        imgProduct.setImageBitmap(bitmap);
                    }
                });
                provider.select();
            }
        });

        edtMaterial.setEnabled(false);
        edtColor.setEnabled(false);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isShown)
            loadData();
    }

    private void loadData() {
        layProductPost.setVisibility(View.INVISIBLE);
        prgBar.setVisibility(View.VISIBLE);

        conn = new MyOkHttp(ProductPostActivity.this, new MyOkHttp.TaskListener() {
            @Override
            public void onFinished(String result) {
                try {
                    JSONObject resObj = new JSONObject(result);
                    if (resObj.getBoolean(KEY_STATUS)) {
                        if(resObj.getBoolean(KEY_SUCCESS)) {
                            JSONArray aryMaterial = resObj.getJSONArray(KEY_MATERIALS);
                            JSONArray aryColor = resObj.getJSONArray(KEY_COLORS);
                            materials = new ArrayList<>();
                            colors = new ArrayList<>();
                            materials.add("請選擇");
                            colors.add("請選擇");

                            for (int i=0; i<aryMaterial.length(); i++)
                                materials.add(aryMaterial.getJSONObject(i).getString(KEY_MATERIAL));
                            for (int i=0; i<aryColor.length(); i++)
                                colors.add(aryColor.getJSONObject(i).getString(KEY_COLOR));

                            showData();
                        }else {
                            Toast.makeText(context, "沒有任何材質與顏色", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(context, "伺服器發生例外", Toast.LENGTH_SHORT).show();
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        conn.execute(getString(R.string.link_show_specification));
    }

    private void showData() {
        ArrayAdapter<String> adpMaterial = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, materials);
        ArrayAdapter<String> adpColor = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, colors);

        spnMaterial.setAdapter(adpMaterial);
        spnColor.setAdapter(adpColor);

        layProductPost.setVisibility(View.VISIBLE);
        prgBar.setVisibility(View.GONE);
        isShown = true;
    }

    public void onRadioSelect(View v) {
        switch (v.getId()) {
            case R.id.rdoSelectMaterial:
                rdoNewMaterial.setChecked(false);
                spnMaterial.setEnabled(true);
                edtMaterial.setEnabled(false);
                edtMaterial.setText(null);
                break;

            case R.id.rdoNewMaterial:
                rdoSelectMaterial.setChecked(false);
                spnMaterial.setEnabled(false);
                edtMaterial.setEnabled(true);
                spnMaterial.setSelection(0);
                break;

            case R.id.rdoSelectColor:
                rdoNewColor.setChecked(false);
                spnColor.setEnabled(true);
                edtColor.setEnabled(false);
                edtColor.setText(null);
                break;

            case R.id.rdoNewColor:
                rdoSelectColor.setChecked(false);
                spnColor.setEnabled(false);
                edtColor.setEnabled(true);
                spnColor.setSelection(0);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ALBUM || requestCode == REQUEST_CROP)
            provider.onActivityResult(requestCode, resultCode, data);
        else
            super.onActivityResult(requestCode, resultCode, data);
    }
}
