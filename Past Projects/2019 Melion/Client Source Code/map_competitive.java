package com.example.melion;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.example.melion.data.ServerCom;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;


import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.function.Function;

import static android.graphics.Color.TRANSPARENT;
import static android.graphics.Color.parseColor;


public class map_competitive extends FragmentActivity implements OnMapReadyCallback {
    private ServerCom serverCom;

    private GoogleMap mMap;

    private LatLng currentPos;
    private FusedLocationProviderClient fusedLocationClient;
    private static float DEFAULT_ZOOM = 9;

    private TextView score_txt;
    private TextView names_txt;
    private TextView[] nameViews;
    private Button host_button;
    private Button center_button;
    private Button search_button;
    private Button match_button;
    private Button team_button;
    private Button join_button;

    private static double GRID_IN_METERS = 5;
    // latitude & longitude conversion: https://stackoverflow.com/a/1253545
    private static double LAT_GRID = 0.00000904371 * GRID_IN_METERS;
    private static double LNG_GRID = 0.00001349055 * GRID_IN_METERS;

    private boolean running = true;
    private boolean inTeam = false;
    private boolean match = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_competitive);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        configureBackButton();
        score_txt = findViewById(R.id.score_txt);
        names_txt = findViewById(R.id.names_txt);
        nameViews = new TextView[]{findViewById(R.id.name1_txt), findViewById(R.id.name2_txt), findViewById(R.id.name3_txt), findViewById(R.id.name4_txt), findViewById(R.id.name5_txt)};


        host_button = findViewById(R.id.host_button);
        host_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    host_button.setVisibility(View.INVISIBLE);
                    search_button.setVisibility(View.INVISIBLE);
                    center_button.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    System.out.println("!d " + e.getMessage());
                }
            }
        });

        center_button = findViewById(R.id.center_button);
        center_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    center = currentPos;
                    createTeam(center.latitude, center.longitude);
                } catch (Exception e) {
                    System.out.println("!d " + e.getMessage());
                }
            }
        });

        search_button = findViewById(R.id.search_button);
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    searchTeam();
                } catch (Exception e) {
                    System.out.println("!d " + e.getMessage());
                }
            }
        });

        match_button = findViewById(R.id.match_button);
        match_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    matchmaking();
                } catch (Exception e) {
                    System.out.println("!d " + e.getMessage());
                }
            }
        });

        team_button = findViewById(R.id.team_button);
        team_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    selectedTeam = (selectedTeam + 1) % availableTeams.size();
                } catch (Exception e) {
                    System.out.println("!d " + e.getMessage());
                }
            }
        });

        join_button = findViewById(R.id.join_button);
        join_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    joinTeam(availableTeams.get(selectedTeam).first);
                } catch (Exception e) {
                    System.out.println("!d " + e.getMessage());
                }
            }
        });

        serverCom = new ServerCom(this);

        //----------------------------------------------

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        final Handler handler = new Handler();
        class MyRunnable implements Runnable {
            private Handler handler;

            public MyRunnable(Handler handler) {
                this.handler = handler;
            }

            @Override
            public void run() {
                if (running) {
                    loop();
                    handler.postDelayed(this, 1000);
                }
            }
        }
        handler.post(new MyRunnable(handler));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Enable Location data if given permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            System.out.println("!d location data permissions not set");
        }

        mMap.moveCamera(CameraUpdateFactory.zoomTo(18.0f));

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // move camera to new location
                currentPos = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(currentPos));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location == null) {
                            System.out.println("!d Failure: last known location is null");
                        } else {
                            // Move the camera to the player location
                            currentPos = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentPos));
                            // Zoom in on the map
                            if (mMap.getMinZoomLevel() > DEFAULT_ZOOM) {
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(mMap.getMinZoomLevel()));
                            } else {
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));
                            }
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentPos));
                        }
                    }
                });
    }

    public void configureBackButton() {
        Button backButton = (Button) findViewById(R.id.backbutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                end();
                inTeam = false;
                match = false;
                running = false;
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        end();
        inTeam = false;
        match = false;
        running = false;
        finish();
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private Polygon drawTile(LatLng position, int team) {
        if (team < 0) {
            // no team
            return null;
        }
        int color = toColor(team);

        double minLat = position.latitude - LAT_GRID / 2;
        double maxLat = position.latitude + LAT_GRID / 2;
        double minLng = position.longitude - LNG_GRID / 2;
        double maxLng = position.longitude + LNG_GRID / 2;

        PolygonOptions rectOptions = new PolygonOptions()
                .add(new LatLng(minLat, minLng),
                        new LatLng(minLat, maxLng),
                        new LatLng(maxLat, maxLng),
                        new LatLng(maxLat, minLng))
                .strokeWidth(1)
                .strokeColor(color)
                .fillColor(color);
        return mMap.addPolygon(rectOptions);
    }

    private int toColor(int team) {
        String color;
        if (team == 1) {
            color = "FF0000"; // red
        } else if (team == 2) {
            color = "0000FF"; // blue
        } else {
            color = "00FF00"; // lime
        }
        return parseColor("#FF" + color);
    }

    //---------------------------------------------------------------------------
    //---------------------------------------------------------------------------
    //---------AB HIER ECHTER COMPETITIVE MODUS----------------------------------
    //---------------------------------------------------------------------------
    //---------------------------------------------------------------------------
    private boolean createTeamSuccess = false;
    private boolean joinedTeamSuccess = false;
    private ArrayList<Pair<Integer, String>> availableTeams = new ArrayList<Pair<Integer, String>>();
    private ArrayList<String> teamNames = new ArrayList<String>();
    private boolean joinedMatchmaking = false;

    private int selectedTeam = 0;

    /*
    Match Status:
        0 not started
        1 running
        2 finished
     */
    private int matchStatus = 0;
    private int teamScore = 0;
    private int competitorScore = 0;
    private int endTeamScore = 0;
    private int endCompetitorScore = 0;
    private int timer = 0;
    private final int mapSize = 20;
    private int[][] fields = new int[mapSize][mapSize];//y,x
    private LatLng center;

    private void loop() {
        if (inTeam) {
            searchNames();
        }
        if (match) {
            switch (matchStatus) {
                case 0: // match not started
                    update(mapping(currentPos)[1], mapping(currentPos)[0]);
                    break;
                case 1: // match running
                    update(mapping(currentPos)[1], mapping(currentPos)[0]);
                    mMap.clear();
                    for (int y = 0; y < mapSize; ++y) {
                        for (int x = 0; x < mapSize; ++x) {
                            drawTile(mapping(y, x), fields[y][x]);
                        }
                    }
                    score_txt.setText("Your score: " + teamScore + "; Enemy score: " + competitorScore + "; Time: " + timer);
                    break;

                case 2: // match ended
                    end();
                    score_txt.setVisibility(View.INVISIBLE);
                    inTeam = false;
                    break;
                case 3: // received data from end()
                    matchStatus = 0;
                    match = false;
                    String message = "You won!";
                    if (endTeamScore < endCompetitorScore) {
                        message = "You lost!";
                    } else if (endTeamScore == endCompetitorScore) {
                        message = "It's a tie!";
                    }
                    String calculation = "Base Score + Friendship Bonus = Total Score";
                    String team = "Your score: " + teamScore + " + " + (endTeamScore-teamScore) + " = " + endTeamScore;
                    String competitor = "Enemy score: " + competitorScore + " + " + (endCompetitorScore-competitorScore) + " = " + endCompetitorScore;
                    new AlertDialog.Builder(this)
                            .setTitle(message)
                            .setMessage(calculation + "\n" + team + "\n" + competitor)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    mMap.clear();
                                    host_button.setVisibility(View.VISIBLE);
                                    search_button.setVisibility(View.VISIBLE);
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    break;
                default: // error
                    break;
            }
        } else {
            if (createTeamSuccess) {
                createTeamSuccess = false;
                inTeam = true;
                center_button.setVisibility(View.INVISIBLE);
                names_txt.setVisibility(View.VISIBLE);
                match_button.setVisibility(View.VISIBLE);
            } else if (joinedMatchmaking) {
                joinedMatchmaking = false;
                match = true;
                match_button.setVisibility(View.INVISIBLE);
                score_txt.setVisibility(View.VISIBLE);
            } else if (joinedTeamSuccess) {
                joinedTeamSuccess = false;
                match = true;
                inTeam = true;
                availableTeams.clear();
                join_button.setVisibility(View.INVISIBLE);
                team_button.setVisibility(View.INVISIBLE);
                names_txt.setVisibility(View.VISIBLE);
                score_txt.setVisibility(View.VISIBLE);
            } else if (!availableTeams.isEmpty()) {
                host_button.setVisibility(View.INVISIBLE);
                search_button.setVisibility(View.INVISIBLE);
                join_button.setVisibility(View.VISIBLE);
                team_button.setVisibility(View.VISIBLE);
                team_button.setText(availableTeams.get(selectedTeam).second);
            }
        }
    }

    private int[] mapping(LatLng realCoords) {
        double latitude = realCoords.latitude - center.latitude;
        double longitude = realCoords.longitude - center.longitude;
        latitude = latitude / LAT_GRID;
        longitude = longitude / LNG_GRID;
        latitude += (double) mapSize / 2.0D;
        longitude += (double) mapSize / 2.0D;
        int y = (int) Math.round(latitude);
        int x = (int) Math.round(longitude);
        if (y >= mapSize || x >= mapSize || y < 0 || x < 0) {
            System.out.println("!d mapping error: out of bounds. mapped to 0,0 instead");
            y = 0;
            x = 0;
        }
        int[] virtualCoords = {y, x};
        return virtualCoords;
    }

    private LatLng mapping(int virtualY, int virtualX) {
        double latitude = (double) virtualY - ((double) mapSize / 2.0D);
        double longitude = (double) virtualX - ((double) mapSize / 2.0D);
        latitude *= LAT_GRID;
        longitude *= LNG_GRID;
        latitude += center.latitude;
        longitude += center.longitude;
        LatLng realCoords = new LatLng(latitude, longitude);
        return realCoords;
    }

    private void createTeam(double midLat, double midLon) {
        Function<JSONObject, Void> fun = jIn -> {
            try {
                if (jIn.getString("status").equals("ok")) {
                    createTeamSuccess = true;
                    System.out.println("!d team has been created");
                } else {
                    System.out.println("!d failed to create team. reason: " + jIn.getString("reason"));
                }
            } catch (Exception e) {
                System.out.println("!d " + e.getMessage());
            }

            return null;
        };
        serverCom.request("/competitive/create/" + midLat + "," + midLon, Request.Method.GET, null, fun);
    }

    private void joinTeam(int creatorId) {
        Function<JSONObject, Void> fun = jIn -> {
            try {
                if (jIn.getString("status").equals("ok")) {
                    joinedTeamSuccess = true;
                    center = new LatLng(jIn.getDouble("mid_lat"), jIn.getDouble("mid_lon"));

                    System.out.println("!d joined team");
                } else {
                    System.out.println("!d failed to join team");
                }
            } catch (Exception e) {
                System.out.println("!d " + e.getMessage());
            }
            return null;
        };
        serverCom.request("/competitive/join/" + creatorId, Request.Method.GET, null, fun);
    }

    private void searchTeam() {
        Function<JSONObject, Void> fun = jIn -> {
            try {
                JSONArray jA = jIn.getJSONArray("teams");
                JSONObject j;
                for (int i = 0; i < jA.length(); i++) {
                    j = jA.getJSONObject(i);
                    availableTeams.add(new Pair(j.getInt("id"), j.getString("name")));
                }

            } catch (Exception e) {
                System.out.println("!d " + e.getMessage());
            }
            return null;
        };
        serverCom.request("/competitive/search", Request.Method.GET, null, fun);
    }

    private void searchNames() {
        Function<JSONObject, Void> fun = jIn -> {
            teamNames.clear();
            try {
                JSONArray jA = jIn.getJSONArray("team");
                JSONObject j;
                for (int i = 0; i < jA.length(); i++) {
                    j = jA.getJSONObject(i);
                    teamNames.add(j.getString("name"));
                }
                // add teamnames to view
                for (int i = 0; i < teamNames.size(); ++i) {
                    nameViews[i].setVisibility(View.VISIBLE);
                    String name = teamNames.get(i);
                    nameViews[i].setText(name);
                }
                for (int i = teamNames.size(); i < nameViews.length; ++i) {
                    nameViews[i].setVisibility(View.INVISIBLE);
                }
                //_______-

            } catch (Exception e) {
                System.out.println("!d " + e.getMessage());
            }
            return null;
        };
        serverCom.request("/competitive/team", Request.Method.GET, null, fun);
    }

    //@app.route("/competitive/start",methods=["GET"])
    //can only be called by teamleader
    //after call it is not possible to join team
    private void matchmaking() {
        Function<JSONObject, Void> fun = jIn -> {
            try {
                if (jIn.getString("status").equals("ok")) {
                    joinedMatchmaking = true;
                    System.out.println("!d started matchmaking");
                } else {
                    System.out.println("!d failed to start matchmaking");
                }
            } catch (Exception e) {
                System.out.println("!d " + e.getMessage());
            }

            return null;
        };
        serverCom.request("/competitive/start", Request.Method.GET, null, fun);
    }

    //send virtual coordinates and receive game data
    //virtual coordinatex: x [0, fieldsize]  y = [0, fieldsize]
    //idee: mittelpunkt des Felds zum mappen der echten auf die virtuellen coordinaten verwenden
    //check ob der punkt im kreis is (einfache kreisformel)

    private void update(int x, int y) {
        Function<JSONObject, Void> fun = jIn -> {
            try {
                matchStatus = jIn.getInt("match_status");
                teamScore = jIn.getInt("score_own_team");
                competitorScore = jIn.getInt("score_competitor");
                timer = jIn.getInt("timer");

                JSONArray f = jIn.getJSONArray("fields");
                JSONObject j;
                for (int i = 0; i < f.length(); i++) {
                    j = f.getJSONObject(i);
                    fields[j.getInt("y")][j.getInt("x")] = j.getInt("val");
                }

            } catch (Exception e) {
                System.out.println("!d " + e.getMessage());
            }
            return null;
        };
        serverCom.request("/competitive/update/" + x + "," + y, Request.Method.GET, null, fun);
    }

    // finaly call end
    // removes player from team
    private void end() {
        Function<JSONObject, Void> fun = jIn -> {
            try {
                matchStatus = jIn.getInt("match_status");
                endTeamScore = jIn.getInt("score_own_team");
                endCompetitorScore = jIn.getInt("score_competitor");
                timer = jIn.getInt("timer");

                JSONArray f = jIn.getJSONArray("fields");
                JSONObject j;
                for (int i = 0; i < f.length(); i++) {
                    j = f.getJSONObject(i);
                    fields[j.getInt("y")][j.getInt("x")] = j.getInt("val");
                }
                matchStatus = 3;
            } catch (Exception e) {
                System.out.println("!d " + e.getMessage());
            }
            return null;
        };
        serverCom.request("/competitive/end", Request.Method.GET, null, fun);
    }

}




