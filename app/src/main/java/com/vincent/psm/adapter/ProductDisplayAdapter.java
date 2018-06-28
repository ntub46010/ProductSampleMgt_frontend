package com.vincent.psm.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vincent.psm.R;
import com.vincent.psm.data.Tile;
import com.vincent.psm.network_helper.ImageDownloader;
import com.vincent.psm.product.ProductDetailActivity;
import com.vincent.psm.structure.ImageDownloadQueue;

import java.util.ArrayList;

import static com.vincent.psm.data.DataHelper.Comma;
import static com.vincent.psm.data.DataHelper.KEY_ID;
import static com.vincent.psm.data.DataHelper.KEY_NAME;

public class ProductDisplayAdapter extends RecyclerView.Adapter<ProductDisplayAdapter.DataViewHolder> {
    private Context context;
    private Resources res;
    private ArrayList<Tile> tiles;
    private ImageDownloadQueue queue;
    private int lastPosition = -1, backgroundColor, queueVolume;

    public ProductDisplayAdapter(Resources res, Context context, ArrayList<Tile> tiles, int queueVolume){
        this.res = res;
        this.context = context;
        this.tiles = tiles;
        this.queueVolume = queueVolume;
        this.queue = new ImageDownloadQueue(queueVolume);
    }

    public class DataViewHolder extends RecyclerView.ViewHolder {
        // 連結資料的顯示物件宣告
        private int position;
        private CardView cardView;
        private LinearLayout layProductCard;
        private ImageView imgProduct;
        private TextView txtProductName, txtProductSize, txtProductPrice;

        DataViewHolder(View itemView) {
            super(itemView);

            // 連結資料的顯示物件取得
            cardView = itemView.findViewById(R.id.cardView);
            layProductCard = itemView.findViewById(R.id.layProductCard);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtProductSize = itemView.findViewById(R.id.txtProductSize);
            txtProductPrice = itemView.findViewById(R.id.txtProductPrice);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent it = new Intent(context, ProductDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(KEY_ID, tiles.get(position).getId());
                    bundle.putString(KEY_NAME, tiles.get(position).getName());
                    it.putExtras(bundle);
                    context.startActivity(it);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return tiles.size();
    }

    public Tile getItem(int position) {
        return tiles.get(position);
    }

    @Override
    public ProductDisplayAdapter.DataViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        // 顯示資料物件來自 R.layout.card_product 中
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_product, viewGroup, false);
        ProductDisplayAdapter.DataViewHolder dataViewHolder = new ProductDisplayAdapter.DataViewHolder(view);
        return dataViewHolder;
    }

    public void onBindViewHolder(ProductDisplayAdapter.DataViewHolder dataViewHolder, int i) {
        // 顯示資料物件及資料項目的對應
        try {
            dataViewHolder.layProductCard.setBackgroundColor(res.getColor(backgroundColor));
        }catch (Exception e) {} //顏色資源未找到，因為未用set方法設定

        //依滑動方向檢查圖片
        if (i > lastPosition) { //往下滑
            if (tiles.get(i).getImg() == null) { //若發現沒圖片
                setGetBitmapTask(i, dataViewHolder); //指派下載器給該項目，放入佇列自動下載
                queue.enqueueFromRear(tiles.get(i));
            }
        }else { //往上滑
            if (tiles.get(i).getImg() == null) { //若發現沒圖片
                setGetBitmapTask(i, dataViewHolder); //指派下載器給該項目，放入佇列自動下載
                queue.enqueueFromFront(tiles.get(i));
            }
        }

        dataViewHolder.position = i;

        dataViewHolder.imgProduct.setImageBitmap((tiles.get(i)).getImg());
        dataViewHolder.txtProductName.setText((tiles.get(i)).getName());

        Tile tile = tiles.get(i);
        if (tile.getLength() != null) { //產品首頁
            dataViewHolder.txtProductSize.setText(context.getString(R.string.txt_product_size, tile.getLength(), tile.getWidth(), tile.getThick()));
            dataViewHolder.txtProductPrice.setText("$ " + Comma(tile.getPrice()));
        }else if (tiles.get(i).getSubTotal() != -1) { //購物車明細
            dataViewHolder.txtProductSize.setText(Comma(tile.getAmount()) + " / " + tile.getStock() + " 個");
            dataViewHolder.txtProductPrice.setText("$ " + Comma(tile.getSubTotal()));

            if (tile.getAmount() > tile.getStock()) {
                dataViewHolder.txtProductSize.setTextColor(Color.parseColor("#FF5050"));
                dataViewHolder.txtProductSize.setTypeface(Typeface.DEFAULT_BOLD);
            }else
                dataViewHolder.txtProductSize.setTextColor(Color.parseColor("#666666"));
        }

        lastPosition = i;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void setBackgroundColor(Resources r, int color) {
        this.backgroundColor = color;
    }

    private void setGetBitmapTask(final int i, final DataViewHolder dataViewHolder) {
        tiles.get(i).setImageDownloader(new ImageDownloader(res.getString(R.string.link_image), new ImageDownloader.TaskListener() {
            @Override
            public void onFinished() {
                dataViewHolder.imgProduct.setImageBitmap(tiles.get(i).getImg());
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
