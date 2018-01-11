package com.khelfi.snackdemostaffside.Model;

/**
 * Created by norma on 24/12/2017.
 *
 */

public class Food {

    private String name, imageLink, description, price, menuId;

    public Food() {
    }

    public Food(String name, String imageLink, String description, String price, String menuId) {
        this.name = name;
        this.imageLink = imageLink;
        this.description = description;
        this.price = price;
        this.menuId = menuId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }
}
