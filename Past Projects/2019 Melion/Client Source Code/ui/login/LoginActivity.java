package com.example.melion.ui.login;

import android.app.Activity;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.example.melion.Main_MenueActivity;
import com.example.melion.R;
import com.example.melion.data.ServerCom;

public class LoginActivity extends AppCompatActivity {

    private ServerCom serverCom;
    private Button registerButton;
    private EditText usernameEditText ;
    private ProgressBar loadingProgressBar;
    private Button verifyButton;
    private EditText verifyEditText;
    private Button skipButton;
    private ConstraintLayout container;
    private TextView melion_talk;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        melion_talk = findViewById(R.id.melion_speech);
        container = findViewById(R.id.container);
        verifyEditText = findViewById(R.id.verify_number);
        usernameEditText = findViewById(R.id.username);
        verifyButton = findViewById(R.id.verify_button);
        loadingProgressBar = findViewById(R.id.loading);
        registerButton = findViewById(R.id.register_button);
        serverCom = new ServerCom(this);

        if(serverCom.isValidated()){
            startActivity(new Intent(LoginActivity.this, Main_MenueActivity.class));
            finish();
            showMassage("Welcome back");
        }

        melion_talk.setText("Hi, Welcome to Melion. To recognize you later we ask for your PhoneNumber. If you still want to continue please enter your PhoneNumber and press the Register button");
        configureButtons();

    }

    private void configureButtons() {
        configureRegisterButton(registerButton);;
        configureVerifyButton(verifyButton);

        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                View view = getCurrentFocus();
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
    }

    private void configureSkipButton() {    //no longer necessary
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, Main_MenueActivity.class));
            }
        });
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    public void configureRegisterButton(Button register){
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    loadingProgressBar.setVisibility(View.VISIBLE);
                    serverCom.setListener(new Response.Listener() {
                        @Override
                        public void onResponse(Object response) {
                            if(response.equals(true)){
                                System.out.println("!d" + response);
                                loadingProgressBar.setVisibility(View.INVISIBLE);
                                registerButton.setVisibility(View.GONE);
                                verifyButton.setVisibility(View.VISIBLE);
                                verifyEditText.setVisibility(View.VISIBLE);
                                melion_talk.setText("And we have liftoff. Now you see that field with the '1234'... Yea we ignore that for now and just press Verify");
                            }
                            if(response.equals(false)){
                                loadingProgressBar.setVisibility(View.INVISIBLE);
                                melion_talk.setText("Hmm. it seems there was some kind of Error, when you pressed that button, Change it and try again");
                            }
                        }
                    });
                    serverCom.registerRequest(Integer.parseInt(usernameEditText.getText().toString()));
                } catch (Exception e){
                    melion_talk.setText("This seems to be an Error with the Server");
                    System.out.println("!d register err");

                }
            }
        });
    }

    public void configureVerifyButton(Button register){
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    loadingProgressBar.setVisibility(View.VISIBLE);
                    serverCom.setListener(new Response.Listener() {
                        @Override
                        public void onResponse(Object response) {
                            if ((boolean) response == true) {
                                loadingProgressBar.setVisibility(View.INVISIBLE);
                                startActivity(new Intent(LoginActivity.this, OnUserFirstLogin.class));
                                finish();
                            }
                            if ((boolean) response == false) {
                                loadingProgressBar.setVisibility(View.INVISIBLE);
                                melion_talk.setText("Hmm, did'nt work, Did you change the '1234' in the Textfield?");
                            }
                        }

                    });
                    serverCom.validateRequest(verifyEditText.getText().toString());
                }catch (Exception e){
                    melion_talk.setText("Hmm, did'nt work, Did you change the '1234' in the Textfield?");
                    System.out.println("!d verify err");

                }
            }
        });
    }

    protected void showMassage(String Massage) {
        Toast.makeText(this, Massage, Toast.LENGTH_SHORT).show();
    }
}
