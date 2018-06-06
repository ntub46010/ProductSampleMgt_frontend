package com.vincent.psm.data;

public class Order extends Cart {
    private String customerAddress, predictDeliverDate, actualDeliverDate, deliverPlace, ps, condition;
    private int deliverFee;

    //訂單首頁
    public Order(String id, String customerName, int productTotal, String predictDeliverDate, String condition) {
        super.id = id;
        super.customerName = customerName;
        super.total = productTotal;
        this.predictDeliverDate = predictDeliverDate;
        this.condition = condition;
    }

    //建立訂單
    public Order(String customerName, String customerPhone, String customerAddress, String contactPerson, String contactPhone,
                 int productTotal, int deliverFee, String predictDeliverDate, String actualDeliverDate, String deliverPlace, String ps) {
        super.customerName = customerName;
        super.customerPhone = customerPhone;
        this.customerAddress = customerAddress;
        super.contactPerson = contactPerson;
        super.contactPhone = contactPhone;
        super.total = productTotal;
        this.deliverFee = deliverFee;
        this.predictDeliverDate = predictDeliverDate;
        this.actualDeliverDate = actualDeliverDate;
        this.deliverPlace = deliverPlace;
        this.ps = ps;;
    }

    //訂單詳情
    public Order(String id, String customerName, String customerPhone, String contactPerson, String contactPhone, int productTotal, int deliverFee,
                 String condition, String predictDeliverDate, String actualDeliverDate, String deliverPlace, String ps, String salesName, String salesId) {
        super.id = id;
        super.customerName = customerName;
        super.customerPhone = customerPhone;
        super.contactPerson = contactPerson;
        super.contactPhone = contactPhone;
        super.total = productTotal;
        this.deliverFee = deliverFee;
        this.condition = condition;
        this.predictDeliverDate = predictDeliverDate;
        this.actualDeliverDate = actualDeliverDate;
        this.deliverPlace = deliverPlace;
        this.ps = ps;
        this.salesName = salesName;
        this.salesId = salesId;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public String getPredictDeliverDate() {
        return predictDeliverDate;
    }

    public String getDeliverPlace() {
        return deliverPlace;
    }

    public String getPs() {
        return ps;
    }

    public int getDeliverFee() {
        return deliverFee;
    }

    public String getCondition() {
        return condition;
    }

    public String getActualDeliverDate() {
        return actualDeliverDate;
    }

    public void setDeliverPlace(String deliverPlace) {
        this.deliverPlace = deliverPlace;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public void setActualDeliverDate(String actualDeliverDate) {
        this.actualDeliverDate = actualDeliverDate;
    }

    public void setSalesId(String salesId) {
        this.customerAddress = salesId;
    }

}
