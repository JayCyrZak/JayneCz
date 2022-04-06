package com.example.melion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.melion.data.ServerCom;

public class Main_MenueActivity extends AppCompatActivity {


    private Button leaderboard_button;
    private ImageButton map_free_button;
    private ImageButton map_competitive_button;
    private Button profile_button;
    private Button share_screen_button;
    private Button debug_button;
    private Button view_picture_button;

    private ServerCom serverCom;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        * Initialise graphics
        */
        preInit();
        /*
        *Initialise functionality
        */
        Init();

    }

    private void preInit(){
        setContentView(R.layout.activity_main__menue);
    }

    private void Init() {

        /*
        * Transition from the main menu to all other states via buttons
        * */
        leaderboard_button = createTransitionButton(R.id.leaderboards_button, leaderboards.class);
        map_competitive_button = createTransitionImageButton(R.id.map_competetive, map_competitive.class);
        profile_button = createTransitionButton(R.id.profile_button, profile.class);
        view_picture_button = createTransitionButton(R.id.view_picture_button, view_picture.class);

        serverCom = new ServerCom(this);

        Context con = this.getApplicationContext();
        Main_MenueActivity act = this;
        /*
         * Messages triggered by buttons
         * */
        ImageButton map_free_button = findViewById(R.id.map_free);
        map_free_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(con, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    startActivity(new Intent(Main_MenueActivity.this,map_free.class));
                } else {
                    System.out.println("!d location data permissions not set");
                    ActivityCompat.requestPermissions(act, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }

            }
        });

        ImageButton map_competitive_button = findViewById(R.id.map_competetive);
        map_competitive_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(con, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    startActivity(new Intent(Main_MenueActivity.this,map_competitive.class));
                } else {
                    System.out.println("!d location data permissions not set");
                    ActivityCompat.requestPermissions(act, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
            }
        });


        debug_button = findViewById(R.id.debug_button);
        debug_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serverCom.cycleDemoUser();
                int dU = serverCom.getActiveDemoUser();
                showMassage("Switching to DemoUser "+dU);
            }
        });

    }

    private Button createTransitionButton(final int id, final Class<?> Activity) {
        Button returnedValue = findViewById(id);

            returnedValue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Main_MenueActivity.this, Activity));
                }
            });
        return returnedValue;
    }

    private ImageButton createTransitionImageButton(final int id, final Class<?> Activity) {
        ImageButton returnedValue = findViewById(id);

        returnedValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Main_MenueActivity.this, Activity));
            }
        });
        return returnedValue;
    }




    private Button createToastButton(int id,final String Massage){
        Button returnedValue = findViewById(id);
        returnedValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMassage(Massage);
            }
        });
        return returnedValue;
    }

    protected void showMassage(String Massage) {
        Toast.makeText(this, Massage, Toast.LENGTH_SHORT).show();
    }

}
