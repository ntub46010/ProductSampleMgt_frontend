package com.vincent.psm.data;

public class Customer extends Specification {
    private String phone, address;

    public Customer(int id, String name, String phone, String address) {
        super.id = id;
        super.name = name;
        this.phone = phone;
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }
}
