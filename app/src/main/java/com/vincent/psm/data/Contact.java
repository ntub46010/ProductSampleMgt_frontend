package com.vincent.psm.data;

public class Contact extends Specification {
    private String phone;

    public Contact(int id, String name, String phone) {
        super.id = id;
        super.name = name;
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }
}
