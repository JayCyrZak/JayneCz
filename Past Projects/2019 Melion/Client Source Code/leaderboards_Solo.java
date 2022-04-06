package com.example.melion;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import com.android.volley.Request;

import com.example.melion.data.ServerCom;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Function;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link leaderboards_Solo.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link leaderboards_Solo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class leaderboards_Solo extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    ServerCom serverCom;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public leaderboards_Solo() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment leaderboards_Solo.
     */
    // TODO: Rename and change types and number of parameters
    public static leaderboards_Solo newInstance(String range, String param2) {
        leaderboards_Solo fragment = new leaderboards_Solo();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, range);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_leaderboards__solo, container, false);

        createTripleList(view);

        serverCom = new ServerCom(getActivity().getApplicationContext());

        String [] spinnerList = {"Distance walked","Achievements","Walked with friends"};

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_dropdown_item_1line, spinnerList);
        Spinner spinner = view.findViewById(R.id.sortbyspinner);
        spinner.setAdapter(arrayAdapter);



        return view;
    }

    private void createTripleList(View view) {
        Spinner sortbyspinner = (Spinner) view.findViewById(R.id.sortbyspinner);
        ListView soloListNr = (ListView) view.findViewById(R.id.solo_list_nr);
        ListView soloListName = (ListView) view.findViewById(R.id.solo_list_name);
        ListView soloListPts = (ListView) view.findViewById(R.id.solo_list_pt);

        sortbyspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View selectedItemView, int pos, long id) {
                Object o = parent.getItemAtPosition(pos);
                switch(o.toString()){
                    case "Distance walked":
                        loadPoints(view);
                        break;
                    case "Achievements":
                        loadAchievements(view);
                        break;
                    case "Walked with friends":
                        loadFriends(view);
                        break;
                    default:
                        System.out.println("Something went wrong while loading the leaderboards");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });
/*
        ArrayList<String> arrayListName = new ArrayList<String>();
        ArrayList<String> arrayListNum = new ArrayList<String>();
        ArrayList<String> arrayListPts = new ArrayList<String>();

        arrayListNum.add("1.");
        arrayListNum.add("2.");
        arrayListNum.add("3.");
        arrayListNum.add("4.");
        arrayListNum.add("5.");

        arrayListName.add("Alexander");
        arrayListName.add("Paul");
        arrayListName.add("Adrian");
        arrayListName.add("Alexander");
        arrayListName.add("Janik");

        arrayListPts.add("15");
        arrayListPts.add("14");
        arrayListPts.add("13");
        arrayListPts.add("12");
        arrayListPts.add("11");

        soloListNr.setAdapter(new ArrayAdapter<String>(getActivity().getApplicationContext(),
                android.R.layout.simple_list_item_1 , arrayListNum));
        soloListName.setAdapter(new ArrayAdapter<String>(getActivity().getApplicationContext(),
                android.R.layout.simple_list_item_1 , arrayListName));
        soloListPts.setAdapter(new ArrayAdapter<String>(getActivity().getApplicationContext(),
                android.R.layout.simple_list_item_1 , arrayListPts));
         */
    }

    private void loadPoints(View view){
        Function<JSONObject,Void> points = loadPoints -> {
            ArrayList<rank> users = parseJSON(loadPoints);
            System.out.println(users);
            setContent(users,view);
            return null;
        };
        System.out.println("/leaderboards/"+ mParam1 + "/distance/");
        serverCom.request("/leaderboards/"+ mParam1 + "/distance/",Request.Method.GET,null,points);
    }
    private void loadAchievements(View view){
        Function<JSONObject,Void> achievements = loadAchievements -> {
            ArrayList<rank> users = parseJSON(loadAchievements);
            System.out.println(users);
            setContent(users,view);
            return null;
        };
        System.out.println("/leaderboards/"+ mParam1 + "/achievements/");
        serverCom.request("/leaderboards/"+ mParam1 + "/achievements/",Request.Method.GET,null,achievements);
    }
    private void loadTeams(View view){
        Function<JSONObject,Void> teams = loadTeams -> {
            ArrayList<rank> users = parseJSON(loadTeams);
            System.out.println(users);
            setContent(users,view);
            return null;
        };
        System.out.println("/leaderboards/"+ mParam1 + "/teams/");
        serverCom.request("/leaderboards/"+ mParam1 + "/teams/",Request.Method.GET,null,teams);
    }
    private void loadFriends(View view){
        Function<JSONObject,Void> friends = loadFriends -> {
            ArrayList<rank> users = parseJSON(loadFriends);
            System.out.println(users);
            setContent(users,view);
            return null;
        };
        System.out.println("/leaderboards/"+ mParam1 + "/paintedtogether/");
        serverCom.request("/leaderboards/"+ mParam1 + "/paintedtogether/",Request.Method.GET,null,friends);
    }

    private ArrayList<rank> parseJSON(JSONObject json){
        ArrayList<rank> result = new ArrayList<rank>();
        try{
            System.out.println(json);
            if(json.has("user_ranks")){
                JSONArray arr = json.getJSONArray("user_ranks");
                for (int i = 0; i < arr.length(); i++) {
                    int rank = arr.getJSONObject(i).getInt("rank");
                    String name = arr.getJSONObject(i).getString("name");
                    int points = arr.getJSONObject(i).getInt("points");
                    result.add(new rank(rank ,name ,points ));
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        Collections.sort(result);
        return result ;
    }

    private void setContent(ArrayList<rank> ranks,View view){
        ListView soloListNr = (ListView) view.findViewById(R.id.solo_list_nr);
        ListView soloListName = (ListView) view.findViewById(R.id.solo_list_name);
        ListView soloListPts = (ListView) view.findViewById(R.id.solo_list_pt);

        ArrayList<String> rank = new ArrayList<String>();
        ArrayList<String> name = new ArrayList<String>();
        ArrayList<String> points = new ArrayList<String>();
        for(int i = 0; i<ranks.size();i++){
            rank.add(i,ranks.get(i).getRank() + ". ");
            name.add(i, ranks.get(i).getName());
            points.add(i, ranks.get(i).getPoints()+"");
        }
        if(getActivity()!=null) {
            soloListNr.setAdapter(new ArrayAdapter<String>(getActivity().getApplicationContext(),
                    android.R.layout.simple_list_item_1, rank));
            soloListName.setAdapter(new ArrayAdapter<String>(getActivity().getApplicationContext(),
                    android.R.layout.simple_list_item_1, name));
            soloListPts.setAdapter(new ArrayAdapter<String>(getActivity().getApplicationContext(),
                    android.R.layout.simple_list_item_1, points));
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

  /*  @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }*/

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
