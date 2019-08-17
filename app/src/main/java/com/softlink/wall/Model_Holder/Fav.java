package com.softlink.wall.Model_Holder;

public class Fav {
    String link, key;

    public Fav(String link, String key) {
        this.link = link;
        this.key = key;
    }

    public Fav() {
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
