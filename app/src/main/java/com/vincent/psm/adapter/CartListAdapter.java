package com.vincent.psm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vincent.psm.R;
import com.vincent.psm.data.Cart;

import java.util.ArrayList;

import static com.vincent.psm.data.DataHelper.Comma;

public class CartListAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private ArrayList<Cart> carts;

    public CartListAdapter(Context context, ArrayList<Cart> carts) {
        layoutInflater = LayoutInflater.from(context);
        this.carts = carts;
    }

    @Override
    public int getCount() {
        return carts.size();
    }

    @Override
    public Object getItem(int position) {
        return carts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null)
            view = layoutInflater.inflate(R.layout.lst_cart, parent, false);

        TextView txtCartName = view.findViewById(R.id.txtCartName);
        TextView txtTotal = view.findViewById(R.id.txtTotal);
        TextView txtCreateTime = view.findViewById(R.id.txtCreateTime);
        TextView txtSalesName = view.findViewById(R.id.txtSalesName);

        Cart cart = carts.get(position);
        txtCartName.setText(cart.getCartName());
        txtTotal.setText("$ " + Comma(String.valueOf(cart.getTotal())));
        txtCreateTime.setText(cart.getCreateTime().replace("-", "/"));
        txtSalesName.setText(cart.getSalesName());

        return view;
    }
}
