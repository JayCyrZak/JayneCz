package com.example.melion;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.melion.data.FriendListElement;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link friendList_choice.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link friendList_choice#newInstance} factory method to
 * create an instance of this fragment.
 */
public class friendList_choice extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int mParam1;    //position in List of requests fragment
    private Bundle mParam2;    //user to be sent friendrequest

    private OnFragmentInteractionListener mListener;

    public friendList_choice() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment friendList_choice.
     */
    // TODO: Rename and change types and number of parameters
    public static friendList_choice newInstance(int param1, Bundle param2) {
        friendList_choice fragment = new friendList_choice();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,// Inflate the layout for this fragment
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_friend_list_choice, container, false);

        FriendListElement friend = (FriendListElement)  mParam2.getSerializable("friend");

        TextView name = view.findViewById(R.id.choice_name);
        name.setText(friend.name);


        Button back = view.findViewById(R.id.backButton);
        Button accept = view.findViewById(R.id.accept_button);
        Button reject = view.findViewById(R.id.reject_button);
        FragmentManager manager = getFragmentManager();


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destroy();
            }
        });

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendList act = (friendList) getActivity();
                friendList_Requests.getInstance().removeFromIncoming(mParam1);
                act.acceptNewFriend(friend.id);
                destroy();
            }
        });

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendList act = (friendList) getActivity();
                friendList_Requests.getInstance().removeFromIncoming(mParam1);
                act.removeFriend(friend.id);
                destroy();
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void destroy(){
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
