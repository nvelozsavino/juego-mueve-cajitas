package com.pocotopocopo.juego;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;

/**
 * Created by nico on 28/02/15.
 */
public class MultiplayerMatch {
    private static String TAG="MultiplayerMatch";
    private Context context;
    private boolean isDoingTurn;
    public interface MultiplayerListener {
        void showSpinner();
        void dismissSpinner();
        void updateMatch(TurnBasedMatch match);
        void startMatch(TurnBasedMatch match);
        void rematch();
        void updateUI();
    }
    private MultiplayerListener multiplayerListener;

    public void showSpinner(){
        if (multiplayerListener!=null){
            multiplayerListener.showSpinner();
        }
    }

    public void dismissSpinner(){
        if (multiplayerListener!=null){
            multiplayerListener.dismissSpinner();
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

    public void  processResult(TurnBasedMultiplayer.InitiateMatchResult result) {
        Status status = result.getStatus();
        if (!status.isSuccess()){
            Log.d(TAG,"Error");
            return;
        }

        TurnBasedMatch match = result.getMatch();

        multiplayerListener.dismissSpinner();

        if (!checkStatusCode(match, result.getStatus().getStatusCode())) {
            return;
        }

        if (match.getData() != null) {
            // This is a game that has already started, so I'll just start
            multiplayerListener.updateMatch(match);
            Log.d(TAG,"TODO: update Match");
            return;
        }
        Log.d(TAG,"TODO: Start Match");


        multiplayerListener.startMatch(match);
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


    public void processResult(TurnBasedMultiplayer.CancelMatchResult result) {
        dismissSpinner();

        if (!checkStatusCode(null, result.getStatus().getStatusCode())) {
            return;
        }

        isDoingTurn = false;

        showWarning("Match", "This match is canceled.  All other players will have their game ended.");
    }




    public void processResult(TurnBasedMultiplayer.LeaveMatchResult result) {
        TurnBasedMatch match = result.getMatch();
        dismissSpinner();
        if (!checkStatusCode(match, result.getStatus().getStatusCode())) {
            return;
        }
        isDoingTurn = false;//(match.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN);
        showWarning("Left", "You've left this match.");
    }


    public void processResult(TurnBasedMultiplayer.UpdateMatchResult result) {
        TurnBasedMatch match = result.getMatch();
        dismissSpinner();
        if (!checkStatusCode(match, result.getStatus().getStatusCode())) {
            return;
        }
        if (match.canRematch()) {
            askForRematch();
        }

        isDoingTurn = (match.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN);

        if (isDoingTurn) {
            updateMatch(match);
            return;
        }
        updateUI();
//        setViewVisibility();
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
                                multiplayerListener.rematch();
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





}
