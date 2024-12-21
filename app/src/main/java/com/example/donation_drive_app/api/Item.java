package com.example.donation_drive_app.api;

import java.util.Date;

public class Item {
    private String id;
    private String name;
    private String hostName;
    private String photoString;
    private String uploadTime;
    private String category;
    private String description;
    private String status;
    private String reservedUserId;

    public Item() {}

    public Item(String id, String name, String hostName,
                String photoString, String uploadTime,
                String category, String description,
                String status, String reservedUserId) {
        this.id = id;
        this.name = name;
        this.hostName = hostName;
        this.photoString = photoString;
        this.uploadTime = uploadTime;
        this.category = category;
        this.description = description;
        this.status = status;
        this.reservedUserId = reservedUserId;
    }

    // overloaded constructor without id for pushing new item to Firebase
    public Item(String name, String hostName, String photoString,
                String uploadTime, String category, String description,
                String status, String reservedUserId) {
        this.name = name;
        this.hostName = hostName;
        this.photoString = photoString;
        this.uploadTime = uploadTime;
        this.category = category;
        this.description = description;
        this.status = status;
        this.reservedUserId = reservedUserId;
    }


    public String getId() {
        return id;
    }

    public String getHostName() {
        return hostName;
    }

    public String getName() {
        return name;
    }

    public String getPhotoString() {
        return photoString;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getReservedUserId() {
        return reservedUserId;
    }
}

