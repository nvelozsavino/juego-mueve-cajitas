package com.pocotopocopo.juego;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


public class MultiplayerActivity extends BaseActivity {

    private final static String TAG="MultiplayerActivity";
    private LinearLayout multiplayerActionsLayout;
    private Button multiplayerStartMatchButton;
    private Button multiplayerQuickMatchButton;
    private Button multiplayerCheckGamesMatchButton;

    private LinearLayout sessionActionsLayout;
    private SignInButton signInButton;
    private Button signOutButton;

    private GameInfo gameInfo;

    final static int RC_SELECT_PLAYERS = 10000;
    final static int RC_LOOK_AT_MATCHES = 10001;

    public boolean isDoingTurn = false;
    private Bundle gameOptions;

    @Override
    protected void initViews(){
        super.initViews();

        multiplayerActionsLayout = (LinearLayout)findViewById(R.id.multiplayerActionsLayout);
        multiplayerStartMatchButton = (Button)findViewById(R.id.multiplayerStartMatchButton);
        multiplayerQuickMatchButton = (Button)findViewById(R.id.multiplayerQuickMatchButton);
        multiplayerCheckGamesMatchButton = (Button)findViewById(R.id.multiplayerCheckGamesButton);

        sessionActionsLayout = (LinearLayout)findViewById(R.id.sessionActionsLayout);
        signInButton = (SignInButton)findViewById(R.id.signInButton);
        signOutButton = (Button)findViewById(R.id.signOutButton);

    }

    private void initListeners(){
        multiplayerStartMatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Start Match clicked");
                startMatchClicked();
            }
        });

        multiplayerQuickMatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //quickMatchClicked();
            }
        });
        multiplayerCheckGamesMatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checkMatchesClicked();
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);
        initViews();
        initListeners();

        Intent intent = getIntent();

        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                Log.d(TAG, "Intent no es Null");
                if (extras.containsKey(GameConstants.GAME_INFO)) {
                    gameInfo = extras.getParcelable(GameConstants.GAME_INFO);
                } else {
                    Log.e(TAG, "Error, invalid intent");
                    finish();
                    return;
                }
            } else {
                Log.e(TAG, "Error, null intent");
                finish();
                return;
            }

        }
        if (savedInstanceState != null) {
            gameInfo = savedInstanceState.getParcelable(GameConstants.GAME_INFO);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(GameConstants.GAME_INFO,gameInfo);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void disconnected() {
        super.disconnected();
        Log.d(TAG,"Disconected");
        signInButton.setVisibility(View.VISIBLE);// Put code here to display the sign-in button
        signOutButton.setVisibility(View.GONE);
        multiplayerActionsLayout.setVisibility(View.GONE);
    }

    @Override
    public void connected() {
        super.connected();
        Log.d(TAG, "Connected");
        multiplayerActionsLayout.setVisibility(View.VISIBLE);

    }

    private void startMatchClicked(){
        Intent intent = Games.TurnBasedMultiplayer.getSelectOpponentsIntent(googleApiClient,
                1, 4, true);
        startActivityForResult(intent, RC_SELECT_PLAYERS);
    }
//
//    private void quickMatchClicked(){
//        Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
//                1, 1, 0);
//
//        TurnBasedMatchConfig tbmc = TurnBasedMatchConfig.builder()
//                .setAutoMatchCriteria(autoMatchCriteria).build();
//
//        showSpinner();
//
//        // Start the match
//        ResultCallback<TurnBasedMultiplayer.InitiateMatchResult> cb = new ResultCallback<TurnBasedMultiplayer.InitiateMatchResult>() {
//            @Override
//            public void onResult(TurnBasedMultiplayer.InitiateMatchResult result) {
//                processResult(result);
//            }
//        };
//        Games.TurnBasedMultiplayer.createMatch(googleApiClient, tbmc).setResultCallback(cb);
//    }
//
//
//    private void checkMatchesClicked(){
//        Intent intent = Games.TurnBasedMultiplayer.getInboxIntent(googleApiClient);
//        startActivityForResult(intent, RC_LOOK_AT_MATCHES);
//    }
//
//

//
//
//    private void processResult(TurnBasedMultiplayer.CancelMatchResult result) {
//        dismissSpinner();
//
//        if (!checkStatusCode(null, result.getStatus().getStatusCode())) {
//            return;
//        }
//
//        isDoingTurn = false;
//
//        showWarning("Match",
//                "This match is canceled.  All other players will have their game ended.");
//    }
//

//
//
//    private void processResult(TurnBasedMultiplayer.LeaveMatchResult result) {
//        TurnBasedMatch match = result.getMatch();
//        dismissSpinner();
//        if (!checkStatusCode(match, result.getStatus().getStatusCode())) {
//            return;
//        }
//        isDoingTurn = (match.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN);
//        showWarning("Left", "You've left this match.");
//    }
//
//
//    public void processResult(TurnBasedMultiplayer.UpdateMatchResult result) {
//        TurnBasedMatch match = result.getMatch();
//        dismissSpinner();
//        if (!checkStatusCode(match, result.getStatus().getStatusCode())) {
//            return;
//        }
//        if (match.canRematch()) {
//            askForRematch();
//        }
//
//        isDoingTurn = (match.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN);
//
//        if (isDoingTurn) {
//            updateMatch(match);
//            return;
//        }
//
//        setViewVisibility();
//    }
//
//
//    public void showWarning(String title, String message) {
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//
//        // set title
//        alertDialogBuilder.setTitle(title).setMessage(message);
//
//        // set dialog message
//        alertDialogBuilder.setCancelable(false).setPositiveButton("OK",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int id) {
//                        // if this button is clicked, close
//                        // current activity
//                    }
//                });
//
//        // create alert dialog
//        AlertDialog mAlertDialog = alertDialogBuilder.create();
//
//        // show it
//        mAlertDialog.show();
//    }
//
//    private boolean checkStatusCode(TurnBasedMatch match, int statusCode) {
//        switch (statusCode) {
//            case GamesStatusCodes.STATUS_OK:
//                return true;
//            case GamesStatusCodes.STATUS_NETWORK_ERROR_OPERATION_DEFERRED:
//                // This is OK; the action is stored by Google Play Services and will
//                // be dealt with later.
//                Toast.makeText(
//                        this,
//                        "Stored action for later.  (Please remove this toast before release.)",
//                        Toast.LENGTH_SHORT).show();
//                // NOTE: This toast is for informative reasons only; please remove
//                // it from your final application.
//                return true;
//            case GamesStatusCodes.STATUS_MULTIPLAYER_ERROR_NOT_TRUSTED_TESTER:
//                showErrorMessage(match, statusCode,
//                        R.string.status_multiplayer_error_not_trusted_tester);
//                break;
//            case GamesStatusCodes.STATUS_MATCH_ERROR_ALREADY_REMATCHED:
//                showErrorMessage(match, statusCode,
//                        R.string.match_error_already_rematched);
//                break;
//            case GamesStatusCodes.STATUS_NETWORK_ERROR_OPERATION_FAILED:
//                showErrorMessage(match, statusCode,
//                        R.string.network_error_operation_failed);
//                break;
//            case GamesStatusCodes.STATUS_CLIENT_RECONNECT_REQUIRED:
//                showErrorMessage(match, statusCode,
//                        R.string.client_reconnect_required);
//                break;
//            case GamesStatusCodes.STATUS_INTERNAL_ERROR:
//                showErrorMessage(match, statusCode, R.string.internal_error);
//                break;
//            case GamesStatusCodes.STATUS_MATCH_ERROR_INACTIVE_MATCH:
//                showErrorMessage(match, statusCode,
//                        R.string.match_error_inactive_match);
//                break;
//            case GamesStatusCodes.STATUS_MATCH_ERROR_LOCALLY_MODIFIED:
//                showErrorMessage(match, statusCode,
//                        R.string.match_error_locally_modified);
//                break;
//            default:
//                showErrorMessage(match, statusCode, R.string.unexpected_status);
//                Log.d(TAG, "Did not have warning or string to deal with: "
//                        + statusCode);
//        }
//
//        return false;
//    }
//
//
//    public void showErrorMessage(TurnBasedMatch match, int statusCode,
//                                 int stringId) {
//
//        showWarning("Warning", getResources().getString(stringId));
//    }
//
//

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SELECT_PLAYERS) {
            // Returned from 'Select players to Invite' dialog

            if (resultCode != Activity.RESULT_OK) {
                // user canceled
                return;
            }

            // get the invitee list
            final ArrayList<String> invitees = data
                    .getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);

            // get automatch criteria
            Bundle autoMatchCriteria = null;

            int minAutoMatchPlayers = data.getIntExtra(
                    Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
            int maxAutoMatchPlayers = data.getIntExtra(
                    Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);

            if (minAutoMatchPlayers > 0) {
                autoMatchCriteria = RoomConfig.createAutoMatchCriteria(minAutoMatchPlayers, maxAutoMatchPlayers, 0);
            } else {
                autoMatchCriteria = null;
            }

            TurnBasedMatchConfig tbmc = TurnBasedMatchConfig.builder()
                    .addInvitedPlayers(invitees)
                    .setAutoMatchCriteria(autoMatchCriteria)
                    .build();

            // Start the match
            Games.TurnBasedMultiplayer.createMatch(googleApiClient, tbmc)
                    .setResultCallback(new ResultCallback<TurnBasedMultiplayer.InitiateMatchResult>() {
                        @Override
                        public void onResult(TurnBasedMultiplayer.InitiateMatchResult result) {

                            processResult(result);
                        }
                    });

            showSpinner();
        }

    }

    private void processResult(TurnBasedMultiplayer.InitiateMatchResult result) {
        Status status = result.getStatus();
        if (!status.isSuccess()){
            Log.d(TAG,"Error");
            return;
        }

        TurnBasedMatch match = result.getMatch();

        dismissSpinner();

        if (!checkStatusCode(match, result.getStatus().getStatusCode())) {
            return;
        }

        if (match.getData() != null) {
            // This is a game that has already started, so I'll just start
            //updateMatch(match);
            Log.d(TAG,"TODO: update Match");
            return;
        }
        Log.d(TAG,"TODO: Start Match");


        startMatch(match);
    }

    private void startMatch(TurnBasedMatch match) {


        Log.d(TAG,"startGame");
        Intent intent = new Intent(getApplicationContext(),GameActivity.PUZZLE.getActivityClass());
        intent.putExtra(GameConstants.GAME_INFO, gameInfo);
        intent.putExtra(GameConstants.MULTIPLAYER_MATCH, match);
        Log.d(TAG, "cree todo el intent y el result");
        startActivity(intent);

    }



    public void showSpinner() {
        findViewById(R.id.progressLayout).setVisibility(View.VISIBLE);
    }

    public void dismissSpinner() {
        findViewById(R.id.progressLayout).setVisibility(View.GONE);
    }


    private boolean checkStatusCode(TurnBasedMatch match, int statusCode) {
        switch (statusCode) {
            case GamesStatusCodes.STATUS_OK:
                return true;
            case GamesStatusCodes.STATUS_NETWORK_ERROR_OPERATION_DEFERRED:
                // This is OK; the action is stored by Google Play Services and will
                // be dealt with later.
                Toast.makeText(
                        this,
                        "Stored action for later.  (Please remove this toast before release.)",
                        Toast.LENGTH_SHORT).show();
                // NOTE: This toast is for informative reasons only; please remove
                // it from your final application.
                return true;
            case GamesStatusCodes.STATUS_MULTIPLAYER_ERROR_NOT_TRUSTED_TESTER:
//                showErrorMessage(match, statusCode,
//                        R.string.status_multiplayer_error_not_trusted_tester);
                Log.d(TAG,getString(R.string.status_multiplayer_error_not_trusted_tester));

                break;
            case GamesStatusCodes.STATUS_MATCH_ERROR_ALREADY_REMATCHED:
//                showErrorMessage(match, statusCode,
//                        R.string.match_error_already_rematched);
                Log.d(TAG,getString(R.string.match_error_already_rematched));
                break;
            case GamesStatusCodes.STATUS_NETWORK_ERROR_OPERATION_FAILED:
//                showErrorMessage(match, statusCode,
//                        R.string.network_error_operation_failed);
                Log.d(TAG,getString(R.string.network_error_operation_failed));
                break;
            case GamesStatusCodes.STATUS_CLIENT_RECONNECT_REQUIRED:
//                showErrorMessage(match, statusCode,
//                        R.string.client_reconnect_required);
                Log.d(TAG,getString(R.string.client_reconnect_required));
                break;
            case GamesStatusCodes.STATUS_INTERNAL_ERROR:
//                showErrorMessage(match, statusCode, R.string.internal_error);
                Log.d(TAG,getString(R.string.internal_error));
                break;
            case GamesStatusCodes.STATUS_MATCH_ERROR_INACTIVE_MATCH:
//                showErrorMessage(match, statusCode,
//                        R.string.match_error_inactive_match);
                Log.d(TAG,getString(R.string.match_error_inactive_match));
                break;
            case GamesStatusCodes.STATUS_MATCH_ERROR_LOCALLY_MODIFIED:
//                showErrorMessage(match, statusCode,
//                        R.string.match_error_locally_modified);
                Log.d(TAG,getString(R.string.match_error_locally_modified));
                break;
            case GamesStatusCodes.STATUS_MULTIPLAYER_DISABLED:
                Log.d(TAG,getString(R.string.match_error_multiplayer_disabled));
                break;
            default:
//                showErrorMessage(match, statusCode, R.string.unexpected_status);
                Log.d(TAG,getString(R.string.unexpected_status));
                Log.d(TAG, "Did not have warning or string to deal with: "
                        + statusCode);
        }

        return false;
    }

}
