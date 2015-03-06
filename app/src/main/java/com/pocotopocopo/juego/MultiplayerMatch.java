package com.pocotopocopo.juego;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;

/**
 * Created by nico on 28/02/15.
 */
public class MultiplayerMatch {
    private static String TAG="MultiplayerMatch";
    private Context context;

    private boolean isSpinning = false;
    //private boolean isDoingTurn;



    public interface MultiplayerListener {
        void showSpinner();
        void dismissSpinner();
        void updateMatch(TurnBasedMatch match);
        void startMatch(TurnBasedMatch match);
        void rematch();
        void updateUI();
        void showResults();
    }
    private MultiplayerListener multiplayerListener;

    public void showResults(){
        if (multiplayerListener!=null){
            multiplayerListener.showResults();
        }
    }

    public void showSpinner(){
        isSpinning=true;
        if (multiplayerListener!=null){
            multiplayerListener.showSpinner();
        }
    }

    public void dismissSpinner(){
        if (isSpinning) {
            isSpinning = false;
            if (multiplayerListener != null) {
                multiplayerListener.dismissSpinner();
            }
        }
    }


    public void startMatch(TurnBasedMatch match){
        if (multiplayerListener!=null){
            multiplayerListener.startMatch(match);
        }
    }

    public void rematch(){
        if (multiplayerListener!=null){
            multiplayerListener.rematch();
        }
    }

    public void updateUI(){
        if (multiplayerListener!=null){
            multiplayerListener.updateUI();
        }
    }



    public MultiplayerMatch(Context context, MultiplayerListener multiplayerListener){
        this.context=context;
        this.multiplayerListener = multiplayerListener;
    }



    public boolean checkStatusCode(TurnBasedMatch match, int statusCode) {
        switch (statusCode) {
            case GamesStatusCodes.STATUS_OK:
                return true;
            case GamesStatusCodes.STATUS_NETWORK_ERROR_OPERATION_DEFERRED:
                // This is OK; the action is stored by Google Play Services and will
                // be dealt with later.
                Toast.makeText(
                        context,
                        "Stored action for later.  (Please remove this toast before release.)",
                        Toast.LENGTH_SHORT).show();
                // NOTE: This toast is for informative reasons only; please remove
                // it from your final application.
                return true;
            case GamesStatusCodes.STATUS_MULTIPLAYER_ERROR_NOT_TRUSTED_TESTER:
                showErrorMessage(match, statusCode, R.string.status_multiplayer_error_not_trusted_tester);
                Log.d(TAG, context.getString(R.string.status_multiplayer_error_not_trusted_tester));

                break;
            case GamesStatusCodes.STATUS_MATCH_ERROR_ALREADY_REMATCHED:
                showErrorMessage(match, statusCode, R.string.match_error_already_rematched);
                Log.d(TAG,context.getString(R.string.match_error_already_rematched));
                break;
            case GamesStatusCodes.STATUS_NETWORK_ERROR_OPERATION_FAILED:
                showErrorMessage(match, statusCode,R.string.network_error_operation_failed);
                Log.d(TAG,context.getString(R.string.network_error_operation_failed));
                break;
            case GamesStatusCodes.STATUS_CLIENT_RECONNECT_REQUIRED:
                showErrorMessage(match, statusCode,R.string.client_reconnect_required);
                Log.d(TAG,context.getString(R.string.client_reconnect_required));
                break;
            case GamesStatusCodes.STATUS_INTERNAL_ERROR:
                showErrorMessage(match, statusCode, R.string.internal_error);
                Log.d(TAG,context.getString(R.string.internal_error));
                break;
            case GamesStatusCodes.STATUS_MATCH_ERROR_INACTIVE_MATCH:
                showErrorMessage(match, statusCode,R.string.match_error_inactive_match);
                Log.d(TAG,context.getString(R.string.match_error_inactive_match));
                break;
            case GamesStatusCodes.STATUS_MATCH_ERROR_LOCALLY_MODIFIED:
                showErrorMessage(match, statusCode, R.string.match_error_locally_modified);
                Log.d(TAG,context.getString(R.string.match_error_locally_modified));
                break;
            case GamesStatusCodes.STATUS_MULTIPLAYER_DISABLED:
                Log.d(TAG,context.getString(R.string.match_error_multiplayer_disabled));
                break;
            default:
                showErrorMessage(match, statusCode, R.string.unexpected_status);
                Log.d(TAG,context.getString(R.string.unexpected_status));
                Log.d(TAG, "Did not have warning or string to deal with: "
                        + statusCode);
        }

        return false;
    }


    public void askForRematch() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder.setMessage("Do you want a rematch?");

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Sure, rematch!",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                rematch();
                            }
                        })
                .setNegativeButton("No.",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });

        alertDialogBuilder.show();
    }


    public void showWarning(String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set title
        alertDialogBuilder.setTitle(title).setMessage(message);

        // set dialog message
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                    }
                });

        // create alert dialog
        AlertDialog mAlertDialog = alertDialogBuilder.create();

        // show it
        mAlertDialog.show();
    }

    public void showErrorMessage(TurnBasedMatch match, int statusCode, int stringId) {
        showWarning("Warning", context.getResources().getString(stringId));
    }

    public void updateMatch(TurnBasedMatch match) {

        int status = match.getStatus();
        int turnStatus = match.getTurnStatus();

        switch (status) {
            case TurnBasedMatch.MATCH_STATUS_CANCELED:
                showWarning("Canceled!", "This game was canceled!");
                return;
            case TurnBasedMatch.MATCH_STATUS_EXPIRED:
                showWarning("Expired!", "This game is expired.  So sad!");
                return;
            case TurnBasedMatch.MATCH_STATUS_AUTO_MATCHING:
                showWarning("Waiting for auto-match...",
                        "We're still waiting for an automatch partner.");
                return;
            case TurnBasedMatch.MATCH_STATUS_COMPLETE:
                if (turnStatus == TurnBasedMatch.MATCH_TURN_STATUS_COMPLETE) {
                    showWarning(
                            "Complete!",
                            "This game is over; someone finished it, and so did you!  There is nothing to be done.");
                    break;
                }

                // Note that in this state, you must still call "Finish" yourself,
                // so we allow this to continue.
                showWarning("Complete!",
                        "This game is over; someone finished it!  You can only finish it now.");
        }

        if (multiplayerListener!=null) {
            multiplayerListener.updateMatch(match);
        }
        // OK, it's active. Check on turn status.
    }

    /***
     * CALLBACKS
     */

    /***
     * Cancel Match
     */
    public ResultCallback<TurnBasedMultiplayer.CancelMatchResult> cancelMatchCallback = new ResultCallback<TurnBasedMultiplayer.CancelMatchResult>() {
        @Override
        public void onResult(TurnBasedMultiplayer.CancelMatchResult cancelMatchResult) {
            processCancelMatchResult(cancelMatchResult);
        }
    };

    private void processCancelMatchResult(TurnBasedMultiplayer.CancelMatchResult cancelMatchResult) {
        dismissSpinner();

        if (!checkStatusCode(null, cancelMatchResult.getStatus().getStatusCode())) {
            return;
        }
        //isDoingTurn = false;
        showWarning("Match", "This match is canceled.  All other players will have their game ended.");
    }

    /***
     * Take Turn
     */
    public ResultCallback<TurnBasedMultiplayer.UpdateMatchResult> takeTurnCallback = new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
        @Override
        public void onResult(TurnBasedMultiplayer.UpdateMatchResult takeTurnResult) {
            processTakeTurnResult(takeTurnResult);
        }
    };

    public void processTakeTurnResult(TurnBasedMultiplayer.UpdateMatchResult takeTurnResult) {
        TurnBasedMatch match = takeTurnResult.getMatch();
        dismissSpinner();
        if (!checkStatusCode(match, takeTurnResult.getStatus().getStatusCode())) {
            return;
        }
        if (match.canRematch()) {
            askForRematch();
        }

        boolean isMyTurn = (match.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN);

        if (isMyTurn) {
            //if is my turn => go to the game
            updateMatch(match);
        } else {
            //Call update UI in case of you want to show that is someone turn
            updateUI();
        }
//        setViewVisibility();
    }

    /***
     * Leave Match During Turn
     */
    public ResultCallback<TurnBasedMultiplayer.LeaveMatchResult> leaveMatchDuringTurnCallback = new ResultCallback<TurnBasedMultiplayer.LeaveMatchResult>() {
        @Override
        public void onResult(TurnBasedMultiplayer.LeaveMatchResult leaveMatchDuringTurnResult) {
            processLeaveMatchDuringTurnResult(leaveMatchDuringTurnResult);
        }
    };

    public void processLeaveMatchDuringTurnResult(TurnBasedMultiplayer.LeaveMatchResult leaveMatchDuringTurnResult) {
        processLeaveMatchResult(leaveMatchDuringTurnResult); //is the same as LeaveMatch
    }

    /***
     * Finish Match
     */
    public ResultCallback<TurnBasedMultiplayer.UpdateMatchResult> finishMatchCallback= new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
        @Override
        public void onResult(TurnBasedMultiplayer.UpdateMatchResult finishMatchResult) {
            processFinishMatchResult(finishMatchResult);
        }
    };

    public void processFinishMatchResult(TurnBasedMultiplayer.UpdateMatchResult finishMatchResult) {
        dismissSpinner();
        TurnBasedMatch match = finishMatchResult.getMatch();
        if (!checkStatusCode(match, finishMatchResult.getStatus().getStatusCode())) {
            return;
        }
        showWarning("Finish", "The game has ended for all the participants");
        //TODO: call results
        showResults();

    }


    /***
     * Finish Player Match
     */
    public ResultCallback<TurnBasedMultiplayer.UpdateMatchResult> finishPlayerMatchCallback= new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
        @Override
        public void onResult(TurnBasedMultiplayer.UpdateMatchResult finishPlayerMatchResult) {
            processFinishPlayerMatchResult(finishPlayerMatchResult);
        }
    };

    public void processFinishPlayerMatchResult(TurnBasedMultiplayer.UpdateMatchResult finishPlayerMatchResult) {
        dismissSpinner();
        TurnBasedMatch match = finishPlayerMatchResult.getMatch();
        if (!checkStatusCode(match, finishPlayerMatchResult.getStatus().getStatusCode())) {
            return;
        }
        showWarning("Finish", "You finish your game");

    }

    /***
     * Leave Match (no turn)
     */
    public ResultCallback<TurnBasedMultiplayer.LeaveMatchResult> leaveMatchCallback = new ResultCallback<TurnBasedMultiplayer.LeaveMatchResult>() {
        @Override
        public void onResult(TurnBasedMultiplayer.LeaveMatchResult leaveMatchResult) {
            processLeaveMatchResult(leaveMatchResult);
        }
    };

    private void processLeaveMatchResult(TurnBasedMultiplayer.LeaveMatchResult leaveMatchResult) {
        dismissSpinner();
        TurnBasedMatch match = leaveMatchResult.getMatch();
        if (!checkStatusCode(match, leaveMatchResult.getStatus().getStatusCode())) {
            return;
        }
        //isDoingTurn = false;//(match.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN);
        showWarning("Left", "You've left this match.");
    }


    /***
     * Rematch
     */
    public ResultCallback<TurnBasedMultiplayer.InitiateMatchResult> rematchCallback = new ResultCallback<TurnBasedMultiplayer.InitiateMatchResult>() {
        @Override
        public void onResult(TurnBasedMultiplayer.InitiateMatchResult rematchResult) {
        processRematchResult(rematchResult);
        }
    };

    private void processRematchResult(TurnBasedMultiplayer.InitiateMatchResult rematchResult) {
        processCreateMatch(rematchResult); //Is the same as Create match
    }

    /***
     * Create Match
     */

    public ResultCallback<TurnBasedMultiplayer.InitiateMatchResult> createMatchCallback = new ResultCallback<TurnBasedMultiplayer.InitiateMatchResult>() {
        @Override
        public void onResult(TurnBasedMultiplayer.InitiateMatchResult createMatchResult) {
            processCreateMatch(createMatchResult);
        }
    };

    public void processCreateMatch(TurnBasedMultiplayer.InitiateMatchResult createMatchResult) {
        dismissSpinner();
        Status status = createMatchResult.getStatus();
        if (!status.isSuccess()){
            Log.d(TAG,"Error");
            return;
        }

        TurnBasedMatch match = createMatchResult.getMatch();
        if (!checkStatusCode(match, createMatchResult.getStatus().getStatusCode())) {
            return;
        }

        if (match.getData() != null) {
            // This is a game that has already started, so I'll just start
            updateMatch(match);
            Log.d(TAG,"TODO: update Match");
        } else { //I have to start a new match
            Log.d(TAG, "TODO: Start Match");
            startMatch(match);
        }
    }

   





}
