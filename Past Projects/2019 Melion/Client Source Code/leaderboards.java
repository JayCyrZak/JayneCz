package com.example.melion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class leaderboards extends AppCompatActivity {

    private Button solo;
    private Button friends;
    private Button global;
    private FragmentManager fragmanager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboards);
        configureButtons();

        fragmanager = getSupportFragmentManager();
    }

    private void configureButtons() {
        configureBackButton();

        solo = (Button) findViewById(R.id.leaderboards_solo);
        solo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = fragmanager.beginTransaction();
                transaction.replace(R.id.leaderboards_frag_container, leaderboards_Solo.newInstance("solo",""));
                transaction.commit();
            }
        });

        friends = (Button) findViewById(R.id.leaderboards_friends);
        friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = fragmanager.beginTransaction();
                transaction.replace(R.id.leaderboards_frag_container, leaderboards_Solo.newInstance("friends",""));
                transaction.commit();
            }
        });

        global = (Button) findViewById(R.id.leaderboards_global);
        global.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = fragmanager.beginTransaction();
                transaction.replace(R.id.leaderboards_frag_container, leaderboards_Solo.newInstance("global",""));
                transaction.commit();
            }
        });

    }

    public void configureBackButton(){
        Button backButton = (Button) findViewById(R.id.backbutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
