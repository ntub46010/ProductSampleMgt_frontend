package com.vincent.psm.data;

public class Tile extends ImageObj {
    private String id, name, length, width, thick, price;

    //商品列表
    public Tile(String id, String photo, String name, String length, String width, String thick, String price) {
        this.id = id;
        super.imgURL = photo;
        this.name = name;
        this.length = length;
        this.width = width;
        this.thick = thick;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public String getPhoto() {
        return super.imgURL;
    }

    public String getName() {
        return name;
    }

    public String getLength() {
        return length;
    }

    public String getWidth() {
        return width;
    }

    public String getThick() {
        return thick;
    }

    public String getPrice() {
        return price;
    }


}
