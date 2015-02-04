package com.pocotopocopo.juego;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameUtils;

/**
 * Created by nico on 02/02/15.
 */



public abstract class BaseActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    protected GoogleApiClient googleApiClient;
    protected boolean signedIn=true;
    protected String mEmail;
    public static final String SIGNED_IN="signedIn";
    public static final String AUTO_SIGNED_IN="autoSignedIn";

    private static final String TAG="BaseActivity";

    private static int RC_SIGN_IN = 9001;

    private boolean mResolvingConnectionFailure = false;
    protected boolean mAutoStartSignInFlow = false;
    protected boolean mSignInClicked = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState!=null){
            mAutoStartSignInFlow=savedInstanceState.getBoolean(AUTO_SIGNED_IN);
        }

        Intent intent=getIntent();
        if (intent!=null){
            if (intent.getExtras().containsKey(SIGNED_IN)){
                signedIn= intent.getExtras().getBoolean(SIGNED_IN);
            } else {
                signedIn=false;
            }
        }
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                        // add other APIs and scopes here as needed
                .build();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            mSignInClicked = false;
            mResolvingConnectionFailure = false;
            if (resultCode == RESULT_OK) {
                googleApiClient.connect();
            } else {
                // Bring up an error dialog to alert the user that sign-in
                // failed. The R.string.signin_failure should reference an error
                // string in your strings.xml file that tells the user they
                // could not be signed in, such as "Unable to sign in."
                BaseGameUtils.showActivityResultError(this,
                        requestCode, resultCode, R.string.signin_failure);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(AUTO_SIGNED_IN,mAutoStartSignInFlow);
    }

    @Override
    protected void onStart() {

        super.onStart();
        if (signedIn) {
            googleApiClient.connect();

        }

    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mAutoStartSignInFlow=true;
        hideSignIn();

    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (mResolvingConnectionFailure) {

            // already resolving
            return;
        }

        // if the sign-in button was clicked or if auto sign-in is enabled,
        // launch the sign-in flow

        if (mSignInClicked || mAutoStartSignInFlow) {

            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = true;

            // Attempt to resolve the connection failure using BaseGameUtils.
            // The R.string.signin_other_error value should reference a generic
            // error string in your strings.xml file, such as "There was
            // an issue with sign-in, please try again later."
            if (!BaseGameUtils.resolveConnectionFailure(this,googleApiClient, connectionResult,RC_SIGN_IN, "There was an issue with sign-in, please try again later.")) {
                //Log.d(TAG,"ErrorCossssde="+connectionResult.getErrorCode());
                mResolvingConnectionFailure = false;
            }

        }
        displaySignIn();

    }

    protected void signIn(){
        mSignInClicked = true;
        mAutoStartSignInFlow=true;
        googleApiClient.connect();

    }
    protected void signOut(){
        mSignInClicked = false;
        mAutoStartSignInFlow=false;

        Games.signOut(googleApiClient);
        googleApiClient.disconnect();

    }

    public void displaySignIn(){}
    public void hideSignIn(){}
}
