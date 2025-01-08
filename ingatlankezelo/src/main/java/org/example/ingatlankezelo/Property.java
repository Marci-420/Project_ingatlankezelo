package org.example.ingatlankezelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Property {

    private int id;
    private String address;
    private String type;
    private double rentPrice;
    private String status;

    public Property(String address, String type, double rentPrice, String status) {
        this.address = address;
        this.type = type;
        this.rentPrice = rentPrice;
        this.status = status;
    }

    public Property(int id, String address, String type, double rentPrice, String status) {
        this.id = id;
        this.address = address;
        this.type = type;
        this.rentPrice = rentPrice;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getRentPrice() {
        return rentPrice;
    }

    public void setRentPrice(double rentPrice) {
        this.rentPrice = rentPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
