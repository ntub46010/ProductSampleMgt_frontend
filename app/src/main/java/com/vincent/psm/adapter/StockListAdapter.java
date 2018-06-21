package com.vincent.psm.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.vincent.psm.R;
import com.vincent.psm.data.DataHelper;
import com.vincent.psm.data.Tile;
import com.vincent.psm.network_helper.ImageDownloader;
import com.vincent.psm.product.ProductDetailActivity;
import com.vincent.psm.product.ProductUpdateActivity;
import com.vincent.psm.structure.ImageDownloadQueue;

import java.math.BigDecimal;
import java.util.ArrayList;

import static com.vincent.psm.data.DataHelper.KEY_ID;
import static com.vincent.psm.data.DataHelper.KEY_NAME;

public class StockListAdapter extends BaseAdapter {
    private Resources res;
    private Context context;
    private LayoutInflater layoutInflater;
    private int layout, lastPosition, queueVolume;

    private ArrayList<Tile> tiles;
    private ImageDownloadQueue queue;

    public int pressPosition;
    public Dialog dialog;

    public StockListAdapter(Resources res, Context context, ArrayList<Tile> tiles, int layout, int queueVolume) {
        this.res = res;
        this.context = context;
        this.tiles = tiles;
        this.layout = layout;
        this.layoutInflater = LayoutInflater.from(context);
        this.queueVolume = queueVolume;
        this.queue = new ImageDownloadQueue(queueVolume);
        //prepareDialog();
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
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = layoutInflater.inflate(layout, parent, false);

        LinearLayout linearLayout = convertView.findViewById(R.id.layBookSummary);
        ImageView imgBookPic = convertView.findViewById(R.id.imgProduct);
        TextView txtId = convertView.findViewById(R.id.txtId);
        TextView txtName = convertView.findViewById(R.id.txtProductName);
        TextView txtStock = convertView.findViewById(R.id.txtStock);

        if (tiles.get(i).getOnSale())
            linearLayout.setBackgroundColor(res.getColor(R.color.lst_stock_onsale));
        else
            linearLayout.setBackgroundColor(res.getColor(R.color.lst_stock_offsale));

        Tile tile = (Tile) getItem(i);
        imgBookPic.setImageBitmap(tile.getImg());
        txtId.setText(tile.getId());
        txtName.setText(tile.getName());
        txtStock.setText(tile.getStock() + " / " + tile.getSafeStock());

        if (tile.getStock() < tile.getSafeStock())
            txtStock.setTextColor(Color.parseColor("#FF5050"));
        else
            txtStock.setTextColor(Color.parseColor("#666666"));


        //依滑動方向檢查圖片
        if (i > lastPosition) { //往下滑
            if (tile.getImg() == null) { //若發現沒圖片
                setGetBitmapTask(i, imgBookPic); //指派下載器給該項目，放入佇列自動下載
                queue.enqueueFromRear(tiles.get(i));
            }
        }else { //往上滑
            if (tile.getImg() == null) { //若發現沒圖片
                setGetBitmapTask(i, imgBookPic); //指派下載器給該項目，放入佇列自動下載
                queue.enqueueFromFront(tiles.get(i));
            }
        }

        lastPosition = i;
        return convertView;
    }

    private void setGetBitmapTask(final int i, final ImageView imageView) {
        tiles.get(i).setImageDownloader(new ImageDownloader(res.getString(R.string.link_image), new ImageDownloader.TaskListener() {
            @Override
            public void onFinished() {
                imageView.setImageBitmap(tiles.get(i).getImg());
                //notifyDataSetChanged(); //不可
            }
        }));
    }

    public void destroy(boolean isFully) {
        if (queue != null) {
            queue.destroy();
            if (isFully)
                queue = null;
            else
                queue = new ImageDownloadQueue(queueVolume);
        }
    }
}
