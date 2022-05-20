package com.example.nation_currency;

public class Currency {
    private String code;
    private String name;
    private String url;

    public  Currency(String mCode, String mName){
        code = mCode;
        name = mName;
        url = String.format("https://usd.fxexchangerate.com/%s.xml", code.toLowerCase());
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Currency{" +
                "code='" + code + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
