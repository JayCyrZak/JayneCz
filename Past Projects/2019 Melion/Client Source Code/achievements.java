package com.example.melion;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.android.volley.Request;
import com.example.melion.data.ServerCom;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.function.Function;

public class achievements{

    private int distance;
    private int amtAchievements;
    private int paintedTogether;
    private long longestFriend;
    private int amtFriends;

    private achievement[] userAchievements;
    private achievement[] userUnlockedAchievements;
    private ServerCom communication;
    private static achievements instance;


    /*
    *   Singleton
    */
    public static achievements getInstance(){
        if (instance == null)
            instance = new achievements();
        return instance;
    }

    public void loadAchievements(Context callerContext){
        Function<JSONObject, Void> f = f1 ->{return null;};
        loadAchievements(callerContext, f);
    }

    public void loadAchievements(Context callerContext, Function<JSONObject, Void> f){
            communication = new ServerCom(callerContext);


        Function<JSONObject, Void> achievements = getAchievements -> {
            try {
                JSONObject obj = getAchievements;
                if(obj.has("achievements")) {
                    JSONArray arr = obj.getJSONArray("achievements");;
                    userAchievements = new achievement[arr.length()];
                    for (int i = 0; i < arr.length(); i++) {
                        String name = arr.getJSONObject(i).getString("name");
                        String id_str = arr.getJSONObject(i).getString("id");
                        String description = arr.getJSONObject(i).getString("description");
                        String value_str = arr.getJSONObject(i).getString("value");
                        String objective_str = arr.getJSONObject(i).getString("objective");
                        String objectiveValue_str = arr.getJSONObject(i).getString("objectiveValue");
                        int id = Integer.parseInt(id_str);
                        int value = Integer.parseInt(value_str);
                        int objective = Integer.parseInt(objective_str);
                        int objectiveValue = Integer.parseInt(objectiveValue_str);
                        userAchievements[i] = new achievement(id, name, description, value, objective, objectiveValue);
                    }
                }
            } catch (Exception e){
                System.out.println("!d No Response");
                e.printStackTrace();
            }
            f.apply(getAchievements);
            return null;
        };
        communication.request("/achievement/allachievements", Request.Method.GET , null , achievements);
     }

    public void loadUnlockedAchievements(Context callerContext){
        Function<JSONObject, Void> f = f1 ->{return null;};
        loadAchievements(callerContext, f);
    }

    public void loadUnlockedAchievements(Context callerContext,Function<JSONObject, Void> f){
            communication = new ServerCom(callerContext);

        Function<JSONObject, Void> achievements = getAchievements -> {
            try {
                JSONObject obj = getAchievements;
                if(obj.has("achievements")){
                JSONArray arr = obj.getJSONArray("achievements");
                userUnlockedAchievements = new achievement[arr.length()];
                for (int i = 0; i < arr.length(); i++) {
                    String name = arr.getJSONObject(i).getString("name");
                    int id = arr.getJSONObject(i).getInt("id");
                    String description = arr.getJSONObject(i).getString("description");
                    int value= arr.getJSONObject(i).getInt("value");
                    int objective = arr.getJSONObject(i).getInt("objective");
                    int objectiveValue = arr.getJSONObject(i).getInt("objectiveValue");
                    userUnlockedAchievements[i] = new achievement(id, name, description, value, objective, objectiveValue);
                    if (userAchievements != null) {
                        for (int j = 0; j < userAchievements.length; j++) {
                            if (userAchievements[j].getId() == id) {
                                userAchievements[j].setUnlocked(true);
                                j = userAchievements.length;
                            }
                        }
                    }
                }
                }
            } catch (Exception e){
                System.out.println("!d No Response");
                e.printStackTrace();
            }
            f.apply(getAchievements);
            return null;
        };
        communication.request("/achievement/unlockedachievements", Request.Method.GET , null , achievements);
    }

    private void unlockAchievement(Context callerContext, achievement achievement_to_unlock){
        if(communication == null) {
            communication = new ServerCom(callerContext);
        }
            new AlertDialog.Builder(callerContext)
                    .setTitle("Achievement unlocked!")
                    .setMessage("Name: " + achievement_to_unlock.getName() + "\nObjective: " + achievement_to_unlock.getDescription())
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

        Function<JSONObject,Void> unlocked = unlockedAchiev ->{return null;};

        communication.request("achievement/unlock/"+achievement_to_unlock.getId(),Request.Method.POST,null,unlocked);
    }

    public void update(Context callerContext){
        if(userAchievements == null){
            loadAchievements(callerContext);
        }
        Function<JSONObject, Void> update = updateAchievements -> {
            if(userAchievements == null){
                System.out.println("!d something went wrong while loading the achievements");
            }else{
                for(int i = 0; i< userAchievements.length; i++){
                    if(userAchievements[i].isUnlocked()) {
                        continue;
                    }
                    switch(userAchievements[i].getObjective()){
                        case achievement.DISTANCE:
                            if(distance >= userAchievements[i].getObjectiveValue()){
                                unlockAchievement(callerContext, userAchievements[i]);
                            }
                            break;
                        case achievement.FRIENDSFOR:
                            if(calcFriendshipleght(longestFriend) >= userAchievements[i].getObjectiveValue()){
                                unlockAchievement(callerContext, userAchievements[i]);
                            }
                            break;
                        case achievement.PAINTEDTOGETHER:
                            if(paintedTogether >= userAchievements[i].getObjectiveValue()){
                                unlockAchievement(callerContext, userAchievements[i]);
                            }
                            break;
                        case achievement.ACHIEVEMENTSUNLOCKED:
                            if(amtAchievements >= userAchievements[i].getObjectiveValue()){
                                unlockAchievement(callerContext , userAchievements[i]);
                            }
                            break;
                        case achievement.AMOUTOFFRIENDS:
                            if(amtFriends >= userAchievements[i].getObjectiveValue()){
                                unlockAchievement(callerContext , userAchievements[i]);
                            }
                            break;
                        default:
                            System.out.println("!d Error while loading objetives");
                            break;
                    }
                }
            }
            return null;
        };
        loadMostPaintedToghether(callerContext);
        loadLongestFriend(callerContext);
        loadDistance(callerContext);
        loadAmountOfFriends(callerContext);
        loadAchievements(callerContext);
        loadUnlockedAchievements(callerContext,update);
        communication = null;
    }

    public void loadDistance(Context callerContext){
            communication = new ServerCom(callerContext);

        Function<JSONObject, Void> distance = getDistance -> {
            try{
                JSONObject obj = getDistance;
                int distanceTemp= obj.getInt("distance");
                this.distance = distanceTemp;
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        };

        communication.request("/achievement/distance", Request.Method.GET , null , distance);
    }

    public void loadAmountOfFriends(Context callerContext){
        communication = new ServerCom(callerContext);
        Function<JSONObject, Void> amountoffriends = getAmountOfFriends -> {
            try{
                JSONObject obj = getAmountOfFriends;
                int amountoffriendsTemp =  obj.getInt("friends");
                this.amtFriends = amountoffriendsTemp;
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        };
        communication.request("/achievement/amountoffriends", Request.Method.GET , null , amountoffriends);
    }

    private long calcFriendshipleght(long time){
        return (System.currentTimeMillis() - time)/(1000*60*60*24);
    }

    public void loadLongestFriend(Context callerContext){
        communication = new ServerCom(callerContext);
        Function<JSONObject, Void> longestFriend = getLongestFriend -> {
            try{
                JSONObject obj = getLongestFriend;
                long LongestFriendTemp;
                if(obj.getString("longest_friend").equals("None")){
                    LongestFriendTemp = Long.MAX_VALUE;
                }else{
                    LongestFriendTemp = obj.getInt("longest_friend");
                }

                this.longestFriend = LongestFriendTemp;
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        };
        communication.request("/achievement/longestfriend", Request.Method.GET , null , longestFriend);
    }

    public void loadMostPaintedToghether(Context callerContext){
        communication = new ServerCom(callerContext);
        Function<JSONObject, Void> mostpaintedtoghether = getMostPaintedToghether -> {
            try{
                JSONObject obj = getMostPaintedToghether;
                int MostPaintedToghetherTemp;
                if(obj.getString("painted_together").equals("None")){
                    MostPaintedToghetherTemp = 0;
                }else{
                    MostPaintedToghetherTemp =  obj.getInt("painted_together");
                }
                this.paintedTogether = MostPaintedToghetherTemp;
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        };
        communication.request("/achievement/mostpaintedtoghether", Request.Method.GET , null , mostpaintedtoghether);
    }

    public achievement[] getUserUnlockedAchievements() {
        return userUnlockedAchievements;
    }

    public void setUserAchievements(achievement[] a) {
        userAchievements = a;
    }

    public achievement[] getUserAchievements() {
        return userAchievements;
    }
    public ServerCom getCommunication() {
        return communication;
    }
}
