package com.example.asus.photogallery;

/**
 * Created by asus on 2016/9/25.
 */
public class GalleryItem {

    private  String url;
    private String caption;
    private  String id;

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }



    public  String toString()
    {
        return caption;
    }
}
