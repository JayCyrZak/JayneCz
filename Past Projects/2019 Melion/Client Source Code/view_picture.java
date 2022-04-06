package com.example.melion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.example.melion.data.Friend;
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

public class view_picture extends AppCompatActivity {
    private ServerCom serverCom;
    private LinearLayout pictureview;
    private ArrayList<Integer> removeFromMetaList;

    private int UserId; // Id of AppUser
    private int GalleryId;  //Id of Galleryowner
    private int picture_counter = 0;
    private FragmentManager fragManager;
    private ArrayList<View> ViewList;
    private ArrayList<PictureBase64Geo> base64Pictures;
    private ArrayList<PictureMetaPreview>  pictureMetaList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_picture);
        Intent intent = this.getIntent();
        UserId = intent.getIntExtra("Id",-1);
        GalleryId = intent.getIntExtra("FriendId", -1);
        serverCom = new ServerCom(this);
        fragManager = getSupportFragmentManager();

        configureBackButton();
        ViewList = new ArrayList<>();
        base64Pictures = new ArrayList<>();
        pictureMetaList = new ArrayList<>();

        Button globalgallery = findViewById(R.id.view_picture_global);
        globalgallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view_picture.this, leaderboards_alt.class));
            }
        });

        //configureSpinner();
        listPictureMetas(GalleryId);





    }

   /* private void configureSpinner() {     //doesn't exist anymore


        String [] spinnerList = {"POINTS","TEAM","FRIENDS"};

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, spinnerList);
        Spinner spinner = findViewById(R.id.view_picture_spinner);
        spinner.setAdapter(arrayAdapter);
    }*/

    private void fillInItems() {

        pictureview = findViewById(R.id.view_picture_imagespace);

        LayoutInflater inflater = LayoutInflater.from(this);

        LinkedList<ImageButton> buttons = new LinkedList<>();

        for(int i = 0; i < pictureMetaList.size(); i++){
            View view = inflater.inflate(R.layout.activity_view_picture_item, pictureview, false);
            TextView textView = view.findViewById(R.id.picture_view_points);
            textView.setText("0");

            ViewList.add(view);
            pictureview.addView(view);
        }

        View view = inflater.inflate(R.layout.activity_empty, pictureview, false);
        ViewList.add(view);
        pictureview.addView(view);


    }


    public String cleanbase64(String base64Data) {
        String[] split = base64Data.split("b'",2);

        return split[1];
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

    private void updateViewInformation(PictureBase64Geo pic) {   //once a picture is received, this updates the actual information and activates the buttons
        int pos = -1;
        for(int i = 0; i < pictureMetaList.size(); i++){
            if(pictureMetaList.get(i).id == pic.id){
                pos = i;
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

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle fragBundle = new Bundle();
                fragBundle.putSerializable("picture", pic);

                Fragment frag = bigPicture.newInstance(fragBundle,"");
                fragManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragManager.beginTransaction();
                transaction.add(R.id.frag_container, frag);
                transaction.commit();
            }
        });

        TextView textView = view.findViewById(R.id.picture_view_points);

        textView.setText(Integer.toString(pic.numberOfRatings));

        ImageButton newButton = view.findViewById(R.id.picture_view_button);
        Button setAsProfile = view.findViewById(R.id.view_picture_profilepic_button);


        if(UserId == GalleryId){  // AppUser not allowed to rate his own picture, but set his pictures as profilepicture
            newButton.setVisibility(View.GONE);
            setAsProfile.setVisibility(View.VISIBLE);
            setAsProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setPictureAsProfilePicture(pic);
                }
            });

        }
        else{
            setAsProfile.setVisibility(View.GONE);
            newButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String s = textView.getText().toString();
                    int i = Integer.parseInt(s);
                    int friendshiplvl = 1;
                    ratePicture(pic.id, friendshiplvl);
                    textView.setText(Integer.toString(i+friendshiplvl));
                    showMassage("You rated a picture!! Here take some points");
                    newButton.setVisibility(View.GONE);
                }
            });
        }

    }

    private void setPictureAsProfilePicture(PictureBase64Geo pic) {
        re_submitPicture(pic);
    }


    private void listOwnPictureMetas(){
        listPictureMetas(-1);
    }


    private void listPictureMetas(int userId){
        Function<JSONObject, Void> fun = jIn -> {
            try {

                JSONArray jA = jIn.getJSONArray("pictures");
                JSONObject j;
                for(int i = 0; i < jA.length(); i++){
                    j = jA.getJSONObject(i);
                    pictureMetaList.add(new PictureMetaPreview(j.getInt("id"), userId, j.getString("name"), j.getString("description"),
                            j.getDouble("rating"), j.getInt("num_ratings"), j.getInt("profile")));
                }
                System.out.println("!d got List of PictureMetas");
                cleanPictureMetaList();
                fillInItems();
                for(int i = 0; i < pictureMetaList.size(); i++){
                    getPicture(pictureMetaList.get(i).id);
                }
            } catch (Exception e){
                System.out.println("!d " + e.getMessage());
            }
            return null;
        };
        if(userId > -1){
            serverCom.request("picture/list/" + userId, Request.Method.GET, null, fun);
        } else {
            serverCom.request("picture/list/", Request.Method.GET, null, fun);
        }




    }

    private void cleanPictureMetaList() {   //searched pictureMetaList for duplicates of pictures and removes them
        removeFromMetaList = new ArrayList<Integer>();
        for(PictureMetaPreview p : pictureMetaList){
            if(p.isProfilePicture == 1 && decodeDescription(p.description) != -1){
                int decodedId = decodeDescription(p.description);
                System.out.println("!d "+decodedId);
                if(decodedId == -1){
                    continue; //no need to delete picture
                }
                for(int i = 0 ; i < pictureMetaList.size(); i++){
                    if(pictureMetaList.get(i).id == decodedId){
                        searchfurther(i);   // in case of profile picture chains
                        removeFromMetaList.add(i);
                    }
                }
            }
        }
        Collections.sort(removeFromMetaList, Collections.reverseOrder());
        for(int i = 0; i < removeFromMetaList.size();i++){
            int index = removeFromMetaList.get(i);
            pictureMetaList.remove(index);
        }
    }

    private void searchfurther(int i){
        int decodedId = decodeDescription(pictureMetaList.get(i).description);
        if(decodedId == -1){
            return;
        }
        for(int j = 0; j < pictureMetaList.size(); j++){
            if(pictureMetaList.get(i).id == decodedId){
                searchfurther(j);
                removeFromMetaList.add(j);
            }
        }
    }

    private int decodeDescription(String description) {
        if(description.startsWith("#")){
            System.out.println("!d found doctored Description");
            String[] split = description.split("#",4);

            try{
                int DecodedId  = Integer.parseInt(split[1]);
                return DecodedId;
            }catch(Exception e){
                System.out.println("!d "+ e);
            }
        }
        return -1;
    }

    private void re_submitPicture(PictureBase64Geo pic){
        Function<JSONObject, Void> fun = jIn -> {
            try {
                if (jIn.getString("status").equals("ok")) {
                    System.out.println("!d picture was submittet");

                } else {
                    System.out.println("!d failed to submit");
                }
            } catch (Exception e){
                System.out.println("!d " + e.getMessage());
            }

            return null;
        };
        JSONObject j = new JSONObject();
        try {

            String newDesc = ("#"+pic.id+"#")+pic.description;

            j.put("name", pic.pictureName);
            j.put("latitude",0.01);
            j.put("longitude",0.01);
            j.put( "description",newDesc);
            j.put("profile",true);
            j.put("image",cleanbase64(pic.base64Data));
        } catch (Exception e) {
            System.out.println("!d " + e.getMessage());
        }
        serverCom.request("picture/submit/", Request.Method.POST, j, fun);

    }

    private void ratePicture(int pictureId, int rating) {
        Function<JSONObject, Void> fun = jIn -> {
            try {
                if (jIn.getString("status").equals("ok")) {
                    System.out.println("!d picture was rated");
                    showMassage("You rated a picture!! I'm sure your friend is thankful");
                } else {
                    System.out.println("!d failed to rate");
                    showMassage("I think you already rated that picture");
                }
            } catch (Exception e) {
                System.out.println("!d " + e.getMessage());
            }
            return null;
        };
        serverCom.request("picture/rate/" + pictureId +"/"+ rating, Request.Method.GET, null, fun);

    }

    protected void showMassage(String Massage) {
        Toast.makeText(this, Massage, Toast.LENGTH_SHORT).show();
    }







}
