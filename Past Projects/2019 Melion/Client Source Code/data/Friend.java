package com.example.melion.data;

public class Friend extends User {
    public int numberOfPoints;
    public int friendsSince;
    public int paintedTogether;
    public int direction;// "direction":"INCOMING_OUTGOING__OR__UNKNOWN"

    public Friend(int id, String name, int phone, int team, String message, int numberOfPoints, int friendsSince, int paintedTogether, int direction){
        super(id, name, phone, team, message, RelationStatus.FRIEND);
        this.numberOfPoints = numberOfPoints;
        this.friendsSince = friendsSince;
        this.paintedTogether = paintedTogether;
        this.direction = direction;
        }

}
