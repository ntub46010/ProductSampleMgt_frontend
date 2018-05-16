package com.vincent.psm.data;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.widget.SimpleAdapter;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataHelper {
    public static String loginUserId = "ntub46010";
    public static byte authority = 2;

    public static final String KEY_STATUS = "Status";
    public static final String KEY_SUCCESS = "Success";
    public static final String KEY_EDIT_MODE = "EditMode";

    public static final String KEY_ACCOUNT = "Account";
    public static final String KEY_PASSWORD = "Password";
    public static final String KEY_USER_INFO = "UserInfo";
    public static final String KEY_IDENTITY = "Identity";
    public static final String KEY_ONSALE = "OnSale";
    public static final String KEY_PRODUCT_INFO = "ProductInfo";
    public static final String KEY_ID = "Id";
    public static final String KEY_PHOTO = "Photo";
    public static final String KEY_NAME = "Name";
    public static final String KEY_MATERIAL = "Material";
    public static final String KEY_COLOR = "Color";
    public static final String KEY_LENGTH = "Length";
    public static final String KEY_WIDTH = "Width";
    public static final String KEY_THICK = "Thick";
    public static final String KEY_PRICE = "Price";
    public static final String KEY_PS = "Ps";
    public static final String KEY_Stock = "Stock";
    public static final String KEY_SAFE_STOCK = "SafeStock";


    public static final String KEY_PRODUCTS = "Products";
    public static final String KEY_MATERIALS = "Materials";
    public static final String KEY_COLORS = "Colors";

    public static String getMD5(String s) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(s.getBytes(), 0, s.length());
            BigInteger i = new BigInteger(1, m.digest());
            return String.format("%1$032x", i).toUpperCase();
        }catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static SimpleAdapter getSimpleAdapter(Context context, int layoutId, int layoutIconId, int layoutTitleId, int[] icon, String[] title) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i< icon.length ; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("icon", icon[i]);
            item.put("shareme", title[i]);
            list.add(item);
        }

        SimpleAdapter adapter = new SimpleAdapter(
                context,
                list,
                layoutId,
                new String[] {"icon", "shareme"},
                new int[] {layoutIconId, layoutTitleId}
        );

        return  adapter;
    }
}
