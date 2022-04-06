package com.example.melion.ui.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.example.melion.Main_MenueActivity;
import com.example.melion.R;
import com.example.melion.data.ServerCom;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class OnUserFirstLogin extends AppCompatActivity {

    private ServerCom serverCom;
    private TextView melion_speak;
    private EditText profileName;
    private EditText tweetText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_user_first_login);

        profileName = findViewById(R.id.login_Name);
        tweetText = findViewById(R.id.login_Message);
        melion_speak = findViewById(R.id.melion_speech);
        melion_speak.setText("Now to get you started, let's choose a Name to call you and maybe some words you want to tell everyone. Just press the ok button when you're done");

        serverCom = new ServerCom(this);
        configureButtons();
    }

    private void configureButtons() {

        ConstraintLayout constraint = findViewById(R.id.first_login_const);
        constraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                view = getCurrentFocus();
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        Button ok = findViewById(R.id.ok_button);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = profileName.getText().toString();
                String newMessage = tweetText.getText().toString();

                if(!newName.matches("[a-zA-Z0-9\\-_]{1,}")){
                    if(newName.length() < 1) melion_speak.setText("Can you make the Name a little longer, please.");
                    else melion_speak.setText("Please use only a-z and A-Z.");
                    System.out.println("!d name not possible");
                    return;
                }
                ok.setVisibility(View.GONE);
                changeName(newName);
                changeProfileMessage(newMessage);


                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                View view = getCurrentFocus();
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                FragmentManager fragManager = getSupportFragmentManager();
                Fragment fragment = com.example.melion.changeTeam.newInstance("login","");
                FragmentTransaction transaction = fragManager.beginTransaction();
                transaction.replace(R.id.frag_container, fragment);
                transaction.commit();
                melion_speak.setText("Ok, "+newName+" got it. Now all that's left is to pick a team, don't worry all you have told me is always changeable later.");

            }
        });
    }

    public void setchangeTeam(int i){
       changeTeam(i);
    }

    private void changeTeam(int newTeamID){
        if(!(newTeamID > 0 && newTeamID < 4)){
            System.out.println("!d team does not exist");
            return;
        }
        Function<JSONObject, Void> fun = jIn -> {
            try {
                if (jIn.getString("status").equals("ok")) {
                    System.out.println("!d team has been changed to :" + newTeamID);
                    prepforMainMenue();
                } else {
                    System.out.println("!d failed to change Team");
                    prepforMainMenue();
                }
            } catch (Exception e){
                System.out.println("!d " + e.getMessage());
            }

            return null;
        };
        serverCom.request("config/team/" + newTeamID, Request.Method.GET, null, fun);

    }

    private void prepforMainMenue() {
        showMassage("And that's it, we're done. Have fun in Melion. I will be watching from down here");
        startActivity(new Intent(OnUserFirstLogin.this, Main_MenueActivity.class));
        finish();
    }


    private void changeProfileMessage(String newMessage){
        Function<JSONObject, Void> fun = jIn -> {
            try {
                if (jIn.getString("status").equals("ok")) {
                    System.out.println("!d message has been set :" + newMessage);
                    melion_speak.setText("");

                } else {
                    System.out.println("!d failed to change message");
                }
            } catch (Exception e){
                System.out.println("!d " + e.getMessage());
            }

            return null;
        };
        JSONObject j = new JSONObject();
        try {
            j.put("msg", newMessage);
        } catch (Exception e) {
            System.out.println("!d " + e.getMessage());
        }

        serverCom.request("config/message" , Request.Method.POST, j, fun);
    }

    private void changeName(String newName){
        if(!newName.matches("[a-zA-Z0-9\\-_]{1,}")){
            System.out.println("!d name not possible");
            return;
        }
        Function<JSONObject, Void> fun = jIn -> {
            try {
                if (jIn.getString("status").equals("ok")) {
                    System.out.println("!d name has been changed to :" + newName);

                } else {
                    System.out.println("!d failed to change name");
                }
            } catch (Exception e){
                System.out.println("!d " + e.getMessage());
            }

            return null;
        };
        serverCom.request("config/name/" + newName, Request.Method.GET, null, fun);
    }

    protected void showMassage(String Massage) {
        Toast.makeText(this, Massage, Toast.LENGTH_LONG).show();
    }
}
