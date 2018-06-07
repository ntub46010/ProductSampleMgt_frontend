package com.vincent.psm.data;

public class User {
    private String name, phone, email, identity, oldPwd, newPwd, newPwd2;
    private int authority;

    //個人檔案頁面
    public User(String name, String phone, String email, int authority, String identity, String oldPwd) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.authority = authority;
        this.identity = identity;
        this.oldPwd = oldPwd;
    }

    //修改個人資料
    public User(String name, String phone, String email, String oldPwd, String newPwd, String newPwd2) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.oldPwd = oldPwd;
        this.newPwd = newPwd;
        this.newPwd2 = newPwd2;
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

    public int getAuthority() {
        return authority;
    }

    public String getIdentity() {
        return identity;
    }

    public String getOldPwd() {
        return oldPwd;
    }

    public String getNewPwd() {
        return newPwd;
    }

    public String getNewPwd2() {
        return newPwd2;
    }

    public void setNewPwd(String newPwd) {
        this.newPwd = newPwd;
    }
}
