package com.mominur77.Jobs_BD.model;

public class Admob {
    private String inter1;
    private String inter2;
    private String banner1;

    public Admob() {
    }

    public Admob(String inter1, String inter2, String banner1) {
        this.inter1 = inter1;
        this.inter2 = inter2;
        this.banner1 = banner1;
    }

    public String getInter1() {
        return inter1;
    }

    public void setInter1(String inter1) {
        this.inter1 = inter1;
    }

    public String getInter2() {
        return inter2;
    }

    public void setInter2(String inter2) {
        this.inter2 = inter2;
    }

    public String getBanner1() {
        return banner1;
    }

    public void setBanner1(String banner1) {
        this.banner1 = banner1;
    }
}
