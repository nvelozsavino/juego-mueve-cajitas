package com.pocotopocopo.juego;

import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.games.multiplayer.turnbased.OnTurnBasedMatchUpdateReceivedListener;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;

/**
 * Created by nico on 2/03/15.
 */
public class MultiplayerUpdateListener implements OnTurnBasedMatchUpdateReceivedListener {

    private Context context;

    public MultiplayerUpdateListener(Context context){
        this.context=context;
    }
    @Override
    public void onTurnBasedMatchReceived(TurnBasedMatch match) {
        switch (match.getStatus()){
            case TurnBasedMatch.MATCH_STATUS_ACTIVE:
                Toast.makeText(context,"Match active",Toast.LENGTH_SHORT).show();
                break;
            case TurnBasedMatch.MATCH_STATUS_COMPLETE:
                Toast.makeText(context,"Match Complete",Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(context,"Otro",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onTurnBasedMatchRemoved(String s) {
        Toast.makeText(context,"Match removed",Toast.LENGTH_SHORT).show();

    }
}
