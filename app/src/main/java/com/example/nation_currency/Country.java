package com.example.nation_currency;

public class Country {

    private String name;
    private String code;
    private String capital;
    private Double area;
    private Integer population;
    private String currency;
    private String languages;
    private String continentName;
    private String urlMapImage;
    private String urlFlagImage;

    public Country(String mName, String mCode, String mCapital, Double mArea, Integer mPopulation, String mCurrency,
                   String mLanguages, String mContinentName){
        name = mName;
        code = mCode;
        capital = mCapital;
        area = mArea;
        population = mPopulation;
        currency = mCurrency;
        languages = mLanguages;
        continentName = mContinentName;
        urlMapImage = String.format("http://img.geonames.org/img/country/250/%s.png", code);
        urlFlagImage = String.format("http://img.geonames.org/flags/x/%s.gif", code.toLowerCase());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    public String getContinentName() {
        return continentName;
    }

    public void setContinentName(String continentName) {
        this.continentName = continentName;
    }

    public String getUrlMapImage() {
        return urlMapImage;
    }

    public void setUrlMapImage(String urlMapImage) {
        this.urlMapImage = urlMapImage;
    }

    public String getUrlFlagImage() {
        return urlFlagImage;
    }

    public void setUrlFlagImage(String urlFlagImage) {
        this.urlFlagImage = urlFlagImage;
    }

    @Override
    public String toString() {
        return "Country{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", capital='" + capital + '\'' +
                ", area='" + area + '\'' +
                ", population='" + population + '\'' +
                ", currency='" + currency + '\'' +
                ", languages='" + languages + '\'' +
                ", continentName='" + continentName + '\'' +
                ", urlMapImage='" + urlMapImage + '\'' +
                ", urlFlagImage='" + urlFlagImage + '\'' +
                '}';
    }


}
