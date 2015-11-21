package com.technospaces.locationapp;

/**
 * Created by Coder on 9/12/2015.
 */
public class Place {
    private int _id;
    private String name;
    private double latitude;
    private double longitude;

    public Place(){

    }
    public Place( String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        String s = this.getName().toUpperCase();
        return s;
    }
}
