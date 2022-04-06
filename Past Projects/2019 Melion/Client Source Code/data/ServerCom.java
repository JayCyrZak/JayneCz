package com.example.melion.data;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Function;


import static android.content.Context.MODE_PRIVATE;
import static java.lang.Math.min;


public class ServerCom {
    private static String mToken;
    private static String mBaseURL = "https://socialgaming.deine.cloud/api/";
    private static boolean mValidated;

    private Response.Listener listener = null;
    private Context mCallerContext; // context from caller activity
    private RequestQueue mRequestQueue;

    private void loadFromStorage(){
        SharedPreferences store = PreferenceManager.getDefaultSharedPreferences(mCallerContext);
        this.mToken = store.getString("melion-key", null);
        this.mValidated = store.getBoolean("melion-validated", false);
    }

    private void saveToStorage(){
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(mCallerContext).edit();
        edit.putString("melion-key", mToken);
        edit.putBoolean("melion-validated", mValidated);
        edit.commit();
    }

    private void validationStatusRequest(){
        Function<JSONObject, Void> fun = jIn -> {
            try{
                if(jIn.getString("status").equals("ok")) {
                    if(jIn.getString("value").equals("ready")) {
                        mValidated = true;
                        System.out.println("!d validated by server");
                    } else {
                        mValidated = false;
                    }
                }
            } catch (Exception e){
                System.out.println("!d " + e.getMessage());
            }
            return null;
        };

        sendJsonRequest("status", Request.Method.GET, true, null, fun);
    }

    public ServerCom(Context callerContext){
        this.mCallerContext = callerContext;
        this.mRequestQueue = Volley.newRequestQueue(callerContext);

        //if(mToken == null || !mValidated)
        loadFromStorage();
        if(mToken == null || !mValidated){
            mToken = null;
            mValidated = false;
            saveToStorage();
        } else {
            validationStatusRequest();
        }
        
    }

    /* USAGE
    1. Register (Button in Profile)
    2. Verify (Button in Profile)
    3. use request(...)
    Filter in Logcat: !d
     */

    // check if key is validated
    public boolean isValidated(){
        System.out.println("!d " + mToken + ": " + (mValidated ? "validated" : "not validated"));
        return mValidated;
    }

    /* request a json object from the server
    path: e.g. "register"
    method: Request.Method.POST or Request.Method.GET
    jsonReq: add json request object
    onResponse gets called when response is available
    */
    public void request(String path, int method, JSONObject jsonReq, Function<JSONObject, Void> onResponse){
		if (jsonReq == null) {
			jsonReq = new JSONObject();
            try {
                jsonReq.put("status", "ok");
            } catch (Exception e) {
                System.out.println("!d " + e.getMessage());
            }
        }
		
        sendJsonRequest(path, method,true,jsonReq, onResponse);
    }

    private void sendJsonRequest(String path, int method, final boolean auth, JSONObject jsonReq, Function<JSONObject, Void> fun){
        JsonObjectRequest req = new JsonObjectRequest(method, mBaseURL + path, jsonReq,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response){
                            fun.apply(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                System.out.println("!d Volley-Error: " + error.getMessage());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                if(auth) {
                    params.put("melion-key", mToken);
                }
                return params;
            }
        };
        mRequestQueue.add(req);
    }

    public void registerRequest(long phoneNumber) {
        if(mToken != null){
            if(listener != null) listener.onResponse(true);
            listener = null;
            System.out.println("!d already registered");
            return;
        }

        JSONObject jObj = new JSONObject();
        try { jObj.put("phone-number", phoneNumber); }
        catch (Exception e){ System.out.println("!d Request-Error ");}

        Function<JSONObject, Void> fun = jIn -> {
            try {
                System.out.println("!d Register Response Key: " +  jIn.getString("key"));
                mToken = jIn.getString("key");
                if(listener != null) listener.onResponse(new Boolean(true));
                listener = null;
            } catch (Exception e){
                if(listener != null) listener.onResponse(new Boolean(false));
                listener = null;
                System.out.println("!d Response-Error ");
            }
            return null;
        };
        sendJsonRequest("register", Request.Method.POST, false, jObj, fun);
    }

    public void validateRequest(String smsKey) {
        if (mToken == null) {
            if(listener != null) listener.onResponse(false);
            listener = null;
            System.out.println("!d token not set");
            return;
        }
        if (mValidated) {
            if(listener != null) listener.onResponse(true);
            listener = null;
            System.out.println("!d already validated");
            return;
        }
        JSONObject j = new JSONObject();
        try {
            j.put("sms-key", smsKey);
        } catch (Exception e) {
            System.out.println("!d " + e.getMessage());
        }

        Function<JSONObject, Void> fun = jIn -> {
            try{
                System.out.println("!d validation Status: " + jIn.getString("status"));
                if(jIn.getString("status").equals("ok")) {
                    mValidated = true;
                    System.out.println("!d Validation Successfull; Saving Token to Storage");
                     if(listener != null)listener.onResponse(true);
                    listener = null;
                    saveToStorage();
                } else {
                    System.out.println("!d val" + jIn.getString("reason"));
                    if(listener != null)listener.onResponse(false);
                    listener = null;
                    mValidated =  false;
                }
            } catch (Exception e){
                System.out.println("!d " + e.getMessage());
            }
            return null;
        };

        sendJsonRequest("validate", Request.Method.POST, true, j, fun);
    }

    public void setListener(Response.Listener listener) {
        this.listener = listener;
    }


    public void setDemoUser(int n){
        if(n < 1 || n > 10){
            System.out.println("!d only demouser: 1 - 10");
        }
        String token = "";
        for(int i = 0; i < n; i++){
            token += "A";
        }
        System.out.println("!d demouser" + n + " token: " + token);
        mToken = token;
        mValidated = true;
        saveToStorage();
    }

    public void removeToken(){
        mToken = null;
        mValidated = false;
        saveToStorage();
        System.out.println("!d token removed");
    }

    public void cycleDemoUser() {
        int i = (mToken.length()+1)%11;
        if(i == 0) i++;
        setDemoUser(i);
    }

    public int getActiveDemoUser(){
        return mToken.length();
    }
}
