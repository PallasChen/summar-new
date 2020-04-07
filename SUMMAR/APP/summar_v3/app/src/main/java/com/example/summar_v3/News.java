package com.example.summar_v3;

public class News {



    private String id;
    private String imageUrl;
    private String title;
    private String summary;
    private String url;
    private String date;
    private String classification;
    private String keyword;
    private String citizen_id;

    public void setCitizen_id(String citizen_id) {
        this.citizen_id = citizen_id;
    }

    public String getCitizen_id() {
        return citizen_id;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getDate() {
        return date;
    }

    public String getClassification() {
        return classification;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public News(String imageUrl, String title, String summary) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.summary = summary;
    }
    public News(String title, String summary) {
        this.title = title;
        this.summary = summary;
    }
    public News(String id,String imageUrl,String title, String summary,String url,String date,String classification,String keyword) {
        this.id=id;
        this.imageUrl=imageUrl;
        this.title = title;
        this.summary = summary;
        this.url=url;
        this.date=date;
        this.classification=classification;
        this.keyword = keyword;
    }
    public News(String id,String imageUrl,String title, String summary,String url,String date,String classification,String keyword,String citizen_id) {
        this.id=id;
        this.imageUrl=imageUrl;
        this.title = title;
        this.summary = summary;
        this.url=url;
        this.date=date;
        this.classification=classification;
        this.keyword = keyword;
        this.citizen_id=citizen_id;
    }

    public News() {
    }
}
