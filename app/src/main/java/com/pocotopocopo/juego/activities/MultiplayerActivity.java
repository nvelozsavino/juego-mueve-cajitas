package com.pocotopocopo.juego.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.ParticipantResult;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.pocotopocopo.juego.GameActivity;
import com.pocotopocopo.juego.GameConstants;
import com.pocotopocopo.juego.GameInfo;
import com.pocotopocopo.juego.MultiplayerGameData;
import com.pocotopocopo.juego.MultiplayerMatch;
import com.pocotopocopo.juego.PlayerResult;
import com.pocotopocopo.juego.PlayerScore;
import com.pocotopocopo.juego.R;
import com.pocotopocopo.juego.ResultAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MultiplayerActivity extends BaseActivity implements MultiplayerMatch.MultiplayerListener {

    private final static String TAG="MultiplayerActivity";
    public static final int RC_CREATE_GAME = 4815;
    public static final int RC_PLAY_GAME = 2342;
    private LinearLayout multiplayerActionsLayout;
    private LinearLayout sessionActionsLayout;
    private Button multiplayerStartMatchButton;
    private Button multiplayerQuickMatchButton;
    private Button multiplayerCheckGamesMatchButton;

    private SignInButton signInButton;
    private Button signOutButton;

    private MultiplayerMatch multiplayerMatch;
    private ActivityResult activityResult=null;


//    private GameInfo gameInfo;

    final static int RC_SELECT_PLAYERS = 10000;
    final static int RC_LOOK_AT_MATCHES = 10001;


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

        if (savedInstanceState != null) {
            match=savedInstanceState.getParcelable(Multiplayer.EXTRA_TURN_BASED_MATCH);
            multiplayerGameData=savedInstanceState.getParcelable(GameConstants.MULTIPLAYER_GAME_DATA);

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        outState.putParcelable(GameConstants.GAME_INFO, gameInfo);
        outState.putParcelable(Multiplayer.EXTRA_TURN_BASED_MATCH,match);
        outState.putParcelable(GameConstants.MULTIPLAYER_GAME_DATA,multiplayerGameData);
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

            // Start the match => this will call startMatch function
            Games.TurnBasedMultiplayer.createMatch(googleApiClient, tbmc)
                    .setResultCallback(multiplayerMatch.createMatchCallback);


            multiplayerMatch.showSpinner();

        } else {
            if (googleApiClient!=null && googleApiClient.isConnected()){
                processActivityResult(requestCode,resultCode,data);
            } else {
                activityResult = new ActivityResult(requestCode, resultCode, data);
                googleApiClient.connect();
            }
        }

    }

    private void processActivityResult(int requestCode, int resultCode, Intent data){


        if (requestCode == RC_CREATE_GAME){

            /**
             * Here the match should be set from on create on the savedStateInstance
             * because it comes from the startMatch callback
             */

            if (resultCode != RESULT_OK) {
                Log.d(TAG,"CREATE GAME back");
                Games.TurnBasedMultiplayer.cancelMatch(googleApiClient, match.getMatchId())
                        .setResultCallback(multiplayerMatch.cancelMatchCallback);
                // user canceled
                multiplayerGameData=null;
                match=null;
                return;
            }

            GameInfo gameInfo=data.getParcelableExtra(GameConstants.GAME_INFO);

            multiplayerGameData = new MultiplayerGameData(gameInfo);


            String playerId = Games.Players.getCurrentPlayerId(googleApiClient);
            String myParticipantId = match.getParticipantId(playerId);

            multiplayerMatch.showSpinner();

            /**
             * I'm creating the game, I'm taking the first turn, this call is to set the initial data
             * after that call updateMatch callback will be called
             */
            Games.TurnBasedMultiplayer.takeTurn(googleApiClient, match.getMatchId(),multiplayerGameData.persist(), myParticipantId)
                    .setResultCallback(multiplayerMatch.takeTurnCallback);

        } else if (requestCode== RC_PLAY_GAME){
            /**
             * Here the match should be set from on create on the savedStateInstance
             * because it comes from the updateMatch callback
             */
            if (resultCode != RESULT_OK) {
                Log.d(TAG,"Leaving Game");
                if (match.getTurnStatus()==TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN) {

                    String nextParticipantId = MultiplayerMatch.getNextParticipantId(googleApiClient,match);

                    /**
                     * I'm notifying that I've leaved the game during my turn, it's not going to
                     * trigger any callback inside this activity, only show a message
                     */
                    Games.TurnBasedMultiplayer.leaveMatchDuringTurn(googleApiClient, match.getMatchId(),nextParticipantId)
                            .setResultCallback(multiplayerMatch.leaveMatchDuringTurnCallback);
                } else {

                    //This shouldn't happen ever
                    Games.TurnBasedMultiplayer.leaveMatch(googleApiClient,match.getMatchId())
                            .setResultCallback(multiplayerMatch.leaveMatchCallback);
                }
                // user canceled
                multiplayerGameData=null;
                match=null;
                return;
            }
            Log.d(TAG,"Turn finished");

            // Create the next turn

            String playerId = Games.Players.getCurrentPlayerId(googleApiClient);
            String myParticipantId = match.getParticipantId(playerId);

            int movements = data.getIntExtra(GameConstants.WIN_MOVEMENTS,-1);
            long time = data.getLongExtra(GameConstants.WIN_TIME,-1L);
            if (match.getData()==null){
                Log.e(TAG,"Error, no match data");
                return;

            }
            multiplayerGameData = MultiplayerGameData.unpack(match.getData());
            multiplayerGameData.setScore(new PlayerScore(myParticipantId,movements,time));


            multiplayerMatch.showSpinner();
            /**
             * Say that I'm finish with the match, it should trigger a finishPlayerMatch callback
             */

            /**
             * Check if all other players have finished their turn, if not, then send your result
             * and gave the turn to other player, Finish the game otherwise
             */


            if (!multiplayerGameData.isGameFinished(MultiplayerMatch.getActivePlayers(match))) {
                //If is another participant left
                //send turn
                multiplayerMatch.showSpinner();
                String nextParticipantId = MultiplayerMatch.getNextParticipantId(googleApiClient,match);
                Games.TurnBasedMultiplayer.takeTurn(googleApiClient, match.getMatchId(),multiplayerGameData.persist(), nextParticipantId)
                        .setResultCallback(multiplayerMatch.takeTurnCallback);


            } else {
                //All Participants end their games

                multiplayerMatch.showSpinner();
                List<ParticipantResult> results=multiplayerGameData.getResults(match.getParticipantIds());
                //I'm the last one who call finish, I have to see who won and finish the game
                Games.TurnBasedMultiplayer.finishMatch(googleApiClient, match.getMatchId(), multiplayerGameData.persist(), results)
                        .setResultCallback(multiplayerMatch.finishMatchCallback);
            }




        } else if (requestCode == RC_LOOK_AT_MATCHES) {
            // Returning from the 'Select Match' dialog

            if (resultCode != RESULT_OK) {
                // user canceled
                return;
            }


            final TurnBasedMatch matchReturned = data.getParcelableExtra(Multiplayer.EXTRA_TURN_BASED_MATCH);

            //TODO: The dialog is created but it is horrible
            if (matchReturned != null) {
                /**
                 * Calling the class MultiplayerMatch tho handle this match. Acording to the match,
                 * the callbacks functions startMatch, updateMatch or something else should trigger
                 * this.match should be set in those callbacks
                 */

                final Dialog playLeaveCancelDialog = new Dialog(MultiplayerActivity.this);
                playLeaveCancelDialog.setContentView(R.layout.play_leave_cancel_dialog);
                playLeaveCancelDialog.setCancelable(true);
                Button playIfTurnButton = (Button) playLeaveCancelDialog.findViewById(R.id.playIfTurnButton);
                Button leaveButton = (Button) playLeaveCancelDialog.findViewById(R.id.leaveButton);
                Button cancelButton = (Button) playLeaveCancelDialog.findViewById(R.id.cancelButton);
                TextView whatToShowText = (TextView) playLeaveCancelDialog.findViewById(R.id.whatToShow);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playLeaveCancelDialog.cancel();

                    }
                });

                switch (matchReturned.getStatus()) {
                    case TurnBasedMatch.MATCH_STATUS_ACTIVE:
                    case TurnBasedMatch.MATCH_STATUS_AUTO_MATCHING:
                        int turnStatus = matchReturned.getTurnStatus();
                        switch (turnStatus) {
                            case TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN:
                                playLeaveCancelDialog.setTitle("It's your turn");
                                playIfTurnButton.setVisibility(View.VISIBLE);
                                playIfTurnButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        multiplayerMatch.updateMatch(matchReturned);
                                        playLeaveCancelDialog.cancel();
                                    }
                                });
                                leaveButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Games.TurnBasedMultiplayer.leaveMatchDuringTurn(googleApiClient, matchReturned.getMatchId(), matchReturned.getPendingParticipantId())
                                                .setResultCallback(multiplayerMatch.leaveMatchDuringTurnCallback);
                                        playLeaveCancelDialog.cancel();
                                    }
                                });

                                //mostrar boton de play
                                break;
                            case TurnBasedMatch.MATCH_TURN_STATUS_THEIR_TURN:
                                playIfTurnButton.setVisibility(View.GONE);
                                playLeaveCancelDialog.setTitle("It's not your turn");
                                leaveButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Games.TurnBasedMultiplayer.leaveMatch(googleApiClient, matchReturned.getMatchId())
                                                .setResultCallback(multiplayerMatch.leaveMatchCallback);
                                        playLeaveCancelDialog.cancel();
                                    }
                                });
                                //ocultar boton de play
                                break;
                        }
                        playLeaveCancelDialog.show();
                        break;
                    case TurnBasedMatch.MATCH_STATUS_COMPLETE:

                        showResults(matchReturned);
//                        playLeaveCancelDialog.setTitle("The Game is completed");
//                        StringBuilder stringBuilder = new StringBuilder();
//                        MultiplayerGameData gameData = MultiplayerGameData.unpack(matchReturned.getData());
//                        List<ParticipantResult> results = gameData.getResults(matchReturned.getParticipantIds());
//                        for (ParticipantResult participantResult : results) {
//                            String participantName = matchReturned.getParticipant(participantResult.getParticipantId()).getDisplayName();
//                            stringBuilder.append(participantResult.getPlacing() + "\t" + participantName + "\n");
//                        }
//                        whatToShowText.setText(stringBuilder.toString());
//                        leaveButton.setText("Ok");
//                        leaveButton.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Games.TurnBasedMultiplayer.finishMatch(googleApiClient, matchReturned.getMatchId())
//                                        .setResultCallback(multiplayerMatch.finishPlayerMatchCallback);
//                                playLeaveCancelDialog.cancel();
//                            }
//                        });
//                        playLeaveCancelDialog.show();

                        break;

                }


            }
                /*switch (matchReturned.getStatus()) {
                    case TurnBasedMatch.MATCH_STATUS_ACTIVE: //	Constant returned by getStatus() indicating that the match has started.
                    case TurnBasedMatch.MATCH_STATUS_AUTO_MATCHING: // Constant returned by getStatus() indicating that one or more slots are waiting to be filled by auto-matching.
                        multiplayerMatch.updateMatch(matchReturned);
                        break;
                    case TurnBasedMatch.MATCH_STATUS_CANCELED: // Constant returned by getStatus() indicating that the match was canceled by one of the participants.
                        Log.d(TAG, "Match Canceled");
                        multiplayerMatch.showWarning("Match Canceled", "The game has been canceled");
                    case TurnBasedMatch.MATCH_STATUS_COMPLETE: //Constant returned by getStatus() indicating that the match has finished.
                        Log.d(TAG, "Match Results");
                        Games.TurnBasedMultiplayer.finishMatch(googleApiClient, matchReturned.getMatchId())
                                .setResultCallback(multiplayerMatch.finishPlayerMatchCallback);
                        break;
                    case TurnBasedMatch.MATCH_STATUS_EXPIRED: //Constant returned by getStatus() indicating that the match expired.
                        Log.d(TAG, "Match Expired");
                        multiplayerMatch.showWarning("Match Expired", "The game has expired");
                        break;
                }
                */


            Log.d(TAG, "Match = " + matchReturned);

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
        // Arrive a match, with valid data
        int turnStatus = match.getTurnStatus();

        switch (turnStatus) {
            case TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN:
                if (match.getData()==null){
                    //This should never happen, only for debug malformed games
                    Log.d(TAG,"No multiplayerGameData, cancelling");
                    multiplayerMatch.showWarning("Invalid game!","The game was invalid, no data");
                    Games.TurnBasedMultiplayer.cancelMatch(googleApiClient,match.getMatchId())
                            .setResultCallback(multiplayerMatch.cancelMatchCallback);
                    this.match=null;
                    multiplayerGameData=null;
                    return;
                }

                multiplayerGameData = MultiplayerGameData.unpack(match.getData());
                GameInfo gameInfo= multiplayerGameData.getGameInfo();

                if (gameInfo==null){
                    //This should never happen, only for debug malformed games where gameInfo is invalid
                    Log.d(TAG,"No multiplayerGameData, cancelling");
                    multiplayerMatch.showWarning("Invalid game!","The game was invalid, no game info");
                    Games.TurnBasedMultiplayer.cancelMatch(googleApiClient,match.getMatchId())
                            .setResultCallback(multiplayerMatch.cancelMatchCallback);
                    this.match=null;
                    multiplayerGameData=null;
                    return;
                }
                this.match=match;
                String playerId = Games.Players.getCurrentPlayerId(googleApiClient);
                String myParticipantId = match.getParticipantId(playerId);
                multiplayerGameData.setScore(new PlayerScore(myParticipantId));
                Intent intent = new Intent(getApplicationContext(), GameActivity.PUZZLE.getActivityClass());
                intent.putExtra(GameConstants.IS_MULTIPLAYER,true);
                intent.putExtra(GameConstants.GAME_INFO, gameInfo);
                Log.d(TAG, "cree todo el intent y el result");
                startActivityForResult(intent, RC_PLAY_GAME);
                return;
            case TurnBasedMatch.MATCH_TURN_STATUS_THEIR_TURN:
                //TODO: Create a better message for this
                /**
                 * If it is not your turn the code shouldn't allow user to get here if a dialog
                 * asking what to do with the game is made, however it could be the case
                 */
                // Should return results. -> ??? is from the google's example
                multiplayerMatch.showWarning("Alas...", "It's not your turn.");
                break;
            case TurnBasedMatch.MATCH_TURN_STATUS_INVITED:
                //TODO: Create a better message for this
                /**
                 * This is where the match is waiting for someone to accept the invitation.
                 * if a dialog asking what to do with the game is made the code shouldn't allow
                 * users to get here.
                 */

                //I think this is when you invited people, and are waiting?
                multiplayerMatch.showWarning("Good inititative!", "Still waiting for invitations.\n\nBe patient!");
                break;
        }
        /**
         * if gets here, it wasn't your turn or your status is invited?
         */
        this.match=null;
        multiplayerGameData=null;





    }

    @Override
    public void startMatch(TurnBasedMatch match) {
        this.match=match;
        Log.d(TAG,"startGame");
        Intent intent = new Intent(getApplicationContext(),GameActivity.CREATE_GAME.getActivityClass());
        intent.putExtra(GameConstants.IS_MULTIPLAYER,true);
        Log.d(TAG, "cree todo el intent y el result");
        startActivityForResult(intent, RC_CREATE_GAME);
    }

    @Override
    public void finishPlayerMatch(TurnBasedMatch match) {
        /**
         * This should come from a call from finishPlayer after a puzzle is completed, here the
         * multiplayerGameData should contain the score of the player.
         * The spinner should prevent user doing something else
         */
//

//        if (!MultiplayerMatch.isGameFinish(match)) {
//            //If is another participant left
//            //send turn
//            multiplayerMatch.showSpinner();
//            String nextParticipantId = MultiplayerMatch.getNextParticipantId(googleApiClient,match);
//            Games.TurnBasedMultiplayer.takeTurn(googleApiClient, match.getMatchId(),multiplayerGameData.persist(), nextParticipantId)
//                    .setResultCallback(multiplayerMatch.takeTurnCallback);
//
//
//        } else {
//            //All Participants end their games
//
//            multiplayerMatch.showSpinner();
//            List<ParticipantResult> results=multiplayerGameData.getResults(match.getParticipantIds());
//            //I'm the last one who call finish, I have to see who won and finish the game
//            Games.TurnBasedMultiplayer.finishMatch(googleApiClient, match.getMatchId(), multiplayerGameData.persist(), results)
//                    .setResultCallback(multiplayerMatch.finishMatchCallback);
//        }
        showResults(match);
        multiplayerGameData = null;
        this.match=null;
        Log.d(TAG,"Match finished for this player");
    }

    @Override
    public void finishMatch(TurnBasedMatch match) {
        /**
         * If gets here, is because you call finishMatch and all the results should be available
         * multiplayerGameData and match should be null
         */

        showResults(match);
        multiplayerGameData = null;
        this.match=null;
        Log.d(TAG,"Match finished for all player");




    }

    private void showResults(final TurnBasedMatch match){

        //TODO: Create a Dialog with all the results, the messages and buttons

        List<String> participantIds =match.getParticipantIds();
        String playerId = Games.Players.getCurrentPlayerId(googleApiClient);
        String myParticipantId = match.getParticipantId(playerId);
        if (match.getData()==null){
            Log.e(TAG, "Error, no data in match");
            return;
        }
        MultiplayerGameData gameData=MultiplayerGameData.unpack(match.getData());


        PlayerScore myScore=gameData.getScore(myParticipantId);
        if (myScore!=null) {
            //multiplayerMatch.showWarning("Result", "You did " + myScore.getMovements() + " movements in " + myScore.getTime() + " time");
        }

        List<PlayerResult> playerResultList = new ArrayList<>();
        for (String participantId: participantIds) {
            PlayerResult playerResult = new PlayerResult(match, participantId);
            playerResultList.add(playerResult);
        }
        Collections.sort(playerResultList,new PlayerResult.ResultComparator());
        final Dialog  resultDialog = new Dialog(this);
        resultDialog.setCancelable(true);
        resultDialog.setContentView(R.layout.results_layout);
        resultDialog.setTitle(R.string.result_title);
        ListView listView = (ListView) resultDialog.findViewById(R.id.lstResults);
        Button btnRematch = (Button)resultDialog.findViewById(R.id.btnRematch);
        Button btnExit = (Button)resultDialog.findViewById(R.id.btnExit);
        ResultAdapter adapter = new ResultAdapter(this,playerResultList);
        listView.setAdapter(adapter);
        if (match.canRematch()) {
            btnRematch.setVisibility(View.VISIBLE);
            btnRematch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    multiplayerMatch.askForRematch(match);
                    resultDialog.cancel();
                }
            });

        } else {
            btnRematch.setVisibility(View.GONE);
        }
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultDialog.cancel();
            }
        });



        resultDialog.show();


//            Participant participant = match.getParticipant(participantId);
//
//
//            ParticipantResult participantResult = match.getParticipant(participantId).getResult();
//            if (participantResult!=null && participantId==myParticipantId){
//                //this is my result
//                switch (participantResult.getResult()) {
//                    case ParticipantResult.MATCH_RESULT_WIN:
//                        Log.d(TAG, "You win");
//                        multiplayerMatch.showWarning("Result", "You Win");
//                        break;
//                    case ParticipantResult.MATCH_RESULT_TIE:
//                        Log.d(TAG, "You Tied");
//                        multiplayerMatch.showWarning("Result", "You Tied");
//                        break;
//                    case ParticipantResult.MATCH_RESULT_LOSS:
//                        Log.d(TAG, "You Loose");
//                        multiplayerMatch.showWarning("Result", "You Loose");
//                        break;
//                    default:
//                        Log.d(TAG, "Don't know the results.");
//                        multiplayerMatch.showWarning("Result", "Don't know???");
//                        break;
//                }
//            }
//        }

        if (match.canRematch()) { //Indicate that the game has finished
            //TODO: check if the rematch is working properly
            //multiplayerMatch.askForRematch(match);
        }
    }

    @Override
    public void rematch(TurnBasedMatch match) {
        multiplayerMatch.showSpinner();
        Games.TurnBasedMultiplayer.rematch(googleApiClient, match.getMatchId())
                .setResultCallback(multiplayerMatch.rematchCallback);
        this.match=null; //is going to be a new game
    }

    @Override
    public void updateUI() {

    }

    @Override
    public void finishAndTakeTurn(TurnBasedMatch match) {
            Games.TurnBasedMultiplayer.finishMatch(googleApiClient,match.getMatchId())
                    .setResultCallback(multiplayerMatch.finishPlayerMatchCallback);
    }


}
