package com.example.melion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.example.melion.data.PictureBase64;
import com.example.melion.data.PictureBase64Geo;
import com.example.melion.data.PictureMetaPreview;
import com.example.melion.data.ServerCom;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.function.Function;

public class leaderboards_alt extends AppCompatActivity {

    private ServerCom serverCom;
    private FragmentManager fragManager;
    private ArrayList<View> ViewList;
    private ArrayList<PictureBase64Geo> base64Pictures;
    private ArrayList<PictureMetaPreview>  pictureMetaList;
    private LinearLayout pictureview;
    private int[] idList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboards_alt);

        Intent intent = this.getIntent();
        serverCom = new ServerCom(this);
        fragManager = getSupportFragmentManager();

        configureBackButton();
        ViewList = new ArrayList<>();
        base64Pictures = new ArrayList<>();
        pictureMetaList = new ArrayList<>();


        listPictureMetasGlobal();


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

    private void listPictureMetasGlobal() {
        Function<JSONObject, Void> fun = jIn -> {
            try {

                JSONArray jA = jIn.getJSONArray("pictures");
                JSONObject j;
                for (int i = 0; i < jA.length(); i++) {
                    j = jA.getJSONObject(i);
                    pictureMetaList.add(new PictureMetaPreview(j.getInt("id"), j.getInt("creator"), j.getString("name"), j.getString("description"),
                            j.getDouble("rating"), j.getInt("num_ratings"), j.getInt("profile")));
                }
                System.out.println("!d got List of PictureMetas");

                fillInTop10();
                System.out.println("!d after");

                for (int i = 0; i < pictureMetaList.size(); i++) {
                    getPicture(pictureMetaList.get(i).id);
                }
            } catch (Exception e) {
                System.out.println("!d " + e.getMessage());
            }
            return null;
        };
        serverCom.request("picture/list/global", Request.Method.GET, null, fun);

    }

        private void fillInTop10() {

            Collections.sort(pictureMetaList,Collections.reverseOrder());

            pictureview = findViewById(R.id.view_picture_imagespace);
            LayoutInflater inflater = LayoutInflater.from(this);

            LinkedList<ImageButton> buttons = new LinkedList<>();

            for(int i = 0; i < 10; i++){
                View view = inflater.inflate(R.layout.activity_view_picture_item, pictureview, false);
                TextView textView = view.findViewById(R.id.picture_view_points);
                textView.setText(Integer.toString(pictureMetaList.get(i).id));
                ImageButton a = view.findViewById(R.id.picture_view_button);
                Button b = view.findViewById(R.id.view_picture_profilepic_button);

                a.setVisibility(View.GONE);
                b.setVisibility(View.GONE);

                ViewList.add(view);
                pictureview.addView(view);
            }

            View view = inflater.inflate(R.layout.activity_empty, pictureview, false);
            ViewList.add(view);
            pictureview.addView(view);


        }


    //adds pictures to the end of base64Pictures
    private void getPicture(int pictureId){
        Function<JSONObject, Void> fun = jIn -> {
            try {
                PictureBase64Geo p = new PictureBase64Geo();
                p.id = pictureId;
                try{
                    p.creatorId =jIn.getInt("creator");
                }catch(Exception e){
                    System.out.println("!d a" + e.getMessage());
                }
                try{
                    p.pictureName = jIn.getString("name");
                }catch(Exception e){
                    System.out.println("!d b" + e.getMessage());
                }
                try{
                    p.description = jIn.getString("description");

                }catch(Exception e){
                    System.out.println("!d c" + e.getMessage());
                }
                try{
                    p.rating = jIn.getDouble("rating");

                }catch(Exception e){
                    System.out.println("!d d" + e.getMessage());
                }
                try{
                    p.numberOfRatings=jIn.getInt("num_ratings");
                }catch(Exception e){
                    System.out.println("!d e" + e.getMessage());
                }
                try{
                    p.base64Data = jIn.getString("image");
                }catch(Exception e){
                    System.out.println("!d f" + e.getMessage());
                }
                try{
                    ((PictureBase64Geo) p).latitude =  jIn.getDouble("latitude");

                }catch(Exception e){
                    System.out.println("!d g" + e.getMessage());
                }
                try{
                    ((PictureBase64Geo) p).longitude =jIn.getDouble("longitude");

                }catch(Exception e){
                    System.out.println("!d h" + e.getMessage());
                }
                try{
                    ((PictureBase64Geo) p).time =  jIn.getInt("time");
                }catch(Exception e){
                    System.out.println("!d i" + e.getMessage());
                }
                try{
                    ((PictureBase64Geo) p).challengeId = jIn.getInt("challenge_id");
                }catch(Exception e){
                    System.out.println("!d j" + e.getMessage());
                }
                base64Pictures.add(p);
                /*base64Pictures.add(new PictureBase64Geo(pictureId, jIn.getInt("creator"), jIn.getString("name"), jIn.getString("description"),
                        jIn.getDouble("rating"), jIn.getInt("num_ratings"), jIn.getString("image"), jIn.getDouble("latitude"), jIn.getDouble("longitude"),
                        jIn.getInt("time"), jIn.getInt("challenge_id")));*/
                System.out.println("!d got picture with id: "+pictureId);
                updateViewInformation(p);
            } catch (Exception e){
                System.out.println("!d " + e.getMessage());
            }
            return null;
        };

        serverCom.request("picture/" + pictureId, Request.Method.GET, null, fun);
    }

    private void updateViewInformation(PictureBase64Geo pic) {   //once a picture is received, this updates the actual information
        int pos = -1;
        for(int i = 0; i < 10; i++){
            if(pictureMetaList.get(i).id == pic.id){
                pos = i;
                break;
            }
        }
        if(pos == -1 )System.out.println("!d a picture has not found its id");

        View view = ViewList.get(pos);
        ImageView image = view.findViewById(R.id.picture_view_image);
        try {
            byte[] decodedString = Base64.decode(pic.base64Data, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            image.setImageBitmap(decodedByte);
        }catch(Exception e){    //in case of bad64 error, try to clean
            try{
                System.out.println("!failed to decode Base64, trying again");
                String cleaned = cleanbase64(pic.base64Data);
                byte[] decodedString = Base64.decode(cleaned, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                image.setImageBitmap(decodedByte);
            }catch(Exception e2){
                System.out.println("!d " + e.getMessage());
            }
        }

        TextView textView = view.findViewById(R.id.picture_view_points);
        textView.setText(Integer.toString(pic.numberOfRatings));

        ImageButton newButton = view.findViewById(R.id.picture_view_button);
        newButton.setVisibility(View.GONE);



    }


    public String cleanbase64(String base64Data) {
        String[] split = base64Data.split("b'",2);

        return split[1];
    }

}
