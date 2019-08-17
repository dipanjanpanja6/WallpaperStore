package com.softlink.wall.Model_Holder;

public class GatItem {
    String link, name;

    public GatItem(String link, String name) {
        this.link = link;
        this.name = name;
    }

    public GatItem() {
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
