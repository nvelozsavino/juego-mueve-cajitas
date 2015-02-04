package com.pocotopocopo.juego;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;
import com.google.example.games.basegameutils.BaseGameUtils;

import java.io.IOException;

/**
 * Created by nico on 02/02/15.
 */



public abstract class BaseActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    protected GoogleApiClient googleApiClient;
    protected boolean signedIn=true;

    public static final String SIGNED_IN="signedIn";
    public static final String AUTO_SIGNED_IN="autoSignedIn";
    private static final String SCOPE = "audience:server:client_id:844436793955-05qq7b1u4g82gq4mljjg0na9bbk6no1t.apps.googleusercontent.com";

    private static final String TAG="BaseActivity";

    private static int RC_SIGN_IN = 9001;

    private boolean mResolvingConnectionFailure = false;
    protected boolean mAutoStartSignInFlow = false;
    protected boolean mSignInClicked = false;
    static final int REQUEST_CODE_RECOVER_FROM_AUTH_ERROR = 1001;
    static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1002;

    private String token;
    private String mEmail;

    public static final String EXTRA_ACCOUNTNAME = "extra_accountname";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState!=null){
            mAutoStartSignInFlow=savedInstanceState.getBoolean(AUTO_SIGNED_IN);
        }

        Intent intent=getIntent();
        if (intent!=null){
            try{
                signedIn= intent.getExtras().getBoolean(SIGNED_IN);
            } catch (Exception e){
                Log.e(TAG, "No",e);
                signedIn=false;
            }



        }
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .addApi(Plus.API)
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
        mEmail =Plus.AccountApi.getAccountName(googleApiClient);


        startTask(mEmail,SCOPE).execute();





        hideSignIn();

    }

    private AsyncTask<Void,Void,Void> startTask(final String accountName, final String scope) {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    token = GoogleAuthUtil.getToken(getApplicationContext(), accountName, scope);
                    //TODO: connect to backend server
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
