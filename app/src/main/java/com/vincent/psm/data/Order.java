package com.vincent.psm.data;

public class Order extends Cart {
    private String customerAddress, predictDeliverDate, deliverPlace, ps;
    private int deliverFee;

    public Order(String customerName, String customerPhone, String customerAddress, String contactPerson, String contactPhone,
                 int productTotal, int deliverFee, String predictDeliverDate, String deliverPlace, String ps, String salesId) {
        super.customerName = customerName;
        super.customerPhone = customerPhone;
        this.customerAddress = customerAddress;
        super.contactPerson = contactPerson;
        super.contactPhone = contactPhone;
        super.total = productTotal;
        this.deliverFee = deliverFee;
        this.predictDeliverDate = predictDeliverDate;
        this.deliverPlace = deliverPlace;
        this.ps = ps;
        super.salesId = salesId;
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

    public void setDeliverPlace(String deliverPlace) {
        this.deliverPlace = deliverPlace;
    }

}
