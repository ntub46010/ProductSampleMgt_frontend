package com.vincent.psm.product;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.vincent.psm.R;
import com.vincent.psm.data.ImageChild;
import com.vincent.psm.data.Tile;
import com.vincent.psm.network_helper.GetBitmapTask;
import com.vincent.psm.network_helper.MyOkHttp;
import com.vincent.psm.structure.ImageUploadQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.vincent.psm.data.DataHelper.KEY_COLOR;
import static com.vincent.psm.data.DataHelper.KEY_COLORS;
import static com.vincent.psm.data.DataHelper.KEY_EDIT_MODE;
import static com.vincent.psm.data.DataHelper.KEY_ID;
import static com.vincent.psm.data.DataHelper.KEY_LENGTH;
import static com.vincent.psm.data.DataHelper.KEY_MATERIAL;
import static com.vincent.psm.data.DataHelper.KEY_MATERIALS;
import static com.vincent.psm.data.DataHelper.KEY_NAME;
import static com.vincent.psm.data.DataHelper.KEY_ONSALE;
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

public class ProductUpdateActivity extends ProductEditActivity {
    private LinearLayout layEditContent;
    private TextView txtId;
    private RadioButton rdoOnStock, rdoOffStock;

    private GetBitmapTask getBitmap;

    private byte editMode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layout = R.layout.activity_product_update;
        toolbarTitle = "編輯產品";
        super.onCreate(savedInstanceState);
        context = this;

        Bundle bundle = getIntent().getExtras();
        id = bundle.getString(KEY_ID);

        layEditContent = findViewById(R.id.layEditContent);
        RadioGroup rgpEditMode = findViewById(R.id.rgpEditMode);
        rdoOnStock = findViewById(R.id.rdoOnStock);
        rdoOffStock = findViewById(R.id.rdoOffStock);
        txtId = findViewById(R.id.txtId);

        rgpEditMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rdoNormal:
                        layEditContent.setVisibility(View.VISIBLE);
                        editMode = 1;
                        break;
                    case R.id.rdoOnStock:
                        layEditContent.setVisibility(View.VISIBLE);
                        editMode = 2;
                        break;
                    case R.id.rdoOffStock:
                        layEditContent.setVisibility(View.GONE);
                        editMode = 3;
                        break;
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void loadData() {
        layProductPost.setVisibility(View.INVISIBLE);
        prgBar.setVisibility(View.VISIBLE);

        conDownLoad = new MyOkHttp(this, new MyOkHttp.TaskListener() {
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
                                    objProduct.getString(KEY_SAFE_STOCK),
                                    objProduct.getInt(KEY_ONSALE) == 1
                            );
                            photo = objProduct.getString(KEY_PHOTO);

                            getBitmap = new GetBitmapTask(getString(R.string.link_image), new GetBitmapTask.TaskListener() {
                                @Override
                                public void onFinished() {
                                    showData();
                                }
                            });
                            getBitmap.execute(tile);
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

    @Override
    protected void showData() {
        super.setSpecification();

        //編輯模式
        if (tile.getOnSale())
            rdoOnStock.setEnabled(false);
        else
            rdoOffStock.setEnabled(false);

        for (int i = 0; i < adpMaterial.getCount(); i++) {
            if (adpMaterial.getItem(i).equals(tile.getMaterial())) {
                spnMaterial.setSelection(i);
                break;
            }
        }

        for (int i = 0; i < adpColor.getCount(); i++) {
            if (adpColor.getItem(i).equals(tile.getColor())) {
                spnColor.setSelection(i);
                break;
            }
        }

        //產品資料
        imgProduct.setImageBitmap(tile.getImg());
        txtId.setText(tile.getId());
        edtName.setText(tile.getName());
        edtLength.setText(tile.getLength());
        edtWidth.setText(tile.getWidth());
        edtThick.setText(tile.getThick());
        edtPrice.setText(tile.getPrice());
        edtPs.setText(tile.getPs());
        edtStock.setText(tile.getStock());
        edtSafeStock.setText(tile.getSafeStock());

        layProductPost.setVisibility(View.VISIBLE);
        prgBar.setVisibility(View.GONE);
        btnSubmit.setVisibility(View.VISIBLE);
        isShown = true;
    }

    @Override
    protected void postProduct() {
        if (!isInfoValid())
            return;

        prepareDialog();

        if (isPhotoChanged) {
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
        }else {
            uploadProduct();
        }
    }

    @Override
    protected void uploadProduct() {
        conUpload = new MyOkHttp(this, new MyOkHttp.TaskListener() {
            @Override
            public void onFinished(String result) {
                dlgUpload.dismiss();
                try {
                    JSONObject resObj = new JSONObject(result);

                    if (resObj.getBoolean(KEY_STATUS)) {
                        if(resObj.getBoolean(KEY_SUCCESS)) {
                            if (editMode == 1 || editMode == 2) {
                                Intent it = new Intent(context, ProductDetailActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString(KEY_ID, tile.getId());
                                bundle.putString(KEY_NAME, tile.getName());
                                it.putExtras(bundle);
                                startActivity(it);
                                Toast.makeText(context, "編輯成功", Toast.LENGTH_SHORT).show();
                            }else if (editMode == 3) {
                                Toast.makeText(context, "下架成功", Toast.LENGTH_SHORT).show();
                            }
                            finish();
                        }else {
                            Toast.makeText(context, "編輯失敗", Toast.LENGTH_SHORT).show();
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
            setUploadReqObj();
            reqObj.put(KEY_EDIT_MODE, editMode);
            reqObj.put(KEY_ID, tile.getId());
            conUpload.execute(getString(R.string.link_edit_product), reqObj.toString());
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        if (getBitmap != null)
            getBitmap.cancel(true);

        super.onDestroy();
    }
}
