package com.vincent.psm.product;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.vincent.psm.R;
import com.vincent.psm.data.AlbumImageProvider;
import com.vincent.psm.data.ImageChild;
import com.vincent.psm.data.Tile;
import com.vincent.psm.data.Verifier;
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
import static com.vincent.psm.data.DataHelper.KEY_PS;
import static com.vincent.psm.data.DataHelper.KEY_SAFE_STOCK;
import static com.vincent.psm.data.DataHelper.KEY_STATUS;
import static com.vincent.psm.data.DataHelper.KEY_SUCCESS;
import static com.vincent.psm.data.DataHelper.KEY_Stock;
import static com.vincent.psm.data.DataHelper.KEY_THICK;
import static com.vincent.psm.data.DataHelper.KEY_WIDTH;

public class ProductPostActivity extends AppCompatActivity {
    private Context context;

    private LinearLayout layProductPost;
    private ImageView imgProduct;
    private RadioButton rdoSelectMaterial, rdoNewMaterial, rdoSelectColor, rdoNewColor;
    private Spinner spnMaterial, spnColor;
    private EditText edtName, edtMaterial, edtColor, edtLength, edtWidth, edtThick, edtPrice, edtPs, edtStock, edtSafeStock;
    private ProgressBar prgBar;
    private ImageView btnSubmit;

    private AlbumImageProvider provider;
    private MyOkHttp conDownLoad, conUpload;
    private ArrayList<String> materials, colors;
    private ImageUploadQueue queue;
    private Dialog dlgUpload;

    private Tile tile;
    private String material, color;

    private boolean isShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_post);
        context = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView txtBarTitle = toolbar.findViewById(R.id.txtToolbarTitle);
        txtBarTitle.setText("新增產品");
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
        btnSubmit = findViewById(R.id.btnSubmit);

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
                postProduct();
            }
        });

        edtMaterial.setEnabled(false);
        edtColor.setEnabled(false);
        btnSubmit.setVisibility(View.GONE);
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

        conDownLoad = new MyOkHttp(ProductPostActivity.this, new MyOkHttp.TaskListener() {
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
        conDownLoad.execute(getString(R.string.link_show_specification));
    }

    private void showData() {
        spnMaterial.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, materials));
        spnColor.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, colors));

        layProductPost.setVisibility(View.VISIBLE);
        prgBar.setVisibility(View.GONE);
        btnSubmit.setVisibility(View.VISIBLE);
        isShown = true;
    }

    private void postProduct() {
        if (!isInfoValid())
            return;

        prepareDialog();
        dlgUpload.show();

        String[] fileNames = new String[1];
        fileNames[0] = "";
        queue = new ImageUploadQueue(getResources(), context, getString(R.string.link_upload_image));
        queue.enqueueFromRear(new ImageChild(provider.getImage(), true));
        queue.startUpload(fileNames, null, null, new ImageUploadQueue.TaskListener() {
            @Override
            public void onFinished(String[] fileNames) {
                tile.setImgURL(fileNames[0]);
                uploadProduct();
            }
        });
    }

    private void uploadProduct() {
        conUpload = new MyOkHttp(ProductPostActivity.this, new MyOkHttp.TaskListener() {
            @Override
            public void onFinished(String result) {
                dlgUpload.dismiss();
                try {
                    JSONObject resObj = new JSONObject(result);

                    if (resObj.getBoolean(KEY_STATUS)) {
                        if(resObj.getBoolean(KEY_SUCCESS)) {
                            Intent it = new Intent(context, ProductDetailActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString(KEY_ID, tile.getId());
                            bundle.putString(KEY_NAME, tile.getName());
                            it.putExtras(bundle);
                            startActivity(it);
                            finish();
                        }else {
                            Toast.makeText(context, "刊登失敗", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(context, "伺服器發生例外", Toast.LENGTH_SHORT).show();
                    }
                }catch (JSONException e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        try {
            JSONObject reqObj = new JSONObject();
            reqObj.put(KEY_NAME, tile.getName());
            reqObj.put(KEY_MATERIAL, tile.getMaterial());
            reqObj.put(KEY_COLOR, tile.getColor());
            reqObj.put(KEY_LENGTH , tile. getLength());
            reqObj.put(KEY_WIDTH, tile.getWidth());
            reqObj.put(KEY_THICK, tile.getThick());
            reqObj.put(KEY_Stock, tile.getStock());
            reqObj.put(KEY_SAFE_STOCK, tile.getSafeStock());
            reqObj.put(KEY_PRICE, tile.getPrice());
            reqObj.put(KEY_PHOTO, tile.getPhoto());
            reqObj.put(KEY_PS, tile.getPs());
            conUpload.execute(getString(R.string.link_post_product), reqObj.toString());
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean isInfoValid() {
        Verifier v = new Verifier(context);
        StringBuffer errMsg = new StringBuffer();

        String material = rdoSelectMaterial.isChecked() ? this.material : edtMaterial.getText().toString();
        String color = rdoSelectColor.isChecked() ? this.color : edtColor.getText().toString();

        tile = null;
        tile = new Tile(
                null,
                "",
                edtName.getText().toString(),
                material,
                color,
                edtLength.getText().toString(),
                edtWidth.getText().toString(),
                edtThick.getText().toString(),
                edtPrice.getText().toString(),
                edtPs.getText().toString(),
                edtStock.getText().toString(),
                edtSafeStock.getText().toString(),
                true
        );

        if (provider.getImage() == null)
            errMsg.append(getString(R.string.chkSelect, "圖片"));

        errMsg.append(v.chkTitle(tile.getName()));
        errMsg.append(v.chkMaterial(tile.getMaterial()));
        errMsg.append(v.chkColor(tile.getColor()));
        errMsg.append(v.chkLength(tile.getLength()));
        errMsg.append(v.chkWidth(tile.getWidth()));
        errMsg.append(v.chkThick(tile.getThick()));
        errMsg.append(v.chkPrice(tile.getPrice()));
        errMsg.append(v.chkPs(tile.getPs()));
        errMsg.append(v.chkStock(tile.getStock()));
        errMsg.append(v.chkSafeStock(tile.getSafeStock()));

        if (errMsg.length() != 0) {
            v.getDialog("新增產品", errMsg.substring(0, errMsg.length() - 1)).show();
            return false;
        }else {
            tile.setPrice(String.valueOf(Integer.parseInt(tile.getPrice()))); //避免有人開頭輸入一堆0
            return true;
        }
    }

    private void prepareDialog() {
        dlgUpload = new Dialog(context);
        dlgUpload.setContentView(R.layout.dlg_uploading);
        dlgUpload.setCancelable(false);
        TextView txtUploadHint = dlgUpload.findViewById(R.id.txtHint);
        txtUploadHint.setText("上傳中，長按取消...");

        LinearLayout layUpload = dlgUpload.findViewById(R.id.layUpload);
        layUpload.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder msgbox = new AlertDialog.Builder(context);
                msgbox.setTitle("新增商品")
                        .setMessage("確定取消上傳嗎？")
                        .setNegativeButton("否", null)
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                try {
                                    queue.cancelUpload();
                                    Toast.makeText(context, "上傳已取消", Toast.LENGTH_SHORT).show();
                                    dlgUpload.dismiss();
                                }catch (NullPointerException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).show();
                return true;
            }
        });
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
