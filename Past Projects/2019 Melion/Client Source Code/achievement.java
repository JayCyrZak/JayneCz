package com.example.melion;

public class achievement {
    /*
     *  Achievement ID received from the server
     */
    private int id;
    /*
     *  Achievement Name
     */
    private String name;
    /*
     *  Achievement weight
     */
    private int value;

    private String description;

    /*
     *  Is this achievement unlocked by the user?
     */
    private int objective;
    public final static int DISTANCE = 1;
    public final static int PAINTEDTOGETHER = 2;
    public final static int ACHIEVEMENTSUNLOCKED = 3;
    public final static int FRIENDSFOR = 4;
    public final static int AMOUTOFFRIENDS = 5;
    private int objectiveValue;

    private boolean unlocked;

    public achievement(int id, String name,String description, int value, int objective, int objectiveValue){
        this.id = id;
        this.name = name;
        this.description = description;
        this.value = value;
        this.objective = objective;
        this.objectiveValue = objectiveValue;
        unlocked = false;
    }

    public achievement(int id, String name, int value, boolean unlocked){
        this.id = id;
        this.name = name;
        this.value = value;
        this.unlocked = unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public int getId() {
                return id;
    }

    public String getName(){
    return name;
    }

    public int getValue() {
        return value;
    }


    public int getObjective() {
        return objective;
    }


    public int getObjectiveValue() {
        return objectiveValue;
    }

    public String getDescription() {
        return description;
    }

    public String toString(){
        return "id: " + id + ", name: " + name + ", description: " + description + ", value: " + value + ", unlocked: " + unlocked+ ", objective: " + objective + ", objectiveValue: " + objectiveValue;
    }

}
