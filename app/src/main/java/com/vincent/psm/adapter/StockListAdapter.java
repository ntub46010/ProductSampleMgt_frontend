package com.vincent.psm.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vincent.psm.R;
import com.vincent.psm.data.ImageObj;
import com.vincent.psm.data.Tile;
import com.vincent.psm.network_helper.GetBitmapTask;
import com.vincent.psm.structure.ImageDownloadQueue;

import java.util.ArrayList;

public class StockListAdapter extends BaseAdapter {
    private Resources res;
    private LayoutInflater layoutInflater;
    private int layout, lastPosition, backgroundColor, queueVolume;

    private ArrayList<Tile> tiles;
    private ImageDownloadQueue queue;

    public StockListAdapter(Resources res, Context context, ArrayList<Tile> tiles, int layout, int queueVolume) {
        this.res = res;
        this.tiles = tiles;
        this.layout = layout;
        this.layoutInflater = LayoutInflater.from(context);
        this.queueVolume = queueVolume;
        this.queue = new ImageDownloadQueue(queueVolume);
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
        ImageView imgBookPic = convertView.findViewById(R.id.imgBookSummaryPic);
        TextView txtId = convertView.findViewById(R.id.txtId);
        TextView txtName = convertView.findViewById(R.id.txtProductName);

        linearLayout.setBackgroundColor(res.getColor(backgroundColor));
        imgBookPic.setImageBitmap(tiles.get(i).getImg());
        txtId.setText(tiles.get(i).getId());
        txtName.setText(tiles.get(i).getName());

        //依滑動方向檢查圖片
        if (i > lastPosition) { //往下滑
            if (tiles.get(i).getImg() == null) { //若發現沒圖片
                setGetBitmapTask(i, imgBookPic); //指派下載器給該項目，放入佇列自動下載
                queue.enqueueFromRear(tiles.get(i));
            }
        }else { //往上滑
            if (tiles.get(i).getImg() == null) { //若發現沒圖片
                setGetBitmapTask(i, imgBookPic); //指派下載器給該項目，放入佇列自動下載
                queue.enqueueFromFront(tiles.get(i));
            }
        }

        lastPosition = i;
        return convertView;
    }

    private void setGetBitmapTask(final int i, final ImageView imageView) {
        tiles.get(i).setGetBitmap(new GetBitmapTask(res.getString(R.string.link_image), new GetBitmapTask.TaskListener() {
            @Override
            public void onFinished() {
                imageView.setImageBitmap(tiles.get(i).getImg());
                //notifyDataSetChanged(); //不可
            }
        }));
    }

    public void setBackgroundColor(int color) {
        this.backgroundColor = color;
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
