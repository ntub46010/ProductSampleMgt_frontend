package com.vincent.psm.product;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.vincent.psm.R;
import com.vincent.psm.data.AlbumImageProvider;
import com.vincent.psm.data.Tile;
import com.vincent.psm.network_helper.GetBitmapTask;
import com.vincent.psm.network_helper.MyOkHttp;
import com.vincent.psm.structure.ImageUploadQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.vincent.psm.data.AlbumImageProvider.REQUEST_ALBUM;
import static com.vincent.psm.data.AlbumImageProvider.REQUEST_CROP;
import static com.vincent.psm.data.DataHelper.KEY_COLOR;
import static com.vincent.psm.data.DataHelper.KEY_COLORS;
import static com.vincent.psm.data.DataHelper.KEY_ID;
import static com.vincent.psm.data.DataHelper.KEY_LENGTH;
import static com.vincent.psm.data.DataHelper.KEY_MATERIAL;
import static com.vincent.psm.data.DataHelper.KEY_MATERIALS;
import static com.vincent.psm.data.DataHelper.KEY_NAME;
import static com.vincent.psm.data.DataHelper.KEY_PHOTO;
import static com.vincent.psm.data.DataHelper.KEY_PRICE;
import static com.vincent.psm.data.DataHelper.KEY_PRODUCT_INFO;
import static com.vincent.psm.data.DataHelper.KEY_PS;
import static com.vincent.psm.data.DataHelper.KEY_SAFE_STOCK;
import static com.vincent.psm.data.DataHelper.KEY_STATUS;
import static com.vincent.psm.data.DataHelper.KEY_SUCCESS;
import static com.vincent.psm.data.DataHelper.KEY_Stock;
import static com.vincent.psm.data.DataHelper.KEY_THICK;
import static com.vincent.psm.data.DataHelper.KEY_WIDTH;

public class ProductEditActivity extends AppCompatActivity {
    private Context context;

    private FrameLayout layProductPost;
    private ImageView imgProduct;
    private TextView txtId;
    private RadioButton rdoSelectMaterial, rdoNewMaterial, rdoSelectColor, rdoNewColor;
    private Spinner spnMaterial, spnColor;
    private EditText edtName, edtMaterial, edtColor, edtLength, edtWidth, edtThick, edtPrice, edtPs, edtStock, edtSafeStock;
    private ProgressBar prgBar;

    private AlbumImageProvider provider;
    private MyOkHttp conDownLoad, conUpload;
    private GetBitmapTask getBitmap;
    private ArrayList<String> materials, colors;
    private ImageUploadQueue queue;
    private Dialog dlgUpload;

    private Tile tile;
    private String material, color, id;

    private boolean isShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_edit);
        context = this;

        Bundle bundle = getIntent().getExtras();
        id = bundle.getString(KEY_ID);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView txtBarTitle = toolbar.findViewById(R.id.txtToolbarTitle);
        txtBarTitle.setText("編輯產品");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        layProductPost = findViewById(R.id.layProductPost);
        imgProduct = findViewById(R.id.imgProduct);
        txtId = findViewById(R.id.txtId);
        rdoSelectMaterial = findViewById(R.id.rdoSelectMaterial);
        rdoNewMaterial = findViewById(R.id.rdoNewMaterial);
        rdoSelectColor = findViewById(R.id.rdoSelectColor);
        rdoNewColor = findViewById(R.id.rdoNewColor);
        spnMaterial = findViewById(R.id.spnMaterial);
        spnColor = findViewById(R.id.spnColor);
        edtName = findViewById(R.id.edtName);
        edtLength = findViewById(R.id.edtLength);
        edtWidth = findViewById(R.id.edtWidth);
        edtThick = findViewById(R.id.edtThick);
        edtPrice = findViewById(R.id.edtPrice);
        edtPs = findViewById(R.id.edtPs);
        edtStock = findViewById(R.id.edtStock);
        edtSafeStock = findViewById(R.id.edtSafeStock);
        edtMaterial = findViewById(R.id.edtMaterial);
        edtColor = findViewById(R.id.edtColor);
        prgBar = findViewById(R.id.prgBar);
        ImageView btnSubmit = findViewById(R.id.btnSubmit);

        imgProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                provider = new AlbumImageProvider(ProductEditActivity.this, 3, 4, 600, 800, new AlbumImageProvider.TaskListener() {
                    @Override
                    public void onFinished(Bitmap bitmap) {
                        imgProduct.setImageBitmap(bitmap);
                    }
                });
                provider.select();
            }
        });

        spnMaterial.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                material = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                color = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //postProduct();
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

        conDownLoad = new MyOkHttp(ProductEditActivity.this, new MyOkHttp.TaskListener() {
            @Override
            public void onFinished(String result) {
                try {
                    JSONObject resObj = new JSONObject(result);
                    if (resObj.getBoolean(KEY_STATUS)) {
                        if(resObj.getBoolean(KEY_SUCCESS)) {
                            //載入規格清單
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

                            //載入產品詳情
                            JSONObject objProduct = resObj.getJSONObject(KEY_PRODUCT_INFO);
                            tile = new Tile(
                                    objProduct.getString(KEY_ID),
                                    objProduct.getString(KEY_PHOTO),
                                    objProduct.getString(KEY_NAME),
                                    objProduct.getString(KEY_MATERIAL),
                                    objProduct.getString(KEY_COLOR),
                                    objProduct.getString(KEY_LENGTH),
                                    objProduct.getString(KEY_WIDTH),
                                    objProduct.getString(KEY_THICK),
                                    objProduct.getString(KEY_PRICE),
                                    objProduct.getString(KEY_PS),
                                    objProduct.getString(KEY_Stock),
                                    objProduct.getString(KEY_SAFE_STOCK)
                            );

                            getBitmap = new GetBitmapTask(getString(R.string.link_image), new GetBitmapTask.TaskListener() {
                                @Override
                                public void onFinished() {
                                    showData();
                                }
                            });
                            getBitmap.execute(tile);

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
        try {
            JSONObject reqObj = new JSONObject();
            reqObj.put(KEY_ID, id);
            conDownLoad.execute(getString(R.string.link_show_editing_product), reqObj.toString());
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showData() {
        //材質、顏色清單
        spnMaterial.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, materials));
        spnColor.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, colors));

        //產品資料
        imgProduct.setImageBitmap(tile.getImg());
        edtName.setText(tile.getName());
        edtPrice.setText(tile.getPrice());
        txtId.setText(tile.getId());
        edtMaterial.setText(tile.getMaterial());
        edtColor.setText(tile.getColor());
        edtLength.setText(tile.getLength());
        edtWidth.setText(tile.getWidth());
        edtThick.setText(tile.getThick());
        edtPs.setText(tile.getPs());
        edtStock.setText(tile.getStock());
        edtSafeStock.setText(tile.getSafeStock());

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
    public void onDestroy() {
        if (conDownLoad != null)
            conDownLoad.cancel();
        if (conUpload != null)
            conUpload.cancel();
        if (getBitmap != null)
            getBitmap.cancel(true);
        if (queue != null)
            queue.destroy();

        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ALBUM || requestCode == REQUEST_CROP)
            provider.onActivityResult(requestCode, resultCode, data);
        else
            super.onActivityResult(requestCode, resultCode, data);
    }
}
