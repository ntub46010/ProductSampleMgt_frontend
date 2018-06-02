package com.vincent.psm.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
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

import java.util.ArrayList;

import static com.vincent.psm.data.DataHelper.KEY_ID;
import static com.vincent.psm.data.DataHelper.KEY_NAME;

public class StockListAdapter extends BaseAdapter {
    private Resources res;
    private Context context;
    private LayoutInflater layoutInflater;
    private int layout, lastPosition, backgroundColor, queueVolume;

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
        prepareDialog();
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

        if (tiles.get(i).getOnSale())
            linearLayout.setBackgroundColor(res.getColor(R.color.lst_stock_onsale));
        else
            linearLayout.setBackgroundColor(res.getColor(R.color.lst_stock_offsale));

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
        tiles.get(i).setImageDownloader(new ImageDownloader(res.getString(R.string.link_image), new ImageDownloader.TaskListener() {
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

    private void prepareDialog() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dlg_list_options);
        dialog.setCancelable(true);

        String[] textGroup = {"查看", "編輯"};
        int[] iconGroup = {
                R.drawable.icon_check,
                R.drawable.icon_edit
        };

        ListView listView = dialog.findViewById(R.id.lstOptions);
        listView.setAdapter(DataHelper.getSimpleAdapter(
                context,
                R.layout.lst_text_with_icon_black,
                R.id.imgIcon,
                R.id.txtTitle,
                iconGroup,
                textGroup
        ));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemName = ((TextView)view.findViewById(R.id.txtTitle)).getText().toString();
                Intent it;
                Bundle bundle;
                switch (itemName) {
                    case "查看":
                        it = new Intent(context, ProductDetailActivity.class);
                        bundle = new Bundle();
                        bundle.putString(KEY_ID, tiles.get(pressPosition).getId());
                        bundle.putString(KEY_NAME, tiles.get(pressPosition).getName());
                        it.putExtras(bundle);
                        context.startActivity(it);
                        break;

                    case "編輯":
                        it = new Intent(context, ProductUpdateActivity.class);
                        bundle = new Bundle();
                        bundle.putString(KEY_ID, tiles.get(pressPosition).getId());
                        bundle.putString(KEY_NAME, tiles.get(pressPosition).getName());
                        it.putExtras(bundle);
                        context.startActivity(it);
                        break;
                }
                dialog.dismiss();
            }
        });
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
