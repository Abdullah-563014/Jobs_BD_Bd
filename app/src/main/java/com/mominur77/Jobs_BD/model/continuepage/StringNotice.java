package com.mominur77.Jobs_BD.model.continuepage;

public class StringNotice {
    private String shortNotice;
    private String targetUrl;

    public StringNotice() {
    }

    public StringNotice(String shortNotice, String targetUrl) {
        this.shortNotice = shortNotice;
        this.targetUrl = targetUrl;
    }


    public String getShortNotice() {
        return shortNotice;
    }

    public void setShortNotice(String shortNotice) {
        this.shortNotice = shortNotice;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }


}
