package com.example.melion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.example.melion.data.FriendListElement;
import com.example.melion.data.ServerCom;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.function.Function;

public class friendList extends AppCompatActivity {

    FragmentManager fragmanager;
    private ServerCom serverCom;
    private int userId;
    private ArrayList<FriendListElement> requestedFriendList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = this.getIntent();
        setContentView(R.layout.activity_friend_list);
        serverCom = new ServerCom(this);
        userId  = intent.getIntExtra("id", -1);
        Bundle bundle = intent.getExtras();
        requestedFriendList = (ArrayList<FriendListElement>) bundle.getSerializable("friends");

        configureButtons();

        Bundle fragBundle = new Bundle();
        fragBundle.putSerializable("friends", requestedFriendList);

        Fragment frag = friendList_Friends.newInstance(userId, fragBundle);
        fragmanager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmanager.beginTransaction();
        transaction.add(R.id.fragment_container, frag);
        transaction.commit();
    }

    private void configureButtons() {
        configureBackButton();

        Button friends = (Button) findViewById(R.id.friendList_friends);
        Button requests = (Button) findViewById(R.id.friendList_requests);

        friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle fragBundle = new Bundle();
                fragBundle.putSerializable("friends", requestedFriendList);

                Fragment frag = friendList_Friends.newInstance(userId, fragBundle);
                fragmanager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmanager.beginTransaction();
                transaction.replace(R.id.fragment_container, frag);
                transaction.commit();
            }
        });

        requests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle fragBundle = new Bundle();
                fragBundle.putSerializable("friends", requestedFriendList);

                Fragment frag = friendList_Requests.newInstance(userId, fragBundle);
                fragmanager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmanager.beginTransaction();
                transaction.replace(R.id.fragment_container, frag);
                transaction.commit();
            }
        });
    }


    public void configureBackButton() {
        Button backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void getOwnFriendsList() {
        getFriendList(-1);
    }

    private void getFriendList(int userId) {
        requestedFriendList = new ArrayList<FriendListElement>();
        Function<JSONObject, Void> fun = jIn -> {
            try {
                JSONArray jA = jIn.getJSONArray("friends");
                JSONObject j;
                for (int i = 0; i < jA.length(); i++) {
                    j = jA.getJSONObject(i);

                    requestedFriendList.add(new FriendListElement(j.getInt("id"), j.getString("name"),
                            j.getInt("phone"), j.getInt("team"), j.getString("message"),
                            j.getInt("since"), j.getInt("painted-together"), j.getInt("relation"),
                            j.getInt("direction")));

                }



            } catch (Exception e) {
                System.out.println("!d " + e.getMessage());
            }
            return null;
        };
        if (userId > -1) {
            serverCom.request("friends/list/" + userId, Request.Method.GET, null, fun);
        } else {
            serverCom.request("friends/list", Request.Method.GET, null, fun);
        }

    }

    public void searchByName(String userName) {    //reacts on call from fragment  //TODO make it so when more user have the same name you can choose
        Function<JSONObject, Void> fun = jIn -> {
            try {
                ArrayList<FriendListElement> searchResult = new ArrayList<>();
                JSONArray jA = jIn.getJSONArray("users");
                JSONObject j;
                for (int i = 0; i < jA.length(); i++) {
                    j = jA.getJSONObject(i);
                    /*searchResult.add(new FriendListElement(j.getInt("id"), j.getString("name"),j.getInt("phone"), j.getInt("team"), j.getString("message"),
                            j.getInt("since"), j.getInt("painted-together"), j.getInt("relation"),
                            j.getInt("direction")));*/
                    searchResult.add(new FriendListElement(j.getInt("id"),j.getString("name")));
                }
                System.out.println("!d got em");
                addNewFriend(searchResult.get(0).id);
            } catch (Exception e) {
                showMassage("Error when trying to add Friend");
                System.out.println("!d " + e.getMessage());
            }
            return null;
        };
        serverCom.request("search/name/" + userName, Request.Method.GET, null, fun);
    }


    private void addNewFriend(int userID) {
        Function<JSONObject, Void> fun = jIn -> {
            try {
                if (jIn.getString("status").equals("ok")) {
                    System.out.println("!d friend has been added :" + userID);
                    showMassage("Friend has been added");
                } else {
                    showMassage("Error when trying to add Friend");
                    System.out.println("!d failed to add friend");
                }
            } catch (Exception e) {
                showMassage("Error when trying to add Friend");
                System.out.println("!d " + e.getMessage());
            }

            return null;
        };
        serverCom.request("friends/add/" + userID, Request.Method.GET, null, fun);
    }

    public void acceptNewFriend(int userID) {
        Function<JSONObject, Void> fun = jIn -> {
            try {
                if (jIn.getString("status").equals("ok")) {
                    showMassage("FriendRequest was accepted");
                    System.out.println("!d friend has been accepted :" + userID);
                } else {
                    System.out.println("!d failed to accept friend");
                }
            } catch (Exception e) {
                System.out.println("!d " + e.getMessage());
            }

            return null;
        };
        serverCom.request("friends/accept/" + userID, Request.Method.GET, null, fun);
    }

    public void removeFriend(int userID) {
        Function<JSONObject, Void> fun = jIn -> {
            try {
                if (jIn.getString("status").equals("ok")) {
                    System.out.println("!d friend has been removed :" + userID);
                    showMassage("FriendRequest was rejected");
                } else {
                    System.out.println("!d failed to remove friend");
                }
            } catch (Exception e) {
                System.out.println("!d " + e.getMessage());
            }

            return null;
        };
        serverCom.request("friends/remove/" + userID, Request.Method.GET, null, fun);
    }

    protected void showMassage(String Massage) {
        Toast.makeText(this, Massage, Toast.LENGTH_SHORT).show();
    }
}