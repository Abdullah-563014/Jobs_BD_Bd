package com.mominur77.Jobs_BD.model.continuepage;

public class ImageNotice {

    private String imageUrl;
    private String targetUrl;

    public ImageNotice() {
    }

    public ImageNotice(String imageUrl, String targetUrl) {
        this.imageUrl = imageUrl;
        this.targetUrl = targetUrl;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

}
