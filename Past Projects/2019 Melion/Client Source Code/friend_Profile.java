package com.example.melion;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.example.melion.data.FriendListElement;
import com.example.melion.data.PictureBase64Geo;
import com.example.melion.data.PictureMetaPreview;
import com.example.melion.data.ServerCom;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.function.Function;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link friend_Profile.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link friend_Profile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class friend_Profile extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private Bundle mParam1;     //bundel of information for the current firendprofile
    private int mParam2;        //Id of the user of the app


    private View view;
    private ArrayList<PictureMetaPreview>  pictureMetaList;
    FriendListElement user;
    private PictureBase64Geo profileImage;
    private ServerCom serverCom;
    private ArrayList<FriendListElement> requestedFriendList;
    private OnFragmentInteractionListener mListener;

    public friend_Profile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment friend_Profile.
     */
    // TODO: Rename and change types and number of parameters
    public static friend_Profile newInstance(Bundle param1, int param2) {
        friend_Profile fragment = new friend_Profile();
        Bundle args = new Bundle();
        args.putBundle(ARG_PARAM1, param1);
        args.putInt(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getBundle(ARG_PARAM1);
            mParam2 = getArguments().getInt(ARG_PARAM2);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        user = (FriendListElement)  mParam1.getSerializable("friend");
        view =  inflater.inflate(R.layout.fragment_friend_profile, container, false);
        Button backButton = (Button) view.findViewById(R.id.friendBack);
        serverCom = new ServerCom(getActivity().getApplicationContext());


        TextView name = (TextView) view.findViewById(R.id.friendName);
        name.setText(user.name);
        TextView tweet = (TextView) view.findViewById(R.id.friendTweet);
        tweet.setText(user.message);
        profile act = (profile) getActivity();
        backButton.setOnClickListener(this);

        listPictureMetas(user.id);

        Button friendButton = (Button) view.findViewById(R.id.send_button);
        friendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                act.addNewFriend(user.id);
            }
        });

        Button gallery = (Button) view.findViewById(R.id.gallery_button);
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profile act = (profile) getActivity();
                act.startGallery(mParam2,user.id);
            }
        });

        if(user.relation == 2 || user.relation == 3 || user.relation == 1 || user.id == mParam2){ //removes friendbutton if already friend/rejected/outgoing or is the user itself
            friendButton.setVisibility(View.GONE);
        }

        setBackgroundToTeamColor(view);
        fillFriends(view);
        getFriendList(user.id,view);




        return view;
    }

    private void setBackgroundToTeamColor(View view) {
        ConstraintLayout teamColor = view.findViewById(R.id.teamColor_friend);
        switch(user.team){
            case 1:
                teamColor.setBackgroundColor(Color.parseColor("#DC143C"));
                break;
            case 2:
                teamColor.setBackgroundColor(Color.parseColor("#7CFC00"));
                break;
            case 3:
                teamColor.setBackgroundColor(Color.parseColor("#00BFFF"));
                break;
        }
    }

    void getFriendList(int userId,View view){
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

                fillFriends(view);



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

    private void fillFriends(View view) {

        ListView friendsList = (ListView) view.findViewById(R.id.SameFriends);
        ArrayList<String> arrayList = new ArrayList<String>();

        if(requestedFriendList != null && requestedFriendList.size() >0){    //if we get information from the server set up the possibility for profilehopping
            for(int i = 0; i < requestedFriendList.size(); i++){
                arrayList.add(requestedFriendList.get(i).name);
            }

            friendsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(getActivity().getApplicationContext(), "Displaying Profile of Friend: " + arrayList.get(position), Toast.LENGTH_SHORT).show();

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("friend", requestedFriendList.get(position));

                    Fragment fragment = friend_Profile.newInstance(bundle, mParam2);
                    FragmentManager manager = getFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(R.id.frag_container, fragment);
                    transaction.commit();
                }
            });
        }
        else{
            arrayList.add("Why");
            arrayList.add("don't you");
            arrayList.add("become");
            arrayList.add("this person's");
            arrayList.add("first friend");
        }




        friendsList.setAdapter(new ArrayAdapter<String>(getActivity().getApplicationContext(),
                android.R.layout.simple_list_item_1 , arrayList));
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


    private void setProfilePicture() {
        PictureBase64Geo picture = profileImage;
        ImageView image = view.findViewById(R.id.friend_profile_image);
        try {
            byte[] decodedString = Base64.decode(picture.base64Data, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            image.setImageBitmap(decodedByte);
        }catch(Exception e){    //in case of bad64 error, try to clean
            try{
                System.out.println("!d "+picture.base64Data);
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
}

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
