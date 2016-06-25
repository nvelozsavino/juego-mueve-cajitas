package com.pocotopocopo.puzzlide.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.pocotopocopo.puzzlide.AuthToken;
import com.pocotopocopo.puzzlide.MultiplayerUpdateListener;
import com.pocotopocopo.puzzlide.R;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by nico on 02/02/15.
 */



public abstract class BaseActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    protected GoogleApiClient googleApiClient;
    protected boolean signedIn;

    public static final String SIGNED_IN="signedIn";
    public static final String AUTO_SIGNED_IN="autoSignedIn";
    private static final String SCOPE = "audience:server:client_id:858786730441-g14rkqs5d3spfjqj09d3lh5t260nvgfu.apps.googleusercontent.com";

    private static final String TAG="Juego.BaseActivity";

    private static int RC_SIGN_IN = 9001;

    private boolean mResolvingConnectionFailure = false;
    protected boolean mAutoStartSignInFlow = false;
    protected boolean mSignInClicked = false;
    static final int REQUEST_CODE_RECOVER_FROM_AUTH_ERROR = 1001;
    static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1002;

    private String token;
    private String mEmail;

    protected AuthToken authToken;

    protected SignInButton signInButton;
    protected Button signOutButton;

    public static final String EXTRA_ACCOUNTNAME = "extra_accountname";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"************************************* START *************************** " + this.getClass());
        Log.d(TAG,"onCreate "+ this.getClass());
        super.onCreate(savedInstanceState);

//        if (savedInstanceState!=null){
//            mAutoStartSignInFlow=savedInstanceState.getBoolean(AUTO_SIGNED_IN);
//        }
//
//        Intent intent=getIntent();
//        if (intent!=null && intent.getExtras()!=null) {
//            if (intent.getExtras().containsKey(SIGNED_IN)) {
//
//                signedIn = intent.getExtras().getBoolean(SIGNED_IN);
//                Log.d(TAG, "extras");
//            } else {
//                Log.d(TAG, "no extras");
//                signedIn = false;
//            }
//            Log.d(TAG, "Signed In: " + signedIn);
//        }



        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .addApi(Plus.API)
                        // add other APIs and scopes here as needed
                .build();


        signInButton=(SignInButton)findViewById(R.id.signInButton);
        signOutButton=(Button)findViewById(R.id.signOutButton);
        if (signInButton!=null) {
            signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signIn();
                }
            });
        }
        if (signOutButton!=null) {
            signOutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signOut();
                    disconnected();

                }
            });
        }

    }

    protected void initViews(){
        signInButton=(SignInButton)findViewById(R.id.signInButton);
        signOutButton=(Button)findViewById(R.id.signOutButton);
        if (signInButton!=null) {
            signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signIn();
                }
            });
        }
        if (signOutButton!=null) {
            signOutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signOut();
                    disconnected();

                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG,"onActivityResult " + this.getClass());
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
        if ((requestCode == REQUEST_CODE_RECOVER_FROM_AUTH_ERROR ||
                requestCode == REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR)
                && resultCode == RESULT_OK) {
            if (data == null) {
                Log.d(TAG,"Unknown error, click the button again");
                return;
            }
            if (resultCode == RESULT_OK) {
                Log.i(TAG, "Retrying");
                startTask(mEmail, SCOPE).execute();
                return;
            }
            if (resultCode == RESULT_CANCELED) {
                Log.d(TAG,"User rejected authorization.");
                return;
            }
            return;
        }
    }




    /**
     * This method is a hook for background threads and async tasks that need to provide the
     * user a response UI when an exception occurs.
     */
    public void handleException(final Exception e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (e instanceof GooglePlayServicesAvailabilityException) {
                    // The Google Play services APK is old, disabled, or not present.
                    // Show a dialog created by Google Play services that allows
                    // the user to update the APK
                    int statusCode = ((GooglePlayServicesAvailabilityException)e)
                            .getConnectionStatusCode();
                    Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode,
                            BaseActivity.this,
                            REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                    dialog.show();
                } else if (e instanceof UserRecoverableAuthException) {
                    // Unable to authenticate, such as when the user has not yet granted
                    // the app access to the account, but the user can fix this.
                    // Forward the user to an activity in Google Play services.
                    Intent intent = ((UserRecoverableAuthException)e).getIntent();
                    startActivityForResult(intent,
                            REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                }
            }
        });
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG,"onSaveInstanceState " + this.getClass());
        super.onSaveInstanceState(outState);
        outState.putBoolean(AUTO_SIGNED_IN,mAutoStartSignInFlow);
    }

    @Override
    protected void onStart() {
        Log.d(TAG,"onStart "+ this.getClass());

        super.onStart();

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.preference_key_file), Context.MODE_PRIVATE);
        signedIn = sharedPreferences.getBoolean(SIGNED_IN, false);
        Log.d(TAG,"signedIn (var): " +signedIn);
        if (signedIn) {
            googleApiClient.connect();

        } else {
            disconnected();
        }

    }

    @Override
    protected void onStop() {
        Log.d(TAG,"onStop " + this.getClass());
        if (googleApiClient!=null && googleApiClient.isConnected()) {
            Games.TurnBasedMultiplayer.unregisterMatchUpdateListener(googleApiClient);
        }
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG,"onConnected " + this.getClass());
        //mAutoStartSignInFlow=true;
        mEmail = Plus.AccountApi.getAccountName(googleApiClient);

        Games.TurnBasedMultiplayer.registerMatchUpdateListener(googleApiClient,new MultiplayerUpdateListener(this));
        startTask(mEmail,SCOPE).execute();
        connected();
    }



    private AsyncTask<Void,Void,Void> startTask(final String accountName, final String scope) {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    token = GoogleAuthUtil.getToken(getApplicationContext(), accountName, scope);
                    //Log.d(TAG,"Token: " + token);
                    //TODO: connect to backend server, not yet
                    URL url= new URL("https://www.googleapis.com/oauth2/v1/tokeninfo?id_token=" + token);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.connect();
                    int responseCode = conn.getResponseCode();
                    String responseReason = conn.getHeaderField(0);
                    Log.d(TAG,"Response code= " + responseCode + "reason:" +responseReason);


                } catch (UserRecoverableAuthException userRecoverableException) {
                    // GooglePlayServices.apk is either old, disabled, or not present, which is
                    // recoverable, so we need to show the user some UI through the activity.
                    handleException(userRecoverableException);
                } catch (GoogleAuthException fatalException) {
                    Log.e(TAG, "Unrecoverable error " + fatalException.getMessage(), fatalException);
                } catch (IOException ioe) {
                    Log.e(TAG, "IO error " + ioe.getMessage(), ioe);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                //Toast.makeText(getApplicationContext(), "Token: " + token, Toast.LENGTH_SHORT).show();
            }
        };
        return task;
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG,"onConnectionSuspended " + this.getClass());
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG,"onConnectionFailed " + this.getClass());
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
        disconnected();

    }

    protected void signIn(){
        mSignInClicked = true;
        mAutoStartSignInFlow=true;
        googleApiClient.connect();

    }
    protected void signOut(){
        mSignInClicked = false;
        mAutoStartSignInFlow=false;
        if (googleApiClient!=null && googleApiClient.isConnected()) {
            Games.TurnBasedMultiplayer.unregisterMatchUpdateListener(googleApiClient);
        }
        Games.signOut(googleApiClient);

        googleApiClient.disconnect();

    }

    public void disconnected(){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.preference_key_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SIGNED_IN, googleApiClient.isConnected());
        editor.commit();
        if (signInButton!=null) {
            signInButton.setVisibility(View.VISIBLE);// Put code here to display the sign-in button
        }
        if (signOutButton!=null) {
            signOutButton.setVisibility(View.GONE);
        }

    }
    public void connected(){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.preference_key_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SIGNED_IN, googleApiClient.isConnected());
        editor.commit();
        if (signInButton!=null) {
            signInButton.setVisibility(View.GONE);// Put code here to display the sign-in button
        }
        if (signOutButton!=null) {
            signOutButton.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected void onResume() {
        Log.d(TAG,"onResume " + this.getClass());
        super.onResume();


    }

    @Override
    protected void onPause() {

        Log.d(TAG,"onPause " + this.getClass());
        Log.d(TAG,"signedIn: " +googleApiClient.isConnected());

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.preference_key_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SIGNED_IN, googleApiClient.isConnected());
        editor.commit();
        super.onPause();
    }
}
