package com.musicgarden.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.musicgarden.android.lib.NetworkLayer;
import com.musicgarden.android.lib.NetworkService;
import com.musicgarden.android.lib.ViewController;
import com.musicgarden.android.responses.SignInResponse;
import com.musicgarden.android.utils.Globals;
import com.musicgarden.android.utils.ViewUtil;

import java.io.IOException;
import java.util.HashMap;

public class GoogleSignInActivity extends
        AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener, ViewController {


    private GoogleApiClient mGoogleApiClient;


    public static final int RC_SIGN_IN = 9001;

    private boolean googleLoggedInUser = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_sign_in);


        SignInButton googleSignInButton = (SignInButton) findViewById(R.id.googleSignInButtonInLoginPage);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(Scopes.PROFILE))
                .requestIdToken(getString(R.string.GOOGLE_CLIENT_AUTHORISATION_TOKEN))
                .build();


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        googleSignInButton.setOnClickListener(this);

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        ViewUtil.showToast(this, "Failed to login...\nCheck your internet connection");
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.googleSignInButtonInLoginPage:

                googleSignIn();

                break;

        }

    }


    private void googleSignIn() {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {

        if (result.isSuccess()) {

            GoogleSignInAccount acct = result.getSignInAccount();

            googleLoggedInUser = true;
            NetworkLayer<GoogleSignInActivity> networkLayer = new NetworkLayer<GoogleSignInActivity>(this);

            HashMap<String, String> headers = new HashMap<String, String>();
            HashMap<String, String> query = new HashMap<String, String>();
            query.put("google_token", acct.getIdToken());

            networkLayer.get(getString(R.string.api_google_sign_in), headers, query);


        } else {

            Globals.userLoggedIn = false;

            ViewUtil.showToast(this, "Failed to login.\nTry again later");

        }
    }

    public void storeTokenSharedPrefs(String token, String name, String image_url, String email, Integer id) {
        SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.shared_prefs), MODE_PRIVATE).edit();
        editor.putString("User Token", token);
        editor.putString("User Name", name);
        editor.putString("User Image", image_url);
        editor.putString("User Email", email);
        editor.putInt("User ID", id);
        Globals.setToken(token);
        Globals.setImage_url(image_url);
        Globals.setName(name);
        Globals.setEmail(email);
        Globals.setId(id);
        Globals.userLoggedIn = true;
        editor.apply();

        if (googleLoggedInUser) {
            signOut();
        }

        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onResponse(String response) {

        ObjectMapper mapper = new ObjectMapper();
        SignInResponse signInResponse = new SignInResponse();
        try {
            signInResponse = mapper.readValue(response, SignInResponse.class);
        } catch (IOException e) {
            ViewUtil.showToast(this, "Mapping error in Sign In");
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        }
        try {
            if (signInResponse.getResponse().equals("success")) {
                storeTokenSharedPrefs(signInResponse.getToken(), signInResponse.getUser_detail().getName(),
                        signInResponse.getUser_detail().getImage_url(), signInResponse.getUser_detail().getEmail(), signInResponse.getUser_detail().getId());

            } else {
                ViewUtil.showToast(this, signInResponse.getError());
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        } catch (Exception e) {
            ViewUtil.showToast(this, "Something Went Wrong");
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        }
    }

    @Override
    public void onError(Throwable t) {
        ViewUtil.showToast(this, t.toString());
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }


    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {

                mGoogleApiClient.clearDefaultAccountAndReconnect();

            }
        });
    }
}