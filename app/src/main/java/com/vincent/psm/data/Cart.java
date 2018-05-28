package com.vincent.psm.data;

public class Cart {
    protected String id, salesName;
    private String cartName;
    protected String salesId;
    protected String customerName, customerPhone, contactPerson, contactPhone;
    protected int total;
    private String createTime;

    public Cart() {

    }

    //購物車首頁
    public Cart(String id, String cartName, int total, String salesName, String salesId, String createTime) {
        this.id = id;
        this.cartName = cartName;
        this.total = total;
        this.salesName = salesName;
        this.salesId = salesId;
        this.createTime = createTime;
    }

    //新增購物車
    public Cart(String cartName, String customerName, String customerPhone, String contactPerson, String contactPhone) {
        this.cartName = cartName;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.contactPerson = contactPerson;
        this.contactPhone = contactPhone;
    }

    //購物車摘要
    public Cart(String salesName, String customerName, String customerPhone, String contactPerson, String contactPhone, int total) {
        this.salesName = salesName;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.contactPerson = contactPerson;
        this.contactPhone = contactPhone;
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

    public String getCreateTime() {
        return createTime;
    }

}
