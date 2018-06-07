package com.vincent.psm.data;

public class User {
    private String name, phone, email, authority, identity;

    public User(String name, String phone, String email, String identityId, String identityName) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.authority = identityId;
        this.identity = identityName;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getAuthority() {
        return authority;
    }

    public String getIdentity() {
        return identity;
    }
}
