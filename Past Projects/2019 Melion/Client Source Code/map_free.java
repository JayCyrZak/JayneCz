package com.example.melion;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.example.melion.data.ServerCom;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;


import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;

import com.example.melion.data.Tile;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.function.Function;

import static android.graphics.Color.parseColor;


public class map_free extends FragmentActivity implements OnMapReadyCallback {
    private ServerCom serverCom;
    EditText nameEdit;
    EditText descriptionEdit;
    Button submitButton;
    Button soloButton;
    Button friendsButton;
    Button globalButton;
    Button screenshotButton;
    Button drawButton;
    Button brushButton;
    SeekBar sizeBar;
    SeekBar redBar;
    SeekBar greenBar;
    SeekBar blueBar;
    ListView nearbyList;

    private GoogleMap mMap;
    private ArrayList<Tile> tiles;
    private String type;

    private int buffer = 0;
    private TreeSet<Integer> nearbyIds;
    private TreeSet<String> nearbyNames;

    private LatLng currentPos;
    private FusedLocationProviderClient fusedLocationClient;

    private static float DEFAULT_ZOOM = 18;

    String base64Data;

    private boolean isRunning = true;

    private boolean isDrawing = false;
    private boolean brushMenuIsOpen = false;
    private int size = 5; // radius in meters
    private int red, green, blue;

    private achievements achiev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_free);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        configureBackButton();
        type = RequestType.solo;
        tiles = new ArrayList<>();
        nearbyIds = new TreeSet<>();
        nearbyNames = new TreeSet<>();
        serverCom = new ServerCom(this);

        nearbyList = (ListView) findViewById(R.id.nearbyList);

        nameEdit = findViewById(R.id.nameEdit);
        descriptionEdit = findViewById(R.id.descriptionEdit);

        submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sendPicture(nameEdit.getText().toString(), descriptionEdit.getText().toString(), base64Data);
                    screenshotButton.setVisibility(View.VISIBLE);
                    submitButton.setVisibility(View.INVISIBLE);
                    nameEdit.setVisibility(View.INVISIBLE);
                    descriptionEdit.setVisibility(View.INVISIBLE);
                } catch (Exception e) {
                    System.out.println("!d " + e.getMessage());
                }
            }
        });
        achiev = achievements.getInstance();

        screenshotButton = (Button) findViewById(R.id.screenshotButton);
        screenshotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    captureScreen();
                } catch (Exception e) {
                    System.out.println("!d " + e.getMessage());
                }
            }
        });

        soloButton = (Button) findViewById(R.id.soloButton);
        soloButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    type = RequestType.solo;
                    tiles.clear();
                    mMap.clear();
                } catch (Exception e) {
                    System.out.println("!d " + e.getMessage());
                }
            }
        });

        friendsButton = (Button) findViewById(R.id.friendsButton);
        friendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    type = RequestType.friends;
                    tiles.clear();
                    mMap.clear();
                } catch (Exception e) {
                    System.out.println("!d " + e.getMessage());
                }
            }
        });

        globalButton = (Button) findViewById(R.id.globalButton);
        globalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    type = RequestType.global;
                    tiles.clear();
                    mMap.clear();
                } catch (Exception e) {
                    System.out.println("!d " + e.getMessage());
                }
            }
        });

        drawButton = (Button) findViewById(R.id.drawButton);
        drawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    isDrawing = !isDrawing;
                    if (isDrawing) {
                        drawButton.setText("Draw? Yes");
                    } else {
                        drawButton.setText("Draw? No");
                    }
                } catch (Exception e) {
                    System.out.println("!d " + e.getMessage());
                }
            }
        });

        brushButton = (Button) findViewById(R.id.brushButton);
        brushButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    brushMenuIsOpen = !brushMenuIsOpen;
                    if (brushMenuIsOpen) {
                        sizeBar.setVisibility(View.VISIBLE);
                        redBar.setVisibility(View.VISIBLE);
                        greenBar.setVisibility(View.VISIBLE);
                        blueBar.setVisibility(View.VISIBLE);
                    } else {
                        sizeBar.setVisibility(View.INVISIBLE);
                        redBar.setVisibility(View.INVISIBLE);
                        greenBar.setVisibility(View.INVISIBLE);
                        blueBar.setVisibility(View.INVISIBLE);
                    }
                } catch (Exception e) {
                    System.out.println("!d " + e.getMessage());
                }
            }
        });

        sizeBar = (SeekBar) findViewById(R.id.sizeBar);
        sizeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                size = seekBar.getProgress();
            }
        });

        redBar = (SeekBar) findViewById(R.id.redBar);
        redBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                red = seekBar.getProgress();
            }
        });

        greenBar = (SeekBar) findViewById(R.id.greenBar);
        greenBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                green = seekBar.getProgress();
            }
        });

        blueBar = (SeekBar) findViewById(R.id.blueBar);
        blueBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                blue = seekBar.getProgress();
            }
        });
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
                if (isRunning) {
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
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(currentPos));

                if (isDrawing) {
                    String color = "FF" + String.format("%1$02X", red) + String.format("%1$02X", green) + String.format("%1$02X", blue);
                    //String color = Integer.toHexString(red) + Integer.toHexString(green) + Integer.toHexString(blue);
                    Tile newTile = new Tile(location.getLatitude(), location.getLongitude(), color, size);
                    sendPosition(newTile);
                }
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
                isRunning = false;
                finish();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            isRunning = false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void loop() {
        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        double angle = Math.abs(bounds.northeast.latitude - bounds.southwest.latitude) + Math.abs(bounds.northeast.longitude - bounds.southwest.longitude);
        requestTiles(tiles, type, bounds.getCenter().latitude, bounds.getCenter().longitude, angle);
        mMap.clear();
        for (Tile tile : tiles) {
            drawTile(tile);
        }
        if (buffer++ > 5) {
            buffer = 0;
            nearbyIds.clear();
            nearbyNames.clear();
            tiles.clear();
        }
        requestNearbyIds();
        for (int i : nearbyIds) {
            requestName(i);
        }
        String[] nearbyNamesArray = new String[nearbyNames.size()];
        nearbyNames.toArray(nearbyNamesArray);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, nearbyNamesArray);
        nearbyList.setAdapter(adapter);
    }

    //Server Communication
    private void sendPosition(Tile tile) {
        JSONObject j = new JSONObject();
        try {
            j.put("latitude", tile.getLatitude());
            j.put("longitude", tile.getLongitude());
            j.put("color", tile.getColor());
            j.put("size", tile.getSize());
        } catch (Exception e) {
            System.out.println("!d " + e.getMessage());
        }

        Function<JSONObject, Void> fun = jIn -> {
            try {
                if (jIn.getString("status").equals("ok")) {
                    System.out.println("!d send position update was successful");
                } else {
                    System.out.println("!d failed to send position: " + jIn.getString("reason"));
                }
            } catch (Exception e) {
                System.out.println("!d " + e.getMessage());
            }

            return null;
        };
        achiev.update(this);
        serverCom.request("update", Request.Method.POST, j, fun);
    }

    interface RequestType {
        String solo = "solo";
        String friends = "friends";
        String global = "global";
    }

    private void requestTiles(ArrayList<Tile> tiles, String type, double latitude, double longitude, double angle) {
        Function<JSONObject, Void> fun = jIn -> {
            try {
                JSONArray jA = jIn.getJSONArray("tiles");
                JSONObject j;
                for (int i = 0; i < jA.length(); i++) {
                    j = jA.getJSONObject(i);
                    Tile newTile = new Tile(j.getDouble("latitude"), j.getDouble("longitude"), j.getString("color"), j.getInt("size"));
                    boolean isDuplicate = false;
                    for (Tile tile : tiles) {
                        if (tile.equals(newTile)) {
                            isDuplicate = true;
                            break;
                        }
                    }
                    if (!isDuplicate) {
                        tiles.add(newTile);
                    }
                }

            } catch (Exception e) {
                System.out.println("!d " + e.getMessage());
            }
            return null;
        };
        serverCom.request("map/" + type + "/" + latitude + "," + longitude + ";" + angle, Request.Method.GET, null, fun);
    }

    private void sendPicture(String name, String description, String base64Data) {
        Function<JSONObject, Void> fun = jIn -> {
            try {
                if (jIn.getString("status").equals("ok")) {
                    System.out.println("!d picture was submitted");
                } else {
                    System.out.println("!d failed to submit");
                }
            } catch (Exception e) {
                System.out.println("!d " + e.getMessage());
            }

            return null;
        };
        JSONObject j = new JSONObject();
        try {
            j.put("name", name);
            j.put("latitude", 0.01);
            j.put("longitude", 0.01);
            j.put("description", description);
            j.put("profile", false);
            j.put("image", base64Data);
        } catch (Exception e) {
            System.out.println("!d " + e.getMessage());
        }

        serverCom.request("picture/submit/", Request.Method.POST, j, fun);
    }

    private void requestNearbyIds() {
        Function<JSONObject, Void> fun = jIn -> {
            try {
                //fill nearbyIds list
                JSONArray jA = jIn.getJSONArray("painting-with");
                for (int i = 0; i < jA.length(); i++) {
                    nearbyIds.add(jA.getInt(i));
                }
            } catch (Exception e) {
                System.out.println("!d " + e.getMessage());
            }
            return null;
        };
        JSONObject j = new JSONObject();
        serverCom.request("update", Request.Method.POST, j, fun);
    }

    private void requestName(int userId) {
        Function<JSONObject, Void> fun = jIn -> {
            try {
                if (jIn.getString("status").equals("ok")) {
                    System.out.println("!d profile has been received");
                    JSONObject p = jIn.getJSONObject("profile");
                    try {
                        nearbyNames.add(p.getString("name"));
                    } catch (Exception e) {
                        System.out.println("!d " + e.getMessage());
                    }
                } else {
                    System.out.println("!d failed to get profile");
                }
            } catch (Exception e) {
                System.out.println("!d " + e.getMessage());
            }
            return null;
        };
        if (userId > -1) {
            serverCom.request("profile/" + userId, Request.Method.GET, null, fun);
        }
    }

    private Circle drawTile(Tile tile) {
        int color;
        try {
            color = parseColor("#" + tile.getColor());
        } catch (IllegalArgumentException e) {
            System.out.println("!d color could not be parsed: " + tile.getColor());
            System.out.println("!d using default color");
            color = Color.RED;
        }

        CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(tile.getLatitude(), tile.getLongitude()))
                .radius(tile.getSize()) // in meters
                .strokeWidth(1)
                .strokeColor(color)
                .fillColor(color);
        return mMap.addCircle(circleOptions);
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
    //---------------------------------

    public void captureScreen() {
        SnapshotReadyCallback callback = new SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                base64Data = bitmapToBase64(snapshot);
                submitButton.setVisibility(View.VISIBLE);
                descriptionEdit.setVisibility(View.VISIBLE);
                nameEdit.setVisibility(View.VISIBLE);
                screenshotButton.setVisibility(View.INVISIBLE);
            }
        };
        mMap.snapshot(callback);
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

}




