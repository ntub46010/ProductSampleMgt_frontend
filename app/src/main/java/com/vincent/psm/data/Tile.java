package com.vincent.psm.data;

public class Tile extends ImageObj {
    private String id, name, length, width, thick;
    private String material, color, ps;
    private int price, amount, subTotal = -1, stock, safeStock;
    private boolean onSale = false;

    //產品管理
    public Tile(String id, String photo, String name, int stock, int safeStock, boolean onSale) {
        this.id = id;
        super.imgURL = photo;
        this.name = name;
        this.stock = stock;
        this.safeStock = safeStock;
        this.onSale = onSale;
    }

    //產品首頁列表
    public Tile(String id, String photo, String name, String length, String width, String thick, int price) {
        this.id = id;
        super.imgURL = photo;
        this.name = name;
        this.length = length;
        this.width = width;
        this.thick = thick;
        this.price = price;
    }

    //購物車列表
    public Tile(String id, String photo, String name, int amount, int subTotal, int stock) {
        this.id = id;
        super.imgURL = photo;
        this.name = name;
        this.amount = amount;
        this.subTotal = subTotal;
        this.stock = stock;
    }

    //新增產品、產品詳情
    public Tile(String id, String photo, String name, String material, String color, String length, String width, String thick
            , int price, String ps, int stock, int safeStock, boolean onSale) {
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
        this.onSale = onSale;
    }

    //訂單明細
    public Tile(String id, String name, String length, String width, String thick, int price, int amount, int subTotal) {
        this.id = id;
        this.name = name;
        this.length = length;
        this.width = width;
        this.thick = thick;
        this.price = price;
        this.amount = amount;
        this.subTotal = subTotal;
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

    public int getPrice() {
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

    public int getStock() {
        return stock;
    }

    public int getSafeStock() {
        return safeStock;
    }

    public boolean getOnSale() {
        return onSale;
    }

    public int getAmount() {
        return amount;
    }

    public int getSubTotal() {
        return subTotal;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setName(String name) {
        this.name = name;
    }

}
