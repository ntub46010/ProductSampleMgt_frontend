package com.vincent.psm.data;

import android.graphics.Bitmap;

import com.vincent.psm.network_helper.ImageDownloader;

public class ImageObj {
    protected String imgURL;
    public Bitmap img;
    private ImageDownloader imageDownloader;
    private boolean isStartDownload = false;

    public ImageObj() {

    }

    public void setImageDownloader(ImageDownloader imageDownloader) {
        this.imageDownloader = imageDownloader;
    }

    public void startDownloadImage() {
        isStartDownload = true;
        imageDownloader.execute(this);
    }

    public boolean isStartDownload() {
        return isStartDownload;
    }

    public void cancelDownloadImage() {
        if (imageDownloader != null)
            imageDownloader.cancel(true);
    }

    public String getImgURL() {
        return imgURL;
    }

    public Bitmap getImg() {
        return img;
    }

    public void setImgURL(String url) {
        this.imgURL = url;
    }

    public void setImg(Bitmap bitmap) {
        this.img = bitmap;
    }

}
