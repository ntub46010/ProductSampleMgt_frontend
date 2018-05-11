package com.vincent.psm.data;

public class Tile extends ImageObj {
    private String id, name, length, width, thick, price;
    private String material, color, ps, stock, safeStock;

    //商品管理
    public Tile(String id, String photo, String name) {
        this.id = id;
        super.imgURL = photo;
        this.name = name;
    }

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

    //新增商品、商品詳情
    public Tile(String id, String photo, String name, String material, String color, String length, String width, String thick, String price, String ps, String stock, String safeStock) {
        this.id = id;
        super.imgURL = photo;
        this.name = name;
        this.material = material;
        this.color = color;
        this.length = length;
        this.width = width;
        this.thick = thick;
        this.price = price;
        this.ps = ps;
        this.stock = stock;
        this.safeStock = safeStock;
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

    public String getMaterial() {
        return material;
    }

    public String getColor() {
        return color;
    }

    public String getPs() {
        return ps;
    }

    public String getStock() {
        return stock;
    }

    public String getSafeStock() {
        return safeStock;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
