package com.example.qComics.data.network.comics;

public class Chapter {

    private Integer id;
    private String name;
    private String comicName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getData() {
        return comicName;
    }

    public void setData(String data) {
        this.comicName = data;
    }

}
