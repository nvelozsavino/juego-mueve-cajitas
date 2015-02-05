package com.pocotopocopo.juego;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;
import java.util.logging.Handler;

/**
 * Created by nico on 04/02/15.
 */


public class AuthToken {

    private Context context;
    private String accountName;
    private String scope;
    private String token;
    private Exception exception;
    private boolean result;
    private Callback listener;


    public AuthToken(Context context, String accountName, String scope, Callback listener){
        this.accountName=accountName;
        this.context=context;
        this.scope=scope;
        this.listener=listener;
        result=false;
    }

    public void getToken(){
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    token = GoogleAuthUtil.getToken(context, accountName, scope);
                    result=true;
                    //TODO: connect to backend server
                } catch (Exception e) {
                    exception=e;
                    result=false;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (result){
                    listener.onTokenAcquired(token);
                } else {
                    listener.onError(exception);
                }
            }
        };

        task.execute();
    }


    public static interface Callback{
        public void onTokenAcquired(final String token);
        public void onError(final Exception e);
    }

}
