package com.khelfi.snackdemostaffside.Model;

import java.util.List;

/**
 *
 * Created by norma on 26/12/2017.
 */

public class Request {

    private String phone;
    private String name;
    private String address;
    private String total;
    private String status;

    private List<Order> foodList;

    public Request() {
    }

    public Request(String phone, String name, String address, String total, List<Order> foodList) {
        this.phone = phone;
        this.name = name;
        this.address = address;
        this.total = total;
        this.foodList = foodList;
        this.status = "0";  // Default: 0. 0: Placed, 1: Shipping, 2: Delivered
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<Order> getFoodList() {
        return foodList;
    }

    public void setFoodList(List<Order> foodList) {
        this.foodList = foodList;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}