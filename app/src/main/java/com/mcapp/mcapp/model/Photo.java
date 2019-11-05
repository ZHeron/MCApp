package com.mcapp.mcapp.model;

import java.io.Serializable;

public class Photo implements Serializable {
   private String id;
   private String name;
   private String content;
   private int createTime;
   private byte[] imagesByte;

    public byte[] getImagesByte() {
        return imagesByte;
    }

    public void setImagesByte(byte[] imagesByte) {
        this.imagesByte = imagesByte;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCreateTime() {
        return createTime;
    }

    public void setCreateTime(int createTime) {
        this.createTime = createTime;
    }
}
