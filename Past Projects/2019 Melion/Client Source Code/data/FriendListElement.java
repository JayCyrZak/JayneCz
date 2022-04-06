package com.example.melion.data;

import java.io.Serializable;

public class FriendListElement implements Serializable {
    public int id;
    public String name;
    public int phone;
    public int team;
    public String message;
    public int friendsSince;
    public int paintedTogether;

    //Friend Request
    public int relation;
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

    public FriendListElement(int id, String name, int phone, int team, String message, int friendsSince,
                             int paintedTogether, int relation, int direction){
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.team = team;
        this.message = message;
        this.friendsSince = friendsSince;
        this.paintedTogether = paintedTogether;
        this.relation = relation;
        this.direction = direction;
        }

        public FriendListElement(int id, String name){
            this.id = id;
            this.name = name;
        }
        public FriendListElement(){
             this.id = id;
             this.name = name;
        }

}
