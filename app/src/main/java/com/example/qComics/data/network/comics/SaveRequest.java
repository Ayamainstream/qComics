package com.example.qComics.data.network.comics;

public class SaveRequest {
    private String comicName;
    private String username;

    public String getComicName() {
        return comicName;
    }

    public void setComicName(String comicName) {
        this.comicName = comicName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
