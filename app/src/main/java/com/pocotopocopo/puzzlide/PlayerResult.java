package com.pocotopocopo.puzzlide;

import android.content.Context;
import android.widget.ImageView;

import com.google.android.gms.common.images.ImageManager;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.ParticipantResult;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;

import java.util.Comparator;

/**
 * Created by nico on 3/04/15.
 */
public class PlayerResult {
    private String participantId;
    private PlayerScore playerScore;
    private ParticipantResult participantResult;
    private TurnBasedMatch match;
    private Participant participant;
    private long drawableId;

    public PlayerResult(TurnBasedMatch match, String participantId){
        this.participantId =participantId;
        MultiplayerGameData gameData=MultiplayerGameData.unpack(match.getData());
        if (gameData==null){
            throw new RuntimeException("No game data");

        }
        playerScore=gameData.getScore(participantId);
        participant= match.getParticipant(participantId);
        participantResult=participant.getResult();
    }

    public String getDisplayName(){
        return participant.getDisplayName();
    }

    public int getPlacing(){
        return participantResult.getPlacing();
    }
    public int getResult(){
        return participantResult.getResult();
    }
    public PlayerScore getPlayerScore(){
        return playerScore;
    }

    public boolean getProfileImage(Context context, ImageView imageView){
        if (participant.getIconImageUrl()!=null) {
            ImageManager imageManager = ImageManager.create(context);
            imageManager.loadImage(imageView, participant.getIconImageUri());
            return true;
        } else {
            return false;
        }

    }

    public static class ResultComparator implements Comparator<PlayerResult> {

        @Override
        public int compare(PlayerResult lhs, PlayerResult rhs) {
            if (lhs.getPlacing()!= ParticipantResult.PLACING_UNINITIALIZED && rhs.getPlacing()!= ParticipantResult.PLACING_UNINITIALIZED){
                return lhs.getPlacing()-rhs.getPlacing();
            } else if (lhs.getPlacing()== ParticipantResult.PLACING_UNINITIALIZED && rhs.getPlacing()== ParticipantResult.PLACING_UNINITIALIZED ){
                return 0;
            } else if (lhs.getPlacing()!= ParticipantResult.PLACING_UNINITIALIZED){
                return -100000;
            } else {
                return +100000;
            }

        }
    }

}
