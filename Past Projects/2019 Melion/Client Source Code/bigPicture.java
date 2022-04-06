package com.example.melion;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.melion.data.PictureBase64Geo;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link bigPicture.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link bigPicture#newInstance} factory method to
 * create an instance of this fragment.
 */
public class bigPicture extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private Bundle mParam1; //PictureData
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public bigPicture() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment bigPicture.
     */
    // TODO: Rename and change types and number of parameters
    public static bigPicture newInstance(Bundle param1, String param2) {
        bigPicture fragment = new bigPicture();
        Bundle args = new Bundle();
        args.putBundle(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getBundle(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_big_picture, container, false);

        PictureBase64Geo picture = (PictureBase64Geo) mParam1.getSerializable("picture");
        ImageView image = view.findViewById(R.id.big_Picture_View);

        try {
            byte[] decodedString = Base64.decode(picture.base64Data, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            image.setImageBitmap(decodedByte);
        }catch(Exception e){    //in case of bad64 error, try to clean
            try{
                System.out.println("!d try block");

                view_picture act = (view_picture) getActivity();
                String cleaned = act.cleanbase64(picture.base64Data);

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
                destroy(view);
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

    public void destroy(View v) {
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
