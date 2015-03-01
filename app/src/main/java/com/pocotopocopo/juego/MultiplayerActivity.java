package com.pocotopocopo.juego;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import java.util.ArrayList;


public class MultiplayerActivity extends BaseActivity {

    private final static String TAG="MultiplayerActivity";
    public static final int RC_CREATE_GAME = 4815;
    public static final int RC_PLAY_GAME = 2342;
    private LinearLayout multiplayerActionsLayout;
    private Button multiplayerStartMatchButton;
    private Button multiplayerQuickMatchButton;
    private Button multiplayerCheckGamesMatchButton;

    private LinearLayout sessionActionsLayout;
    private SignInButton signInButton;
    private Button signOutButton;

    private MultiplayerMatch multiplayerMatch;

//    private GameInfo gameInfo;

    final static int RC_SELECT_PLAYERS = 10000;
    final static int RC_LOOK_AT_MATCHES = 10001;

    public boolean isDoingTurn = false;
    private Bundle gameOptions;

    private TurnBasedMatch match;
    private GameData gameData;

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
                checkMatchesClicked();
            }
        });

    }

    private void checkMatchesClicked() {
        Intent intent = Games.TurnBasedMultiplayer.getInboxIntent(googleApiClient);
        startActivityForResult(intent, RC_LOOK_AT_MATCHES);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multiplayer_activity_layout);
        initViews();
        initListeners();

        multiplayerMatch=new MultiplayerMatch(this,multiplayerListener);

        Intent intent = getIntent();

//        if (intent != null) {
//            Bundle extras = intent.getExtras();
//            if (extras != null) {
//                Log.d(TAG, "Intent no es Null");
//                if (extras.containsKey(GameConstants.GAME_INFO)) {
//                    gameInfo = extras.getParcelable(GameConstants.GAME_INFO);
//                } else {
//                    Log.e(TAG, "Error, invalid intent");
//                    finish();
//                    return;
//                }
//            } else {
//                Log.e(TAG, "Error, null intent");
//                finish();
//                return;
//            }
//
//        }
        if (savedInstanceState != null) {
//            gameInfo = savedInstanceState.getParcelable(GameConstants.GAME_INFO);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        outState.putParcelable(GameConstants.GAME_INFO, gameInfo);
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

   private void processActivityResult(int requestCode, int resultCode, Intent data){


           if (requestCode == RC_CREATE_GAME){
               googleApiClient.connect();
               if (resultCode != Activity.RESULT_OK) {
                   Log.d(TAG,"CREATE GAME back");
                   Games.TurnBasedMultiplayer.cancelMatch(googleApiClient, match.getMatchId());
                   // user canceled
                   return;
               }

               GameInfo gameInfo=data.getParcelableExtra(GameConstants.GAME_INFO);

               gameData = new GameData(gameInfo);
               // Some basic turn data



               String playerId = Games.Players.getCurrentPlayerId(googleApiClient);
               String myParticipantId = match.getParticipantId(playerId);

               multiplayerMatch.showSpinner();

               Games.TurnBasedMultiplayer.takeTurn(googleApiClient, match.getMatchId(),
                       gameData.persist(), myParticipantId).setResultCallback(
                       new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                           @Override
                           public void onResult(TurnBasedMultiplayer.UpdateMatchResult result) {
                               multiplayerMatch.processResult(result);
                           }
                       });

           } else if (requestCode== RC_PLAY_GAME){
               if (resultCode != Activity.RESULT_OK) {
                   Log.d(TAG,"PLAY GAME Failed");
                   if (match.getTurnStatus()==TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN) {

                       String nextParticipantId = getNextParticipantId();

                       Games.TurnBasedMultiplayer.leaveMatchDuringTurn(googleApiClient, match.getMatchId(),
                               nextParticipantId).setResultCallback(
                               new ResultCallback<TurnBasedMultiplayer.LeaveMatchResult>() {
                                   @Override
                                   public void onResult(TurnBasedMultiplayer.LeaveMatchResult result) {
                                       multiplayerMatch.processResult(result);
                                   }
                               });
                   } else {
                       Games.TurnBasedMultiplayer.leaveMatch(googleApiClient,match.getMatchId());
                   }
                   // user canceled
                   return;
               }
               String nextParticipantId = getNextParticipantId();
               // Create the next turn

               int movements = data.getIntExtra(GameConstants.WIN_MOVEMENTS,-1);
               long time = data.getLongExtra(GameConstants.WIN_TIME,-1L);

               //TODO: update game data with last game info

               multiplayerMatch.showSpinner();

               if (true) { //TODO: check if the game has finished
                   Games.TurnBasedMultiplayer.takeTurn(googleApiClient, match.getMatchId(),
                           gameData.persist(), nextParticipantId).setResultCallback(
                           new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                               @Override
                               public void onResult(TurnBasedMultiplayer.UpdateMatchResult result) {
                                   multiplayerMatch.processResult(result);
                               }
                           });

                   gameData = null;
               } else {
                   multiplayerMatch.showSpinner();
                   Games.TurnBasedMultiplayer.finishMatch(googleApiClient, match.getMatchId())
                           .setResultCallback(new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                               @Override
                               public void onResult(TurnBasedMultiplayer.UpdateMatchResult result) {
                                   multiplayerMatch.processResult(result);
                               }
                           });

                   isDoingTurn = false;

               }

           }

   }


    @Override
    public void connected() {
        super.connected();
        if (activityResult!=null){
            int requestCode=activityResult.requestCode;
            int resultCode=activityResult.resultCode;
            Intent data=activityResult.data;
            processActivityResult(requestCode,resultCode,data);
        }
        activityResult=null;

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

    private class ActivityResult{
        public int requestCode, resultCode;
        public Intent data;
        public ActivityResult(int requestCode, int resultCode, Intent data){
            this.requestCode=requestCode;
            this.resultCode=resultCode;
            this.data=data;
        }
    }

    private ActivityResult activityResult=null;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        activityResult=null;
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

                            multiplayerMatch.processResult(result);
                        }
                    });

            multiplayerMatch.showSpinner();
        } else if (requestCode == RC_LOOK_AT_MATCHES) {
            // Returning from the 'Select Match' dialog

            if (resultCode != Activity.RESULT_OK) {
                // user canceled
                return;
            }

            TurnBasedMatch match = data.getParcelableExtra(Multiplayer.EXTRA_TURN_BASED_MATCH);
            //TODO: Open a dialog to ask what to do with that match (Play if Turn, Leave, Cancel)
            if (match != null) {
                multiplayerMatch.updateMatch(match);
            }

            Log.d(TAG, "Match = " + match);
        } else {
            if (googleApiClient!=null && googleApiClient.isConnected()){
                processActivityResult(requestCode,resultCode,data);
            } else {
                activityResult = new ActivityResult(requestCode, resultCode, data);
                googleApiClient.connect();
            }
        }

    }

    public String getNextParticipantId() {

        String playerId = Games.Players.getCurrentPlayerId(googleApiClient);
        String myParticipantId = match.getParticipantId(playerId);

        ArrayList<String> participantIds = match.getParticipantIds();

        int desiredIndex = -1;

        for (int i = 0; i < participantIds.size(); i++) {
            if (participantIds.get(i).equals(myParticipantId)) {
                desiredIndex = i + 1;
            }
        }

        if (desiredIndex < participantIds.size()) {
            return participantIds.get(desiredIndex);
        }

        if (match.getAvailableAutoMatchSlots() <= 0) {
            // You've run out of automatch slots, so we start over.
            return participantIds.get(0);
        } else {
            // You have not yet fully automatched, so null will find a new
            // person to play against.
            return null;
        }
    }


    private MultiplayerMatch.MultiplayerListener multiplayerListener = new MultiplayerMatch.MultiplayerListener() {
        @Override
        public void showSpinner() {
            findViewById(R.id.progressLayout).setVisibility(View.VISIBLE);
        }

        @Override
        public void dismissSpinner() {
            findViewById(R.id.progressLayout).setVisibility(View.GONE);
        }

        @Override
        public void updateMatch(TurnBasedMatch match) {
            MultiplayerActivity.this.match=match;
            int status = match.getStatus();
            int turnStatus = match.getTurnStatus();

            switch (turnStatus) {
                case TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN:
                    if (match.getData()==null){
                        Log.d(TAG,"No gameData, cancelling");
                        multiplayerMatch.showWarning("Invalid game!","The game was invalid");
                        Games.TurnBasedMultiplayer.cancelMatch(googleApiClient,match.getMatchId());
                        return;
                    }

                    gameData=GameData.unpack(match.getData());

                    GameInfo gameInfo=gameData.getGameInfo();
                    Intent intent = new Intent(getApplicationContext(),GameActivity.PUZZLE.getActivityClass());
                    intent.putExtra(GameConstants.IS_MULTIPLAYER,true);
                    intent.putExtra(GameConstants.GAME_INFO, gameInfo);
                    Log.d(TAG, "cree todo el intent y el result");
                    startActivityForResult(intent, RC_PLAY_GAME);
                    return;
                case TurnBasedMatch.MATCH_TURN_STATUS_THEIR_TURN:
                    // Should return results.
                    multiplayerMatch.showWarning("Alas...", "It's not your turn.");
                    break;
                case TurnBasedMatch.MATCH_TURN_STATUS_INVITED:
                    multiplayerMatch.showWarning("Good inititative!",
                            "Still waiting for invitations.\n\nBe patient!");
            }

            gameData = null;




        }

        @Override
        public void startMatch(TurnBasedMatch match) {
            MultiplayerActivity.this.match=match;
            Log.d(TAG,"startGame");
            Intent intent = new Intent(getApplicationContext(),GameActivity.CREATE_GAME.getActivityClass());
            intent.putExtra(GameConstants.IS_MULTIPLAYER,true);
//        intent.putExtra(GameConstants.GAME_INFO, gameInfo);
            //intent.putExtra(GameConstants.MULTIPLAYER_MATCH, match);
            Log.d(TAG, "cree todo el intent y el result");
            startActivityForResult(intent, RC_CREATE_GAME);
        }

        @Override
        public void rematch() {
            showSpinner();
            Games.TurnBasedMultiplayer.rematch(googleApiClient, match.getMatchId()).setResultCallback(
                    new ResultCallback<TurnBasedMultiplayer.InitiateMatchResult>() {
                        @Override
                        public void onResult(TurnBasedMultiplayer.InitiateMatchResult result) {
                            multiplayerMatch.processResult(result);
                        }
                    });
            match = null;
            isDoingTurn = false;
        }

        @Override
        public void updateUI() {

        }
    };





}
