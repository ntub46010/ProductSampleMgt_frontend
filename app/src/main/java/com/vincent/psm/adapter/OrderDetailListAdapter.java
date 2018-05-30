package com.vincent.psm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vincent.psm.R;
import com.vincent.psm.data.Tile;

import java.util.ArrayList;

import static com.vincent.psm.data.DataHelper.Comma;

public class OrderDetailListAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Tile> tiles;

    public OrderDetailListAdapter(Context context, ArrayList<Tile> tiles) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.tiles = tiles;
    }

    @Override
    public int getCount() {
        return tiles.size();
    }

    @Override
    public Object getItem(int position) {
        return tiles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null)
            view = layoutInflater.inflate(R.layout.lst_order_detail, parent, false);

        TextView txtId = view.findViewById(R.id.txtId);
        TextView txtProductName = view.findViewById(R.id.txtProductName);
        TextView txtSize = view.findViewById(R.id.txtArea);
        TextView txtAmount = view.findViewById(R.id.txtAmount);
        TextView txtSubTotal = view.findViewById(R.id.txtSubTotal);

        Tile tile = tiles.get(position);
        txtId.setText(tile.getId());
        txtProductName.setText(tile.getName());
        txtSize.setText(context.getString(R.string.txt_product_size, tile.getLength(), tile.getWidth(), tile.getThick()));
        txtAmount.setText(Comma(String.valueOf(tile.getAmount())) + " 個");
        txtSubTotal.setText(Comma(String.valueOf(tile.getSubTotal())));

        return view;
    }
}
