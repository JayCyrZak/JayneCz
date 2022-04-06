package com.example.melion.data;

public class UserProfile {
    public int id;
    public String name;
    public int phone; //opt
    public int team;
    public String message;
    public int friendsSince;
    public int numberOfAchievements;
    public int numberOfPoints;
    public int numberOfFriends;
    public int paintedWithFrineds;
    public int paintedTogether;

    //Friend Request
    public int relation; //"FRIENDSHIP_STATUS"
    public int direction;

    public interface RelationStatus {
        int None = 0;
        int Pending = 1;
        int Rejected = 2;
        int Valid = 3;
    }

    public interface RequestDirection {
        int None = 0;
        int In = 1;
        int Out = 2;
    }

    public UserProfile(int id, String name, int phone, int team, String message, int friendsSince, int numberOfAchievements, int numberOfPoints,
                       int numberOfFriends, int paintedWithFrineds, int paintedTogether, int relation, int direction) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.team = team;
        this.message = message;
        this.friendsSince = friendsSince;
        this.numberOfAchievements = numberOfAchievements;
        this.numberOfPoints = numberOfPoints;
        this.numberOfFriends = numberOfFriends;
        this.paintedWithFrineds = paintedWithFrineds;
        this.paintedTogether = paintedTogether;
    }

    public UserProfile(){

    }
}
