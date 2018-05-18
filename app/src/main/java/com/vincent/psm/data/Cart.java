package com.vincent.psm.data;

public class Cart {
    private String id, cartName, salesName, salesId;
    private String customerName, customerPhone, contactPerson, contactPhone;
    private int total;

    //購物車首頁
    public Cart(String id, String cartName, int total, String salesName, String salesId) {
        this.id = id;
        this.cartName = cartName;
        this.total = total;
        this.salesName = salesName;
        this.salesId = salesId;
    }

    //新增購物車
    public Cart(String cartName, String customerName, String customerPhone, String contactPerson, String contactPhone) {
        this.cartName = cartName;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.contactPerson = contactPerson;
        this.contactPhone = contactPhone;
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

    public String getCustomerPhone() {
        return customerPhone;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public int getTotal() {
        return total;
    }

    public String getSalesId() {
        return salesId;
    }



}
