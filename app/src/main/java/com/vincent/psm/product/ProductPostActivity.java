package com.vincent.psm.product;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.vincent.psm.R;
import com.vincent.psm.data.ImageChild;
import com.vincent.psm.network_helper.MyOkHttp;
import com.vincent.psm.structure.ImageUploadQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.vincent.psm.data.DataHelper.KEY_COLOR;
import static com.vincent.psm.data.DataHelper.KEY_COLORS;
import static com.vincent.psm.data.DataHelper.KEY_ID;
import static com.vincent.psm.data.DataHelper.KEY_MATERIAL;
import static com.vincent.psm.data.DataHelper.KEY_MATERIALS;
import static com.vincent.psm.data.DataHelper.KEY_NAME;
import static com.vincent.psm.data.DataHelper.KEY_PRODUCT_INFO;
import static com.vincent.psm.data.DataHelper.KEY_STATUS;
import static com.vincent.psm.data.DataHelper.KEY_SUCCESS;

public class ProductPostActivity extends ProductEditActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layout = R.layout.activity_product_post;
        toolbarTitle = "新增產品";
        activity = this;
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void loadData() {
        layEditContent.setVisibility(View.INVISIBLE);
        prgBar.setVisibility(View.VISIBLE);

        conn = new MyOkHttp(activity, new MyOkHttp.TaskListener() {
            @Override
            public void onFinished(JSONObject resObj) throws JSONException {
                if (resObj.length() == 0) {
                    Toast.makeText(activity, "沒有網路連線", Toast.LENGTH_SHORT).show();
                    prgBar.setVisibility(View.GONE);
                    return;
                }
                if (resObj.getBoolean(KEY_STATUS)) {
                    if (resObj.getBoolean(KEY_SUCCESS)) {
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
                        Toast.makeText(activity, "沒有任何材質與顏色", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(activity, "伺服器發生例外", Toast.LENGTH_SHORT).show();
                }
            }
        });
        conn.execute(getString(R.string.link_show_specification));
    }

    @Override
    protected void showData() {
        super.setSpecification();

        layEditContent.setVisibility(View.VISIBLE);
        prgBar.setVisibility(View.GONE);
        btnSubmit.setVisibility(View.VISIBLE);
        isShown = true;
    }

    @Override
    protected void postProduct() {
        if (!isInfoValid())
            return;

        prepareDialog();

        String[] fileNames = new String[1];
        fileNames[0] = "";
        queue = new ImageUploadQueue(getResources(), activity, getString(R.string.link_upload_image));
        queue.enqueueFromRear(new ImageChild(provider.getImage(), true));
        queue.startUpload(fileNames, null, null, new ImageUploadQueue.TaskListener() {
            @Override
            public void onFinished(String[] fileNames) {
                tile.setImgURL(fileNames[0]);
                uploadProduct();
            }
        });
    }

    @Override
    protected void uploadProduct() {
        conn = new MyOkHttp(activity, new MyOkHttp.TaskListener() {
            @Override
            public void onFinished(JSONObject resObj) throws JSONException {
                dlgUpload.dismiss();

                if (resObj.getBoolean(KEY_STATUS)) {
                    if(resObj.getBoolean(KEY_SUCCESS)) {
                        JSONObject obj = resObj.getJSONObject(KEY_PRODUCT_INFO);
                        Intent it = new Intent(activity, ProductDetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString(KEY_ID, obj.getString(KEY_ID));
                        bundle.putString(KEY_NAME, obj.getString(KEY_NAME));
                        it.putExtras(bundle);
                        startActivity(it);
                        finish();
                    }else {
                        Toast.makeText(activity, "刊登失敗", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(activity, "伺服器發生例外", Toast.LENGTH_SHORT).show();
                }
            }
        });
        try {
            setUploadReqObj();
            conn.execute(getString(R.string.link_post_product), reqObj.toString());
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
