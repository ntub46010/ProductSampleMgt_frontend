package com.vincent.psm.data;

public class Specification {
    protected int id;
    protected String name;

    public Specification() {

    }

    public Specification(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
