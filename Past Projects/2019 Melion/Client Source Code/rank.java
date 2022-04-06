package com.example.melion;

public class rank implements Comparable<rank>{
    private int rank;
    private String name;
    private int points;

    public rank(int rank, String name,int points){
        this.rank = rank;
        this.name = name;
        this.points = points;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    @Override
    public int compareTo(rank other){
        return Integer.compare(this.rank,other.rank);
    }

}
