package com.khelfi.snackdemostaffside.Model;

/**
 * Created by norma on 23/12/2017.
 */

public class Category {

    private String name;
    private String link;

    public Category() {
    }

    public Category(String name, String link) {
        this.name = name;
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
