package com.qb.ListViewVideoViewExp.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/11/5.
 */
public class VideoInfo implements Serializable{
    private String title;
    private String videoUrl;
    private String imgUrl;

    private int imgResouce;

    public VideoInfo(String title,String videoUrl,int imgResouce)
    {
        this.title= title;
        this.videoUrl=videoUrl;
        this.imgResouce=imgResouce;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getImgResouce() {
        return imgResouce;
    }

    public void setImgResouce(int imgResouce) {
        this.imgResouce = imgResouce;
    }
}
