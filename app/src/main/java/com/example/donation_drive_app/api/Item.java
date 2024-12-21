package com.example.donation_drive_app.api;

import java.util.Date;

public class Item {
    private int id;
    private String name;
    private String hostName;
    private String photoSource;
    private Date uploadTime;
    private String category;
    private String description;
    private String status;
    private int reservedUserId;

    public Item(int id, String hostName, String name,
                String photoSource, Date uploadTime,
                String category, String description,
                String status, int reservedUserId) {
        this.id = id;
        this.name = name;
        this.hostName = hostName;
        this.photoSource = photoSource;
        this.uploadTime = uploadTime;
        this.category = category;
        this.description = description;
        this.status = status;
        this.reservedUserId = reservedUserId;
    }

    public int getId() {
        return id;
    }

    public String getHostName() {
        return hostName;
    }

    public String getName() {
        return name;
    }

    public String getPhotoSource() {
        return photoSource;
    }

    public Date getUploadTime() {
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

    public int getReservedUserId() {
        return reservedUserId;
    }
}

