package com.vincent.psm.data;

public class Condition {
    private int id;
    private String condition;

    public Condition(int id, String condition) {
        this.id = id;
        this.condition = condition;
    }

    public int getId() {
        return id;
    }

    public String getCondition() {
        return condition;
    }
}
