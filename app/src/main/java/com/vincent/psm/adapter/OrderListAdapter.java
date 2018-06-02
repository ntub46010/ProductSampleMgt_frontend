package com.vincent.psm.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vincent.psm.R;
import com.vincent.psm.data.Cart;
import com.vincent.psm.data.Order;

import java.util.ArrayList;

import static com.vincent.psm.data.DataHelper.Comma;

public class OrderListAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private ArrayList<Order> orders;

    public OrderListAdapter(Context context, ArrayList<Order> orders) {
        layoutInflater = LayoutInflater.from(context);
        this.orders = orders;
    }

    @Override
    public int getCount() {
        return orders.size();
    }

    @Override
    public Object getItem(int position) {
        return orders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null)
            view = layoutInflater.inflate(R.layout.lst_order, parent, false);

        TextView txtId = view.findViewById(R.id.txtOrderId);
        TextView txtCustomerName = view.findViewById(R.id.txtCustomerName);
        TextView txtTotal = view.findViewById(R.id.txtTotal);
        TextView txtDeliverDate = view.findViewById(R.id.txtDeliverDate);
        TextView txtCondition = view.findViewById(R.id.txtCondition);

        Order order = (Order) getItem(position);
        txtId.setText(order.getId());
        txtCustomerName.setText(order.getCustomerName());
        txtTotal.setText("$ " + Comma(String.valueOf(order.getTotal())));
        txtDeliverDate.setText(order.getPredictDeliverDate().replace("-", "/"));
        txtCondition.setText(order.getCondition());

        switch (order.getCondition()) {
            case "待處理":
                txtCondition.setTextColor(Color.parseColor("#FF5050"));
                break;
            case "已完成":
                txtCondition.setTextColor(Color.parseColor("#00A000"));
                break;
            default:
                txtCondition.setTextColor(Color.parseColor("#CC9900"));
                break;
        }

        return view;
    }
}
