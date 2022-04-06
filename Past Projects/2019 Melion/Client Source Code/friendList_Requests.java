package com.example.melion;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.melion.data.FriendListElement;
import com.example.melion.data.ServerCom;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link friendList_Requests.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link friendList_Requests#newInstance} factory method to
 * create an instance of this fragment.
 */
public class friendList_Requests extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int mParam1;
    private Bundle mParam2;

    View view;
    private static friendList_Requests instance = null;
    private ArrayList<String> incomingList;
    private ArrayList<FriendListElement> requestedFriendList ;
    private ArrayList<FriendListElement> requestsOutgoing ;
    private ArrayList<FriendListElement> requestsIncoming ;
    private OnFragmentInteractionListener mListener;

    public friendList_Requests() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment friendList_Requests.
     */
    // TODO: Rename and change types and number of parameters
    public static friendList_Requests newInstance(int param1, Bundle param2) {
        friendList_Requests fragment = new friendList_Requests();
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
        view = inflater.inflate(R.layout.fragment_friend_list__requests, container, false);
        instance = this;
        friendList act = (friendList) getActivity();

        ListView incoming = (ListView) view.findViewById(R.id.requests_incoming);
        incomingList = new ArrayList<String>();

        ListView outgoing = (ListView) view.findViewById(R.id.requests_outgoing);
        ArrayList<String> outgoingList = new ArrayList<String>();

        requestedFriendList = (ArrayList<FriendListElement>) mParam2.getSerializable("friends");
        requestsOutgoing = new ArrayList<>();
        requestsIncoming = new ArrayList<>();

        if(requestedFriendList != null){    //sort friendsrequests in in and out
            for(int i = 0; i < requestedFriendList.size(); i++){
                FriendListElement f = requestedFriendList.get(i);
                if(f.relation == 3 || f.relation == 2) continue;    //ignore Friends and Rejects
                switch (f.direction){
                    case 0:
                        break;
                    case 1:
                        requestsIncoming.add(f);
                        incomingList.add(f.name);
                        break;
                    case 2:
                        requestsOutgoing.add(f);
                        outgoingList.add(f.name);
                        break;
                     default:
                         System.out.println("!d Friend with undefined Direction");
                }
            }
        }


        incoming.setAdapter(new ArrayAdapter<String>(getActivity().getApplicationContext(),
                android.R.layout.simple_list_item_1 , incomingList));


        outgoing.setAdapter(new ArrayAdapter<String>(getActivity().getApplicationContext(),
                android.R.layout.simple_list_item_1 , outgoingList));

        incoming.setOnItemClickListener(new AdapterView.OnItemClickListener() { //Click on item of start choice frag to accept/reject friendrequest
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*act.acceptNewFriend(requestsIncoming.get(position).id);
                incomingList.remove(position);
                requestsIncoming.remove(position);
                incoming.setAdapter(new ArrayAdapter<String>(getActivity().getApplicationContext(),
                        android.R.layout.simple_list_item_1 , incomingList));*/
                Bundle bundle = new Bundle();
                bundle.putSerializable("friend", requestsIncoming.get(position));

                Fragment frag = friendList_choice.newInstance(position, bundle);
                FragmentManager fragmanager = getFragmentManager();
                FragmentTransaction transaction = fragmanager.beginTransaction();
                transaction.add(R.id.requests_container, frag);

                transaction.commit();
            }
        });

        EditText newFriend = view.findViewById(R.id.send_button_txt);

        Button confirm = (Button) view.findViewById(R.id.send_button);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                act.searchByName(newFriend.getText().toString());
                InputMethodManager imm = (InputMethodManager) act.getSystemService(Activity.INPUT_METHOD_SERVICE);
                View view = act.getCurrentFocus();
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });


        /*RelativeLayout l = view.findViewById(R.id.requests_container);
        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) act.getSystemService(Activity.INPUT_METHOD_SERVICE);
                View view = act.getCurrentFocus();
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });*/

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

    public void removeFromIncoming(int position){
        incomingList.remove(position);
        requestsIncoming.remove(position);
        ListView incoming = (ListView) view.findViewById(R.id.requests_incoming);
        incoming.setAdapter(new ArrayAdapter<String>(getActivity().getApplicationContext(),
                android.R.layout.simple_list_item_1 , incomingList));
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

    public static friendList_Requests getInstance() {
        return instance;
    }
}
