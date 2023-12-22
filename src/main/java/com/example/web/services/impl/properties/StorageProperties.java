package com.example.web.services.impl.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
public class StorageProperties {

    /**
     * Folder location for storing files
     */
    private String location = "src\\main\\resources\\static\\img\\userImage\\";
    private String videoLocation = "src\\main\\resources\\video\\";

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getVideoLocation() {return videoLocation;}
}