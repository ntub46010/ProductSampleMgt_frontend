package com.vincent.psm.product;

import android.app.Activity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.vincent.psm.R;
import com.vincent.psm.adapter.ProductDisplayAdapter;
import com.vincent.psm.data.Tile;
import com.vincent.psm.network_helper.MyOkHttp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.vincent.psm.data.DataHelper.KEY_COLOR;
import static com.vincent.psm.data.DataHelper.KEY_COLORS;
import static com.vincent.psm.data.DataHelper.KEY_ID;
import static com.vincent.psm.data.DataHelper.KEY_KEYWORD;
import static com.vincent.psm.data.DataHelper.KEY_LENGTH;
import static com.vincent.psm.data.DataHelper.KEY_MATERIAL;
import static com.vincent.psm.data.DataHelper.KEY_MATERIALS;
import static com.vincent.psm.data.DataHelper.KEY_NAME;
import static com.vincent.psm.data.DataHelper.KEY_ONSALE;
import static com.vincent.psm.data.DataHelper.KEY_PHOTO;
import static com.vincent.psm.data.DataHelper.KEY_PRICE;
import static com.vincent.psm.data.DataHelper.KEY_PRODUCTS;
import static com.vincent.psm.data.DataHelper.KEY_SEARCH_MODE;
import static com.vincent.psm.data.DataHelper.KEY_STATUS;
import static com.vincent.psm.data.DataHelper.KEY_SUCCESS;
import static com.vincent.psm.data.DataHelper.KEY_THICK;
import static com.vincent.psm.data.DataHelper.KEY_WIDTH;

public class ProductSearchActivity extends AppCompatActivity {
    private Activity activity;

    private FrameLayout laySearchOption;
    private ImageView btnSubmit;
    private EditText edtKeyword;
    private Spinner spnKeyword;
    private CheckBox chkInclude;
    private RecyclerView recyProduct;
    private ProgressBar prgBar;

    private String keyword;
    private ArrayList<String> materials, colors;
    private ArrayAdapter<String> adpMaterial, adpColor;

    private MyOkHttp conn;
    private ArrayList<Tile> tiles;
    private ProductDisplayAdapter adapter;

    private byte searchMode;
    private boolean isShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_search);
        activity = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView txtBarTitle = toolbar.findViewById(R.id.txtToolbarTitle);
        txtBarTitle.setText("搜尋產品");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        laySearchOption = findViewById(R.id.laySearchOption);
        btnSubmit = findViewById(R.id.btnSubmit);
        RadioGroup rgpSearchMode = findViewById(R.id.rgpSearchMode);
        edtKeyword = findViewById(R.id.edtKeyword);
        spnKeyword = findViewById(R.id.spnKeyword);
        chkInclude = findViewById(R.id.chkInclude);
        recyProduct = findViewById(R.id.recyclerView);
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        prgBar = findViewById(R.id.prgBar);

        btnSubmit.setImageResource(R.drawable.icon_search);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchMode == 0 || edtKeyword.getText().toString().equals("") || keyword.equals("請選擇"))
                    Toast.makeText(activity, "未定義搜尋依據", Toast.LENGTH_SHORT).show();
                else if (searchMode == 1 || searchMode == 2) {
                    keyword = edtKeyword.getText().toString();
                    searchProduct();
                }
            }
        });

        rgpSearchMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rdoId:
                        searchMode = 1;
                        edtKeyword.setVisibility(View.VISIBLE);
                        spnKeyword.setVisibility(View.GONE);
                        break;
                    case R.id.rdoName:
                        searchMode = 2;
                        edtKeyword.setVisibility(View.VISIBLE);
                        spnKeyword.setVisibility(View.GONE);
                        break;
                    case R.id.rdoMaterial:
                        searchMode = 3;
                        edtKeyword.setVisibility(View.GONE);
                        spnKeyword.setVisibility(View.VISIBLE);
                        spnKeyword.setAdapter(adpMaterial);
                        break;
                    case R.id.rdoColor:
                        searchMode = 4;
                        edtKeyword.setVisibility(View.GONE);
                        spnKeyword.setVisibility(View.VISIBLE);
                        spnKeyword.setAdapter(adpColor);
                        break;
                }
            }
        });

        spnKeyword.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                keyword = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        swipeRefreshLayout.setEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (conn != null && !isShown)
            searchProduct();
        else if (!isShown)
            loadData();
    }

    private void loadData() {
        btnSubmit.setVisibility(View.GONE);
        laySearchOption.setVisibility(View.INVISIBLE);
        prgBar.setVisibility(View.VISIBLE);

        conn = new MyOkHttp(activity, new MyOkHttp.TaskListener() {
            @Override
            public void onFinished(JSONObject resObj) throws JSONException {
                if (resObj.getBoolean(KEY_STATUS)) {
                    if (resObj.getBoolean(KEY_SUCCESS)) {
                        JSONArray aryMaterial = resObj.getJSONArray(KEY_MATERIALS);
                        JSONArray aryColor = resObj.getJSONArray(KEY_COLORS);
                        materials = new ArrayList<>();
                        colors = new ArrayList<>();
                        materials.add("請選擇");
                        colors.add("請選擇");

                        for (int i = 0; i < aryMaterial.length(); i++)
                            materials.add(aryMaterial.getJSONObject(i).getString(KEY_MATERIAL));
                        for (int i = 0; i < aryColor.length(); i++)
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

    private void showData() {
        adpMaterial = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, materials);
        adpColor = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, colors);
        btnSubmit.setVisibility(View.VISIBLE);
        laySearchOption.setVisibility(View.VISIBLE);
        prgBar.setVisibility(View.GONE);
    }

    private void searchProduct() {
        isShown = false;
        prgBar.setVisibility(View.VISIBLE);
        btnSubmit.setVisibility(View.GONE);
        recyProduct.setVisibility(View.GONE);

        conn = new MyOkHttp(activity, new MyOkHttp.TaskListener() {
            @Override
            public void onFinished(JSONObject resObj) throws JSONException {
                if (resObj.length() == 0) {
                    Toast.makeText(activity, "沒有網路連線", Toast.LENGTH_SHORT).show();
                    prgBar.setVisibility(View.GONE);
                    return;
                }
                if (resObj.getBoolean(KEY_STATUS)) {
                    if(resObj.getBoolean(KEY_SUCCESS)) {
                        tiles = new ArrayList<>();
                        JSONArray ary = resObj.getJSONArray(KEY_PRODUCTS);
                        for (int i = 0; i < ary.length(); i++) {
                            JSONObject obj = ary.getJSONObject(i);
                            tiles.add(new Tile(
                                    obj.getString(KEY_ID),
                                    obj.getString(KEY_PHOTO),
                                    obj.getString(KEY_NAME),
                                    obj.getString(KEY_LENGTH),
                                    obj.getString(KEY_WIDTH),
                                    obj.getString(KEY_THICK),
                                    obj.getString(KEY_PRICE)
                            ));
                        }
                    }else {
                        Toast.makeText(activity, "沒有符合的產品", Toast.LENGTH_SHORT).show();
                    }
                    showProduct();
                }else {
                    Toast.makeText(activity, "伺服器發生例外", Toast.LENGTH_SHORT).show();
                }
            }
        });
        try {
            JSONObject reqObj = new JSONObject();
            reqObj.put(KEY_SEARCH_MODE, searchMode);
            reqObj.put(KEY_KEYWORD, keyword);
            reqObj.put(KEY_ONSALE, chkInclude.isChecked() ? 0 : 1);
            //conn.setSafely(true);
            //conn.execute(getString(R.string.link_search_product), reqObj.toString());
            Toast.makeText(activity, reqObj.toString(), Toast.LENGTH_SHORT).show();
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showProduct() {
        recyProduct.setHasFixedSize(true);
        recyProduct.setLayoutManager(new LinearLayoutManager(activity));

        adapter = new ProductDisplayAdapter(getResources(), activity, tiles, 10);
        adapter.setBackgroundColor(getResources(), R.color.card_product);
        recyProduct.setAdapter(adapter);
        recyProduct.setVisibility(View.VISIBLE);

        prgBar.setVisibility(View.GONE);
        btnSubmit.setVisibility(View.VISIBLE);
        tiles = null;
        isShown = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (conn != null)
            conn.cancel();
    }

    @Override
    public void onDestroy() {
        if (adapter != null) {
            adapter.destroy(true);
            adapter = null;
        }
        System.gc();
        super.onDestroy();
    }
}
