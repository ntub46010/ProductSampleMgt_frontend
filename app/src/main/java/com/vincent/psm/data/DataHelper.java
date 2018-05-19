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
    public static String defaultCartId = "";
    public static String defaultCartName = "";

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
    public static final String KEY_STOCK = "Stock";
    public static final String KEY_SAFE_STOCK = "SafeStock";
    public static final String KEY_CART_AMOUNT = "CartAmount";
    public static final String KEY_CART_NAME = "CartName";
    public static final String KEY_CUSTOMER_NAME = "CustomerName";
    public static final String KEY_CUSTOMER_PHONE = "CustomerPhone";
    public static final String KEY_CONTACT_PERSON = "ContactPerson";
    public static final String KEY_CONTACT_PHONE = "ContactPhone";
    public static final String KEY_SALES = "Sales";
    public static final String KEY_SALES_ID = "SalesId";
    public static final String KEY_SALES_NAME = "SalesName";
    public static final String KEY_TOTAL = "Total";
    public static final String KEY_CART_INFO = "CartInfo";
    public static final String KEY_CREATE_TIME = "CreateTime";
    public static final String KEY_AMOUNT = "Amount";
    public static final String KEY_SUBTOTAL = "SubTotal";
    public static final String KEY_CART_ID = "CartId";
    public static final String KEY_PRODUCT_ID = "ProductId";


    public static final String KEY_PRODUCTS = "Products";
    public static final String KEY_MATERIALS = "Materials";
    public static final String KEY_COLORS = "Colors";
    public static final String KEY_CARTS = "Carts";

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
            item.put("title", title[i]);
            list.add(item);
        }

        SimpleAdapter adapter = new SimpleAdapter(
                context,
                list,
                layoutId,
                new String[] {"icon", "title"},
                new int[] {layoutIconId, layoutTitleId}
        );

        return  adapter;
    }

    public static String Comma(String num) {
        boolean negative = num.contains("-");
        if (negative) num = num.substring(1);
        String[] numPart = num.split("\\.");
        String result;

        if (numPart[0].length() < 4)
            result = numPart[0];
        else {
            result = Comma(numPart[0].substring(0, numPart[0].length() - 3))
                    + "," + numPart[0].substring(numPart[0].length() - 3);
        }

        if (numPart.length == 2)
            result += "." + numPart[1];
        if (negative)
            result = "-" + result;

        return result;
    }
}
