package com.vincent.psm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vincent.psm.R;

public class MainFunctionAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private int[] icons;
    private String[] titles;

    public MainFunctionAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);

        icons = new int[] {
                R.drawable.fun_browse,
                R.drawable.fun_management,
                R.drawable.fun_cart,
                R.drawable.fun_order,
                R.drawable.fun_notification,
                R.drawable.fun_logout,
                R.drawable.fun_management,
                R.drawable.fun_management
        };

        titles = new String[] {
                "瀏覽產品",
                "產品管理",
                "購物車",
                "訂單",
                "通知",
                "登出",
                "推播測試",
                "上傳圖片測試"
        };
    }

    @Override
    public int getCount() {
        return icons.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        if (view == null)
            view = layoutInflater.inflate(R.layout.content_main_function, parent, false);

        ImageView imgFunc = view.findViewById(R.id.imgFunc);
        TextView txtFunc = view.findViewById(R.id.txtFunc);
        imgFunc.setImageResource(icons[position]);
        txtFunc.setText(titles[position]);

        return view;
    }
}
