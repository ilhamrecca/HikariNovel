package com.tubes.lightnovel.Model;

import java.util.List;

public class Novel    {
    private String Name;
    private String Image;
    private String Category;
    private String Sinopsis;

    public String getSinopsis() {
        return Sinopsis;
    }

    public void setSinopsis(String sinopsis) {
        Sinopsis = sinopsis;
    }

    private List<Volume> Volume;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public List<Volume> getVolume() {
        return Volume;
    }

    public void setVolume(List<Volume> volume) {
        Volume = volume;
    }
}