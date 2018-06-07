package com.vincent.psm.data;

import android.app.AlertDialog;
import android.content.Context;

import com.vincent.psm.R;

import java.util.regex.Pattern;

public class Verifier {
    private Context c;
    private String ptnText = "[\\u4e00-\\u9fa5a-zA-Z_0-9]{%s,%s}"; //不包含標點符號
    private String ptnNumber = "[0-9]{%s,%s}";
    private String ptnEmail = "^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$";

    private String lowerProductName = "1", upperProductName = "30";
    private String lowerMaterial = "1", upperMaterial = "20";
    private String lowerColor = "1", upperColor = "20";
    private String lowerLength = "2", upperLength = "4"; //10~9999 mm
    private String lowerThick = "1", upperThick = "3"; //10~999 mm
    private String lowerPrice = "1", upperPrice = "7"; //0~9,999,999 NT
    private String lowerPs = "0", upperPs = "100";
    private String lowerStock = "1", upperStock = "10"; //0~9,999,999,999
    private String lowerCustomerName = "0", upperCustomerName = "50";
    private String lowerCustomerPhone = "0", upperCustomerPhone = "15";
    private String lowerContactName = "0", upperContactName = "20";
    private String lowerName = "1", upperName = "30";
    private String lowerPhone = "7", upperPhone = "15";
    private String lowerPwd = "6", upperPwd = "15";

    private String ptnProductName = String.format(ptnText, lowerProductName, upperProductName);
    private String ptnMaterial = String.format(ptnText, lowerMaterial, upperMaterial);
    private String ptnColor = String.format(ptnText, lowerColor, upperColor);
    private String ptnLength = String.format(ptnNumber, lowerLength, upperLength);
    private String ptnThick = String.format(ptnNumber, lowerThick, upperThick);
    private String ptnPrice = String.format(ptnNumber, lowerPrice, upperPrice);
    private String ptnPs = String.format(ptnText, lowerPs, upperPs);
    private String ptnStock = String.format(ptnNumber, lowerStock, upperStock);
    private String ptnCustomerName = String.format(ptnText, lowerCustomerName, upperCustomerName);
    private String ptnCustomerPhone = String.format(ptnText, lowerCustomerPhone, upperCustomerPhone);
    private String ptnContactName = String.format(ptnText, lowerContactName, upperContactName);
    private String ptnName = String.format(ptnText, lowerName, upperName);
    private String ptnPhone = String.format(ptnNumber, lowerPhone, upperPhone);
    private String ptnPwd = String.format(ptnText, lowerPwd, upperPwd);

    public Verifier(Context context) {
        this.c = context;
    }

    public AlertDialog.Builder getDialog(String title, String msg) {
        return new AlertDialog.Builder(c)
                .setTitle(title)
                .setMessage(msg)
                .setCancelable(true)
                .setPositiveButton("確定", null);
    }

    public String chkTitle(String s) {
        if (Pattern.matches(ptnProductName, s))
            return "";
        else
            return c.getString(R.string.chk_range_words, "產品名稱", String.valueOf(lowerProductName), String.valueOf(upperProductName));
    }

    public String chkMaterial(String s) {
        if (Pattern.matches(ptnMaterial, s) && !s.equals("請選擇"))
            return "";
        else if(s.equals("請選擇"))
            return c.getString(R.string.chk_is_empty, "材質");
        else
            return c.getString(R.string.chk_range_words, "材質", String.valueOf(lowerMaterial), String.valueOf(upperMaterial));
    }

    public String chkColor(String s) {
        if (Pattern.matches(ptnColor, s) && !s.equals("請選擇"))
            return "";
        else if(s.equals("請選擇"))
            return c.getString(R.string.chk_is_empty, "顏色");
        else
            return c.getString(R.string.chk_range_words, "顏色", String.valueOf(lowerColor), String.valueOf(upperColor));
    }

    public String chkLength(String s) {
        if (Pattern.matches(ptnLength, s))
            return "";
        else
            return c.getString(R.string.chk_number, "長度");
    }

    public String chkWidth(String s) {
        if (Pattern.matches(ptnLength, s))
            return "";
        else
            return c.getString(R.string.chk_number, "寬度");
    }

    public String chkThick(String s) {
        if (Pattern.matches(ptnThick, s))
            return "";
        else
            return c.getString(R.string.chk_number, "厚度");
    }

    public String chkPrice(String s) {
        if (Pattern.matches(ptnPrice, s))
            return "";
        else
            return c.getString(R.string.chk_format_wrong, "價格");
    }

    public String chkPs(String s) {
        if (Pattern.matches(ptnPs, s))
            return "";
        else
            return c.getString(R.string.chk_max_words, "備註", String.valueOf(upperPs));
    }

    public String chkStock(String s) {
        if (Pattern.matches(ptnStock, s))
            return "";
        else
            return c.getString(R.string.chk_number, "庫存量");
    }

    public String chkSafeStock(String s) {
        if (Pattern.matches(ptnStock, s))
            return "";
        else
            return c.getString(R.string.chk_number, "安全庫存量");
    }

    public String chkCartName(String s) {
        if (Pattern.matches(ptnProductName, s))
            return "";
        else
            return c.getString(R.string.chk_cart_name);
    }

    public String chkCustomerName(String s) {
        if (Pattern.matches(ptnCustomerName, s))
            return "";
        else
            return c.getString(R.string.chk_max_words, "客戶名稱", String.valueOf(upperCustomerName));
    }

    public String chkCustomerPhone(String s) {
        if (Pattern.matches(ptnCustomerPhone, s))
            return "";
        else
            return c.getString(R.string.chk_max_words, "客戶電話", String.valueOf(upperCustomerPhone));
    }

    public String chkContactName(String s) {
        if (Pattern.matches(ptnContactName, s))
            return "";
        else
            return c.getString(R.string.chk_max_words, "聯絡人名稱", String.valueOf(upperContactName));
    }

    public String chkContactPhone(String s) {
        if (Pattern.matches(ptnCustomerPhone, s))
            return "";
        else
            return c.getString(R.string.chk_max_words, "聯絡人電話", String.valueOf(upperCustomerPhone));
    }

    public String chkName(String title, String s) {
        if (Pattern.matches(ptnName, s))
            return "";
        else
            return c.getString(R.string.chk_range_words, title, String.valueOf(lowerName), String.valueOf(upperName));
    }

    public String chkPhone(String title, String s) {
        if (Pattern.matches(ptnPhone, s))
            return "";
        else
            return c.getString(R.string.chk_range_words, title, String.valueOf(lowerPhone), String.valueOf(upperPhone));
    }

    public String chkAddress(String s) {
        if (Pattern.matches(ptnPs, s))
            return "";
        else
            return c.getString(R.string.chk_max_words, "地址", String.valueOf(upperPs));
    }

    public String chkProductTotal(String s) {
        if (Pattern.matches(ptnPrice, s))
            return "";
        else
            return c.getString(R.string.chk_is_empty, "產品總金額");
    }

    public String chkDeliverFee(String s) {
        if (Pattern.matches(ptnPrice, s))
            return "";
        else
            return c.getString(R.string.chk_format_wrong, "運費");
    }

    public String chkEmail(String s) {
        if (Pattern.matches(ptnEmail, s))
            return "";
        else
            return c.getString(R.string.chk_format_wrong, "信箱");
    }

    public String chkNewPwd(String pwd1, String pwd2) {
        if (!Pattern.matches(ptnPwd, pwd1))
            return "新密碼格式錯誤\n";
        else if (!pwd1.equals(pwd2))
            return "新密碼不一致\n";
        else
            return "";
    }
}
