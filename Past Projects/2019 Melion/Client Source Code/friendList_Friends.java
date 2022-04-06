package com.example.melion;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.melion.data.FriendListElement;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link friendList_Friends.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link friendList_Friends#newInstance} factory method to
 * create an instance of this fragment.
 */
public class friendList_Friends extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int mParam1;
    private Bundle mParam2;

    private ArrayList<FriendListElement> requestedFriendList;
    private OnFragmentInteractionListener mListener;

    public friendList_Friends() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment friendList_Friends.
     */
    // TODO: Rename and change types and number of parameters
    public static friendList_Friends newInstance(int param1, Bundle param2) {
        friendList_Friends fragment = new friendList_Friends();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putBundle(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARAM1);
            mParam2 = getArguments().getBundle(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_list__friends, container, false);

        requestedFriendList = (ArrayList<FriendListElement>) mParam2.getSerializable("friends");


        ListView friendsList = (ListView) view.findViewById(R.id.List_friends);
        ArrayList<String> arrayList = new ArrayList<String>();

        if(requestedFriendList != null){
            for (int i = 0; i < requestedFriendList.size();i ++){
                if(requestedFriendList.get(i).relation == 3){
                    arrayList.add(requestedFriendList.get(i).name);
                }

            }
        }
        else {
            arrayList.add("Here");
            arrayList.add("we");
            arrayList.add("need");
            arrayList.add("the");
            arrayList.add("List");
            arrayList.add("of");
            arrayList.add("Friends");
            arrayList.add("from");
            arrayList.add("the");
            arrayList.add("Server/UserProfile");

        }




        friendsList.setAdapter(new ArrayAdapter<String>(getActivity().getApplicationContext(),
                android.R.layout.simple_list_item_1 , arrayList));

        return view;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

 /*   @Override
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
