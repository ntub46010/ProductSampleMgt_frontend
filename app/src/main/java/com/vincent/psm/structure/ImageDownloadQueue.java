package com.vincent.psm.structure;

import com.vincent.psm.data.ImageObj;

public class ImageDownloadQueue extends Queue {
    private int volume;

    public ImageDownloadQueue(int volume) {
        this.volume = volume;
    }

    @Override
    protected void onEnqueue(Object obj, boolean isFromFront) {
        if (size() > volume) { //超出容量
            if (isFromFront) //若從前端加入，則從後端清除
                dequeueFromRear();
            else //從後端加入，則從前端清除
                dequeueFromFront();
        }

        if (((ImageObj) obj).getImg() == null) //若圖片為空，則啟動自帶的下載器
            ((ImageObj) obj).startDownloadImage();
    }

    @Override
    protected void onDequeue(Object obj) {
        ((ImageObj) obj).cancelDownloadImage(); //取消下載
        ((ImageObj) obj).setImageDownloader(null); //清除下載器
        ((ImageObj) obj).setImg(null); //清除圖片
    }

    public int getSize() {
        return size();
    }

    @Override
    public void destroy() {
        //取消所有下載，並清除圖片
        for (int i = 0; i < size(); i++) {
            ((ImageObj) get(i)).cancelDownloadImage();
            ((ImageObj) get(i)).setImageDownloader(null);
            ((ImageObj) get(i)).setImg(null);
        }
        clear();
    }
}
