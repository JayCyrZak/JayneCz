package com.example.melion.data;

public class User {
    public int id;
    public String name;
    public int phone; //opt
    public int team;
    public String message;
    public int relation; //"FRIENDSHIP_STATUS"

    public interface RelationStatus {
        int UNKNOWN = 0;
        int FRIEND = 1;
    }

    public User(int id, String name, int phone, int team, String message, int relation){
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.team = team;
        this.message = message;
        this.relation = relation;
    }
}
