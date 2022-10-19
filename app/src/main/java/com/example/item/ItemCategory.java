package com.example.item;

public class ItemCategory {

    private int CategoryId;
    private String CategoryName;
    private String CategoryImageUrl;
    private String Counting;

    public String getCategoryImageUrl() {
        return CategoryImageUrl;
    }

    public void setCategoryImageUrl(String categoryImageUrl) {
        CategoryImageUrl = categoryImageUrl;
    }

    public String getCounting() {
        return Counting;
    }

    public void setCounting(String counting) {
        Counting = counting;
    }

    public int getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(int id) {
        this.CategoryId = id;
    }


    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String name) {
        this.CategoryName = name;
    }

    public String getCategoryImage() {
        return CategoryImageUrl;

    }

    public void setCategoryImage(String image) {
        this.CategoryImageUrl = image;
    }

}
