package com.example.melion.data;

public class Tile {
    private double latitude;
    private double longitude;
    private String color;
    private int size;

    public Tile(double latitude, double longitude, String color, int size) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.color = color;
        this.size = size;
    }

    @Override
    public boolean equals(Object object) {
        double accuracy = 0.00001;
        if (object == this) {
            return true;
        }
        if (!(object instanceof Tile)) {
            return false;
        }
        Tile tile = (Tile) object;
        if (tile.color.equals(color) && Math.abs(tile.latitude - latitude) < accuracy && Math.abs(tile.longitude - longitude) < accuracy) {
            return true;
        }
        return false;
    }

    public String getColor() {
        return color;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getSize() {
        return size;
    }
}