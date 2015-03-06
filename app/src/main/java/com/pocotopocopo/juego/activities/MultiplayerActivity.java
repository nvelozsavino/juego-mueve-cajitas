package com.pocotopocopo.juego.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.ParticipantResult;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;
import com.pocotopocopo.juego.GameActivity;
import com.pocotopocopo.juego.GameConstants;
import com.pocotopocopo.juego.GameInfo;
import com.pocotopocopo.juego.MultiplayerGameData;
import com.pocotopocopo.juego.MultiplayerMatch;
import com.pocotopocopo.juego.PlayerScore;
import com.pocotopocopo.juego.R;

import java.util.ArrayList;
import java.util.List;


public class MultiplayerActivity extends BaseActivity implements MultiplayerMatch.MultiplayerListener {

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

    private Bundle gameOptions;

    private TurnBasedMatch match;
    private MultiplayerGameData multiplayerGameData;

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

        multiplayerMatch=new MultiplayerMatch(this,this);

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
               if (resultCode != RESULT_OK) {
                   Log.d(TAG,"CREATE GAME back");
                   Games.TurnBasedMultiplayer.cancelMatch(googleApiClient, match.getMatchId())
                           .setResultCallback(multiplayerMatch.cancelMatchCallback);
                   // user canceled
                   return;
               }

               GameInfo gameInfo=data.getParcelableExtra(GameConstants.GAME_INFO);

               multiplayerGameData = new MultiplayerGameData(gameInfo);


               String playerId = Games.Players.getCurrentPlayerId(googleApiClient);
               String myParticipantId = match.getParticipantId(playerId);

               multiplayerMatch.showSpinner();

               Games.TurnBasedMultiplayer.takeTurn(googleApiClient, match.getMatchId(),multiplayerGameData.persist(), myParticipantId)
                       .setResultCallback(multiplayerMatch.takeTurnCallback);

           } else if (requestCode== RC_PLAY_GAME){
               if (resultCode != RESULT_OK) {
                   Log.d(TAG,"PLAY GAME Failed");
                   if (match.getTurnStatus()==TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN) {

                       String nextParticipantId = getNextParticipantId();

                       Games.TurnBasedMultiplayer.leaveMatchDuringTurn(googleApiClient, match.getMatchId(),nextParticipantId)
                               .setResultCallback(multiplayerMatch.leaveMatchDuringTurnCallback);
                   } else {

                       //This shouldn't happen ever
                       Games.TurnBasedMultiplayer.leaveMatch(googleApiClient,match.getMatchId())
                            .setResultCallback(multiplayerMatch.leaveMatchCallback);
                   }
                   // user canceled
                   return;
               }
               String nextParticipantId = getNextParticipantId();
               // Create the next turn

               String playerId = Games.Players.getCurrentPlayerId(googleApiClient);
               String myParticipantId = match.getParticipantId(playerId);

               int movements = data.getIntExtra(GameConstants.WIN_MOVEMENTS,-1);
               long time = data.getLongExtra(GameConstants.WIN_TIME,-1L);
               multiplayerGameData.setScore(myParticipantId, new PlayerScore(movements,time));


               multiplayerMatch.showSpinner();

               //Finish my turn
               Games.TurnBasedMultiplayer.finishMatch(googleApiClient,match.getMatchId())
                       .setResultCallback(multiplayerMatch.finishMatchCallback); //Say that I'm finish with the match


               if (!isGameFinish(match,multiplayerGameData)) { //TODO: check if the game has finished
                   //If is another participant left
                   //send turn
                   Games.TurnBasedMultiplayer.takeTurn(googleApiClient, match.getMatchId(),multiplayerGameData.persist(), nextParticipantId)
                           .setResultCallback(multiplayerMatch.takeTurnCallback);

                   multiplayerGameData = null;
               } else {
                   //All Participants end their games

                   multiplayerMatch.showSpinner();
                   //TODO: Create Participant Result List
                   List<ParticipantResult> results=new ArrayList<>();

                   //I'm the last one who call finish, I have to see who won and finish the game
                   Games.TurnBasedMultiplayer.finishMatch(googleApiClient, match.getMatchId(), multiplayerGameData.persist(), results)
                       .setResultCallback(multiplayerMatch.finishMatchCallback); //TODO: check if this callback should be the same as the previous one
               }

           }

   }


    public static boolean isGameFinish(TurnBasedMatch match,MultiplayerGameData multiplayerGameData){
        List<String> participants=match.getParticipantIds();
        int activeParticipants=0;
        boolean areAllPlayerFinished=true;
        for (String pId: participants){
            int status=match.getParticipantStatus(pId);
            switch (status){
                case Participant.STATUS_INVITED:
                    Log.d(TAG,"Participant invited");
                    return false;
                case Participant.STATUS_JOINED:
                    Log.d(TAG,"Participant joined");
                    return false;
                case Participant.STATUS_DECLINED:
                    Log.d(TAG,"Participant declined");
                    break;
                case Participant.STATUS_FINISHED:
                    Log.d(TAG,"Participant finished");
                    break;
                case Participant.STATUS_LEFT:
                    Log.d(TAG,"Participant left");
                    break;
                case Participant.STATUS_NOT_INVITED_YET:
                    Log.d(TAG,"Participant not invited yet");
                    return false;
                case Participant.STATUS_UNRESPONSIVE:
                    Log.d(TAG,"Participant unrseponsive");
                    break;
            }
        }
        return areAllPlayerFinished;
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

            if (resultCode != RESULT_OK) {
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
                    .setResultCallback(multiplayerMatch.createMatchCallback);

            multiplayerMatch.showSpinner();
        } else if (requestCode == RC_LOOK_AT_MATCHES) {
            // Returning from the 'Select Match' dialog

            if (resultCode != RESULT_OK) {
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
                    Log.d(TAG,"No multiplayerGameData, cancelling");
                    multiplayerMatch.showWarning("Invalid game!","The game was invalid");
                    Games.TurnBasedMultiplayer.cancelMatch(googleApiClient,match.getMatchId())
                            .setResultCallback(multiplayerMatch.cancelMatchCallback);
                    return;
                }

                multiplayerGameData = MultiplayerGameData.unpack(match.getData());

                if (multiplayerGameData.getGameInfo()==null){
                    Log.d(TAG,"No multiplayerGameData, cancelling");
                    multiplayerMatch.showWarning("Invalid game!","The game was invalid");
                    Games.TurnBasedMultiplayer.cancelMatch(googleApiClient,match.getMatchId())
                            .setResultCallback(multiplayerMatch.cancelMatchCallback);
                    return;
                }

                GameInfo gameInfo= multiplayerGameData.getGameInfo();
                Intent intent = new Intent(getApplicationContext(), GameActivity.PUZZLE.getActivityClass());
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

        multiplayerGameData = null;




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
        Games.TurnBasedMultiplayer.rematch(googleApiClient, match.getMatchId())
                .setResultCallback(multiplayerMatch.rematchCallback);
        match = null;
    }

    @Override
    public void updateUI() {

    }

    @Override
    public void showResults() {

    }


}
