package com.vincent.psm.data;

import android.app.AlertDialog;
import android.content.Context;

import com.vincent.psm.R;

import java.util.regex.Pattern;

public class Verifier {
    private Context c;
    private String ptnText = "[\\u4e00-\\u9fa5a-zA-Z_0-9]{%s,%s}";
    private String ptnNumber = "[0-9]{%s,%s}";

    private String lowerProductName = "1", upperProductName = "30";
    private String lowerMaterial = "1", upperMaterial = "20";
    private String lowerColor = "1", upperColor = "20";
    private String lowerLength = "2", upperLength = "4"; //10~9999 mm
    private String lowerThick = "1", upperThick = "3"; //10~999 mm
    private String lowerPrice = "1", upperPrice = "7"; //0~9,999,999 NT
    private String lowerPs = "0", upperPs = "100";
    private String lowerStock = "1", upperStock = "10"; //0~9,999,999,999 NT

    private String ptnProductName = String.format(ptnText, lowerProductName, upperProductName);
    private String ptnMaterial = String.format(ptnText, lowerMaterial, upperMaterial);
    private String ptnColor = String.format(ptnText, lowerColor, upperColor);
    private String ptnLength = String.format(ptnNumber, lowerLength, upperLength);
    private String ptnThick = String.format(ptnNumber, lowerThick, upperThick);
    private String ptnPrice = String.format(ptnNumber, lowerPrice, upperPrice);
    private String ptnPs = String.format(ptnText, lowerPs, upperPs);
    private String ptnStock = String.format(ptnNumber, lowerStock, upperStock);

    public Verifier(Context context) {
        this.c = context;
    }

    public AlertDialog.Builder getDialog(String title, String msg) {
        return new AlertDialog.Builder(c)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton("確定", null);
    }

    public String chkTitle(String s) {
        if (Pattern.matches(ptnProductName, s))
            return "";
        else
            return c.getString(R.string.chkTitle);
    }

    public String chkMaterial(String s) {
        if (Pattern.matches(ptnMaterial, s) && !s.equals("請選擇"))
            return "";
        else if(s.equals("請選擇"))
            return c.getString(R.string.chkSelect, "材質");
        else
            return c.getString(R.string.chkMaterial);
    }

    public String chkColor(String s) {
        if (Pattern.matches(ptnColor, s) && !s.equals("請選擇"))
            return "";
        else if(s.equals("請選擇"))
            return c.getString(R.string.chkSelect, "顏色");
        else
            return c.getString(R.string.chkColor);
    }

    public String chkLength(String s) {
        if (Pattern.matches(ptnLength, s))
            return "";
        else
            return c.getString(R.string.chkNumber, "長度");
    }

    public String chkWidth(String s) {
        if (Pattern.matches(ptnLength, s))
            return "";
        else
            return c.getString(R.string.chkNumber, "寬度");
    }

    public String chkThick(String s) {
        if (Pattern.matches(ptnThick, s))
            return "";
        else
            return c.getString(R.string.chkNumber, "厚度");
    }

    public String chkPrice(String s) {
        if (Pattern.matches(ptnPrice, s))
            return "";
        else
            return c.getString(R.string.chkPrice);
    }

    public String chkPs(String s) {
        if (Pattern.matches(ptnPs, s))
            return "";
        else
            return c.getString(R.string.chkPs);
    }

    public String chkStock(String s) {
        if (Pattern.matches(ptnStock, s))
            return "";
        else
            return c.getString(R.string.chkNumber, "庫存量");
    }

    public String chkSafeStock(String s) {
        if (Pattern.matches(ptnStock, s))
            return "";
        else
            return c.getString(R.string.chkNumber, "安全庫存量");
    }
}
