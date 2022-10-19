package com.example.item;

public class ItemCity {
    private String cityId;
    private String cityName;
    private  String cityImage;
    private  String Counting;
    public String getCityImage() {
        return cityImage;
    }

    public String getCounting() {
        return Counting;
    }

    public void setCounting(String counting) {
        Counting = counting;
    }

    public void setCityImage(String cityImage) {
        this.cityImage = cityImage;
    }



    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }


}
