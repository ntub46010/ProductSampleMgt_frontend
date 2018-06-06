package com.vincent.psm.product;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.vincent.psm.R;
import com.vincent.psm.data.AlbumImageProvider;
import com.vincent.psm.data.Tile;
import com.vincent.psm.data.Verifier;
import com.vincent.psm.network_helper.MyOkHttp;
import com.vincent.psm.structure.ImageUploadQueue;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.vincent.psm.data.AlbumImageProvider.REQUEST_ALBUM;
import static com.vincent.psm.data.AlbumImageProvider.REQUEST_CROP;
import static com.vincent.psm.data.DataHelper.KEY_COLOR;
import static com.vincent.psm.data.DataHelper.KEY_LENGTH;
import static com.vincent.psm.data.DataHelper.KEY_MATERIAL;
import static com.vincent.psm.data.DataHelper.KEY_NAME;
import static com.vincent.psm.data.DataHelper.KEY_PHOTO;
import static com.vincent.psm.data.DataHelper.KEY_PRICE;
import static com.vincent.psm.data.DataHelper.KEY_PS;
import static com.vincent.psm.data.DataHelper.KEY_SAFE_STOCK;
import static com.vincent.psm.data.DataHelper.KEY_STOCK;
import static com.vincent.psm.data.DataHelper.KEY_THICK;
import static com.vincent.psm.data.DataHelper.KEY_WIDTH;

public abstract class ProductEditActivity extends AppCompatActivity {
    protected Activity activity;
    protected int layout;
    protected String toolbarTitle, dialogTitle;

    protected LinearLayout layProductPost, layEditContent;
    protected ImageView imgProduct;
    protected RadioButton rdoSelectMaterial, rdoNewMaterial, rdoSelectColor, rdoNewColor;
    protected Spinner spnMaterial, spnColor;
    protected EditText edtName, edtMaterial, edtColor, edtLength, edtWidth, edtThick, edtPrice, edtPs, edtStock, edtSafeStock;
    protected ProgressBar prgBar;
    protected ImageView btnSubmit;

    protected ArrayAdapter<String> adpMaterial, adpColor;

    protected AlbumImageProvider provider;
    protected MyOkHttp conn;
    protected ArrayList<String> materials, colors;
    protected ImageUploadQueue queue;
    protected Dialog dlgUpload;
    private AlertDialog msgbox;
    protected JSONObject reqObj = new JSONObject();

    protected Tile tile;
    protected String material, color, id, photo;

    protected boolean isShown = false, isPhotoChanged = false;

    protected abstract void loadData();
    protected abstract void showData();
    protected abstract void postProduct();
    protected abstract void uploadProduct();

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

        layProductPost = findViewById(R.id.layProductPost);
        layEditContent = findViewById(R.id.layEditContent);
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
                provider = new AlbumImageProvider(ProductEditActivity.this, 3, 4, 600, 800, new AlbumImageProvider.TaskListener() {
                    @Override
                    public void onFinished(Bitmap bitmap) {
                        imgProduct.setImageBitmap(bitmap);
                        isPhotoChanged = true;
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

    protected void setSpecification() {
        //材質、顏色清單
        adpMaterial = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, materials);
        adpColor = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, colors);
        spnMaterial.setAdapter(adpMaterial);
        spnColor.setAdapter(adpColor);
    }

    protected boolean isInfoValid() {
        Verifier v = new Verifier(activity);
        StringBuffer errMsg = new StringBuffer();

        String material = rdoSelectMaterial.isChecked() ? this.material : edtMaterial.getText().toString();
        String color = rdoSelectColor.isChecked() ? this.color : edtColor.getText().toString();

        tile = null;
        tile = new Tile(
                id,
                photo,
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

        if (provider != null && provider.getImage() == null)
            errMsg.append(getString(R.string.chk_is_empty, "圖片"));

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
            v.getDialog("產品資料錯誤", errMsg.substring(0, errMsg.length() - 1)).show();
            return false;
        }else {
            tile.setPrice(String.valueOf(Integer.parseInt(tile.getPrice()))); //避免有人開頭輸入一堆0
            return true;
        }
    }

    protected void prepareDialog() {
        dlgUpload = new Dialog(activity);
        dlgUpload.setContentView(R.layout.dlg_uploading);
        dlgUpload.setCancelable(false);
        TextView txtUploadHint = dlgUpload.findViewById(R.id.txtHint);
        txtUploadHint.setText("上傳中，長按取消...");

        msgbox = new AlertDialog.Builder(activity)
                .setTitle(dialogTitle)
                .setMessage("確定取消上傳嗎？")
                .setCancelable(true)
                .setNegativeButton("否", null)
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            queue.cancelUpload();
                            Toast.makeText(activity, "上傳已取消", Toast.LENGTH_SHORT).show();
                            dlgUpload.dismiss();
                        }catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .create();

        LinearLayout layUpload = dlgUpload.findViewById(R.id.layUpload);
        layUpload.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                msgbox.show();
                return true;
            }
        });
        dlgUpload.show();
    }

    protected void setUploadReqObj() throws JSONException {
        reqObj.put(KEY_NAME, tile.getName());
        reqObj.put(KEY_MATERIAL, tile.getMaterial());
        reqObj.put(KEY_COLOR, tile.getColor());
        reqObj.put(KEY_LENGTH , tile. getLength());
        reqObj.put(KEY_WIDTH, tile.getWidth());
        reqObj.put(KEY_THICK, tile.getThick());
        reqObj.put(KEY_STOCK, tile.getStock());
        reqObj.put(KEY_SAFE_STOCK, tile.getSafeStock());
        reqObj.put(KEY_PRICE, tile.getPrice());
        reqObj.put(KEY_PHOTO, tile.getPhoto());
        reqObj.put(KEY_PS, tile.getPs());
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
        if (conn != null)
            conn.cancel();
        if (queue != null) {
            queue.destroy();
            queue = null;
        }
        if (dlgUpload != null)
            dlgUpload.dismiss();
        if (msgbox != null)
            msgbox.dismiss();

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
