package com.vincent.psm.data;

public class Cart {
    private String id, cartName, salesName;
    private String customerName, contactPerson;
    private int total;

    //購物車首頁
    public Cart(String id, String cartName, String salesName, int total) {
        this.id = id;
        this.cartName = cartName;
        this.salesName = salesName;
        this.total = total;
    }

    public String getId() {
        return id;
    }

    public String getCartName() {
        return cartName;
    }

    public String getSalesName() {
        return salesName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public int getTotal() {
        return total;
    }



}
