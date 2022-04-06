package com.example.melion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.example.melion.data.FriendListElement;
import com.example.melion.data.PictureBase64Geo;
import com.example.melion.data.PictureMetaPreview;
import com.example.melion.data.ServerCom;
import com.example.melion.data.UserProfile;
import com.example.melion.data.model.LoggedInUser;

import org.json.JSONObject;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.function.Function;

public class profile extends AppCompatActivity implements friend_Profile.OnFragmentInteractionListener {
    private ServerCom serverCom;
    private TextView status;
    private LoggedInUser user;
    private Button tweetButton;
    private Button friendList;
    private Button changeTeam;
    private ListView listView;
    private LinearLayout gallery;
    private EditText tweetText;
    private EditText profileName;
    private ScrollView profileScrollView;
    private UserProfile userProfile;
    private FragmentManager fragManager ;

    //OnResponse Variables
    private ArrayList<PictureMetaPreview>  pictureMetaList;
    private PictureBase64Geo profileImage;
    private ArrayList<FriendListElement> requestedFriendList;
    private ArrayList<FriendListElement> searchResult;
    private UserProfile requestedProfile;
    private achievements achiev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        ConstraintLayout focus = findViewById(R.id.constraintLayout2);  //prevents focus being shifted to the Edittexts
        focus.requestFocus();

        fragManager = getSupportFragmentManager();
        listView = (ListView)findViewById(R.id.listview);
        gallery = findViewById(R.id.gallery);
        tweetText = (EditText) findViewById(R.id.tweetText);
        profileName = (EditText) findViewById(R.id.profileName);
        tweetButton = (Button)  findViewById(R.id.tweetButton);
        serverCom = new ServerCom(this);


        configureButtons();
        fillListView();

        achiev = achievements.getInstance();
        achiev.update(this);
        fillAchievementView();


        getOwnProfile();
        listOwnPictureMetas();


    }

    private void configureButtons() {
        configureBackButton();

        FrameLayout fragCont = findViewById(R.id.frag_container);
        fragCont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                View view = getCurrentFocus();
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        friendList = (Button) findViewById(R.id.friendList);
        friendList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   //passing UserId and Friendslist to new Friendslist Aktivity
                Bundle friends = new Bundle();
                friends.putSerializable("friends", requestedFriendList);

                Intent intent = new Intent(profile.this, com.example.melion.friendList.class);
                intent.putExtra("id",requestedProfile.id);
                intent.putExtras(friends);
                startActivity(intent);
            }

        });

        changeTeam = (Button) findViewById(R.id.changeTeam);
        changeTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = com.example.melion.changeTeam.newInstance("profile","");
                FragmentTransaction transaction = fragManager.beginTransaction();
                transaction.replace(R.id.frag_container, fragment);
                transaction.commit();
            }
        });

        tweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = profileName.getText().toString();
                String newMessage = tweetText.getText().toString();
                if(!newName.equals(requestedProfile.name)){   //React to name change
                    changeName(newName);
                }
                if(!newMessage.equals(requestedProfile.message)){
                    changeProfileMessage(newMessage);
                }

                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                View view = getCurrentFocus();
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            }
        });

        Button gallery = findViewById(R.id.gallery_button);
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGallery(requestedProfile.id,requestedProfile.id);
            }
        });





    }

    public void setStatus(String msg){
       status.setText(msg);
    }

    public void configureBackButton() {
        Button backButton = (Button) findViewById(R.id.backbutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void fillListView(){    //Fills the Friends-ListView and defines Behaviour

            ArrayList<String> arrayList = new ArrayList<String>();
            if(requestedFriendList != null) {
                for (int i = 0; i < requestedFriendList.size(); i++) {
                    arrayList.add(requestedFriendList.get(i).name);
                }

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { //opens friendProfile fragments on itemclick
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Toast.makeText(profile.this, "Displaying Profile of Friend: " + arrayList.get(position), Toast.LENGTH_SHORT).show();

                        Bundle bundle = new Bundle();
                        bundle.putSerializable("friend", requestedFriendList.get(position));

                        Fragment fragment = friend_Profile.newInstance(bundle, requestedProfile.id);
                        FragmentManager manager = getSupportFragmentManager();
                        FragmentTransaction transaction = manager.beginTransaction();
                        transaction.replace(R.id.frag_container, fragment);
                        transaction.commit();
                    }
                });
            }
            else {
                arrayList.add("No");
                arrayList.add("Friends");
                arrayList.add("found");
                arrayList.add("sorry");
                arrayList.add(":(");
            }
            ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList);
            listView.setAdapter(arrayAdapter);

            listView.setOnTouchListener(new View.OnTouchListener() {    //Disables Parent-Scroll to overwrite Child
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });


    }

    private void fillAchievementView(){   //for now just duplicates the item view and fills the horz slider with them
        achievements achiev = achievements.getInstance();
        LayoutInflater inflater = LayoutInflater.from(this);
        Function<JSONObject, Void> f = getAchievements -> {
                    achievement[] achievementsUnlocked = achiev.getUserUnlockedAchievements();
                    if(achievementsUnlocked !=null){
                        for(int i = 0; i < achievementsUnlocked.length; i++){
                            View view = inflater.inflate(R.layout.activity_item, gallery, false);
                            TextView textView = view.findViewById(R.id.archText);
                            textView.setText(achievementsUnlocked[i].getName());

                            ImageView imageView = view.findViewById(R.id.archImage);
                            switch(achievementsUnlocked[i].getValue()){
                                case 1:
                                    imageView.setImageResource(R.drawable.medal_bronze);
                                    break;
                                case 2:
                                    imageView.setImageResource(R.drawable.medal_silver);
                                    break;
                                case 3:
                                    imageView.setImageResource(R.drawable.medal_gold);
                                    break;
                                default:
                                    imageView.setImageResource(R.drawable.medal_bronze);
                            }

                            gallery.addView(view);
                        }
                    }else{
                        View view = inflater.inflate(R.layout.activity_item, gallery, false);
                        TextView textView = view.findViewById(R.id.archText);
                        textView.setText("You don't have any Achievements unlocked");

                        gallery.addView(view);
                    }
            return null;
        };
        achiev.loadUnlockedAchievements(this.getApplicationContext(),f);
    }


    //ServerCom
    private void changeName(String newName){
        if(!newName.matches("[a-zA-Z0-9\\-_]{1,}")){
            if(newName.length() < 1) showMassage("Name is too short");
            else showMassage("Please use only a-z and A-Z");
            System.out.println("!d name not possible");
            return;
        }
        Function<JSONObject, Void> fun = jIn -> {
            try {
                if (jIn.getString("status").equals("ok")) {
                    System.out.println("!d name has been changed to :" + newName);
                    requestedProfile.name = newName;
                    setProfileName();

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

    private void changeTeam(int newTeamID){
        if(!(newTeamID > 0 && newTeamID < 4)){
            System.out.println("!d team does not exist");
            return;
        }
        Function<JSONObject, Void> fun = jIn -> {
            try {
                if (jIn.getString("status").equals("ok")) {
                    System.out.println("!d team has been changed to :" + newTeamID);
                    requestedProfile.team = newTeamID;
                    setTeamColor();
                } else {
                    System.out.println("!d failed to change name");
                }
            } catch (Exception e){
                System.out.println("!d " + e.getMessage());
            }

            return null;
        };
        serverCom.request("config/team/" + newTeamID, Request.Method.GET, null, fun);

    }

    void getOwnProfile(){
        getProfile(-1);
    }

    //sets Profile variable of class
    private void getProfile(int userId){
        Function<JSONObject, Void> fun = jIn -> {
            try {
                if (jIn.getString("status").equals("ok")) {
                    System.out.println("!d profile has been received");
                    JSONObject p = jIn.getJSONObject("profile");
                    UserProfile f = new UserProfile();
                    try{
                        f.id = p.getInt("id");
                    }catch(Exception e){
                        System.out.println("!d " + e.getMessage());
                    }
                    try{
                        f.name = p.getString("name");
                    }catch(Exception e){
                        System.out.println("!d " + e.getMessage());
                    }
                    try{
                        f.phone = p.getInt("phone");
                    }catch(Exception e){
                        System.out.println("!d " + e.getMessage());
                    }
                    try{
                        f.team = p.getInt("team");
                    }catch(Exception e){
                        System.out.println("!d " + e.getMessage());
                    }
                    try{
                        f.message = p.getString("message");
                    }catch(Exception e){
                        System.out.println("!d " + e.getMessage());
                    }
                    try{
                        f.friendsSince =  p.getInt("since");
                    }catch(Exception e){
                        System.out.println("!d " + e.getMessage());
                    }
                    try{
                        f.numberOfAchievements = p.getInt("num-achievements");
                    }catch(Exception e){
                        System.out.println("!d " + e.getMessage());
                    }
                    try{
                        f.numberOfPoints = p.getInt("num-points");
                    }catch(Exception e){
                        System.out.println("!d " + e.getMessage());
                    }
                    try{
                        f.numberOfFriends = p.getInt("num-friends");
                    }catch(Exception e){
                        System.out.println("!d " + e.getMessage());
                    }
                    try{
                        f.paintedWithFrineds = p.getInt("painted-with-friends");
                    }catch(Exception e){
                        System.out.println("!d " + e.getMessage());
                    }
                    try{
                        f.paintedTogether = p.getInt("painted-together");
                    }catch(Exception e){
                        System.out.println("!d " + e.getMessage());
                    }
                    try{
                        f.relation = p.getInt("relation");
                    }catch(Exception e){
                        System.out.println("!d " + e.getMessage());
                    }
                    try{
                        f.direction = p.getInt("direction");
                    }catch(Exception e){
                        System.out.println("!d " + e.getMessage());
                    }
                    requestedProfile = f;

                    /*requestedProfile = new UserProfile(p.getInt("id"), p.getString("name"), p.getInt("phone"),
                            p.getInt("team"), p.getString("message"), p.getInt("since"), p.getInt("num-achievements"),
                            p.getInt("num-points"), p.getInt("num-friends"), p.getInt("painted-with-friends"),
                            p.getInt("painted-together"), p.getInt("relation"), p.getInt("direction"));*/

                    setProfileInformation();
                    getFriendList(requestedProfile.id);

                } else {
                    System.out.println("!d failed to get profile ");
                }
            } catch (Exception e){
                System.out.println("!d " + e.getMessage());
            }

            return null;
        };
        if(userId > -1){
            serverCom.request("profile/" + userId, Request.Method.GET, null, fun);
        } else {
            serverCom.request("profile", Request.Method.GET, null, fun);
        }

    }

    private void setProfileInformation() {  //set information from the received User
        setProfileName();

        setProfileMessage();

        setTeamColor();

    }

    private void getProfilePicture() { // is used after PictureMetas have been received
        int highID = -1;
        int position = -1;
        for(int i = 0; i < pictureMetaList.size(); i++){    //search for most recent profile choice

            if(pictureMetaList.get(i).isProfilePicture == 1 && pictureMetaList.get(i).id > highID){
                position = i ;
                highID = pictureMetaList.get(i).id;
            }
        }
        if(position != -1){
            System.out.println("!d Profile Picture found");
            getPicture(pictureMetaList.get(position).id);
        }
        System.out.println("!d No Profile Picture found");
    }

    private void setProfilePicture() {
        PictureBase64Geo picture = profileImage;
        ImageView image = findViewById(R.id.profile_Image);
        try {
            byte[] decodedString = Base64.decode(picture.base64Data, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            image.setImageBitmap(decodedByte);
        }catch(Exception e){    //in case of bad64 error, try to clean
            try{
                String cleaned = cleanbase64(picture.base64Data);
                byte[] decodedString = Base64.decode(cleaned, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                image.setImageBitmap(decodedByte);
            }catch(Exception e2){
                System.out.println("!d " + e.getMessage());
            }
        }
    }


    public String cleanbase64(String base64Data) {
        String[] split = base64Data.split("b'",2);

        return split[1];
    }

    private void setProfileName(){
        profileName.setText(requestedProfile.name);
    }

    private void setProfileMessage(){
        tweetText.setText(requestedProfile.message, TextView.BufferType.EDITABLE);
    }

    private void setTeamColor(){

        ImageView teamColor = findViewById(R.id.teamcolor);
        switch(requestedProfile.team){
            case 1:
                teamColor.setColorFilter(getColor(android.R.color.holo_red_dark));
                break;
            case 2:
                teamColor.setColorFilter(getColor(android.R.color.holo_green_dark));
                break;
            case 3:
                teamColor.setColorFilter(getColor(android.R.color.holo_blue_dark));
                break;
        }

    }

    private void getOwnFriendsList(){
        getFriendList(-1);
    }

    void getFriendList(int userId){
        requestedFriendList = new ArrayList<FriendListElement>();
        Function<JSONObject, Void> fun = jIn -> {
            try {
                JSONArray jA = jIn.getJSONArray("friends");
                JSONObject j;
                for(int i = 0; i < jA.length(); i++){
                    j = jA.getJSONObject(i);

                    requestedFriendList.add(new FriendListElement(j.getInt("id"), j.getString("name"),
                            j.getInt("phone"), j.getInt("team"), j.getString("message"),
                            j.getInt("since"), j.getInt("painted-together"), j.getInt("relation"),
                            j.getInt("direction")));

                }

                fillListView();



            } catch (Exception e){
                System.out.println("!d " + e.getMessage());
            }
            return null;
        };
        if(userId > -1){
            serverCom.request("friends/list/" + userId, Request.Method.GET, null, fun);
        } else {
            serverCom.request("friends/list", Request.Method.GET, null, fun);
        }

    }

    private void searchByName(String userName){
        Function<JSONObject, Void> fun = jIn -> {
            try {
                JSONArray jA = jIn.getJSONArray("friends");
                JSONObject j;
                for(int i = 0; i < jA.length(); i++){
                    j = jA.getJSONObject(i);
                    searchResult.add(new FriendListElement(j.getInt("id"), j.getString("name"),
                            j.getInt("phone"), j.getInt("team"), j.getString("message"),
                            j.getInt("since"), j.getInt("painted-together"), j.getInt("relation"),
                            j.getInt("direction")));
            }

            } catch (Exception e){
                System.out.println("!d " + e.getMessage());
            }
            return null;
        };
        serverCom.request("search/name/" + userName, Request.Method.GET, null, fun);
    }



    public void addNewFriend(int userID){
        Function<JSONObject, Void> fun = jIn -> {
            try {
                if (jIn.getString("status").equals("ok")) {
                    System.out.println("!d friend has been added :" + userID);
                } else {
                    System.out.println("!d failed to add friend");
                }
            } catch (Exception e){
                System.out.println("!d " + e.getMessage());
            }

            return null;
        };
        serverCom.request("friends/add/" + userID, Request.Method.GET, null, fun);
    }

    private void acceptNewFriend(int userID){
        Function<JSONObject, Void> fun = jIn -> {
            try {
                if (jIn.getString("status").equals("ok")) {
                    System.out.println("!d friend has been accepted :" + userID);
                } else {
                    System.out.println("!d failed to accept friend");
                }
            } catch (Exception e){
                System.out.println("!d " + e.getMessage());
            }

            return null;
        };
        serverCom.request("friends/accept/" + userID, Request.Method.GET, null, fun);
    }

    private void removeFriend(int userID){
        Function<JSONObject, Void> fun = jIn -> {
            try {
                if (jIn.getString("status").equals("ok")) {
                    System.out.println("!d friend has been removed :" + userID);
                } else {
                    System.out.println("!d failed to remove friend");
                }
            } catch (Exception e){
                System.out.println("!d " + e.getMessage());
            }

            return null;
        };
        serverCom.request("friends/remove/" + userID, Request.Method.GET, null, fun);
    }

    private void changeProfileMessage(String newMessage){
        Function<JSONObject, Void> fun = jIn -> {
            try {
                if (jIn.getString("status").equals("ok")) {
                    System.out.println("!d message has been set :" + newMessage);
                    requestedProfile.message = newMessage;
                    setProfileMessage();

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

   public void setchangeTeam(int newTeam){
        if(newTeam == requestedProfile.team){

        }
        else{
            changeTeam(newTeam);
        }
    }

    public void startGallery(int UserId , int FriendId ){
        Intent intent = new Intent(profile.this, com.example.melion.view_picture.class);
        intent.putExtra("Id", UserId);
        intent.putExtra("FriendId", FriendId);
        startActivity(intent);
    }

    private void listOwnPictureMetas(){
        listPictureMetas(-1);
    }


    private void listPictureMetas(int userId){
        pictureMetaList  = new ArrayList<>();
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

                getProfilePicture();

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
                profileImage = p;
                System.out.println("!d got picture with id: "+pictureId);
                setProfilePicture();
            } catch (Exception e){
                System.out.println("!d " + e.getMessage());
            }
            return null;
        };

        serverCom.request("picture/" + pictureId, Request.Method.GET, null, fun);
    }

    protected void showMassage(String Massage) {
        Toast.makeText(this, Massage, Toast.LENGTH_SHORT).show();
    }




    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}