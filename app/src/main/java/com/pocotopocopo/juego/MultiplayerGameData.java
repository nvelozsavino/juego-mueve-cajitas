package com.pocotopocopo.juego;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.games.multiplayer.ParticipantResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by nico on 22/02/15.
 */
public class MultiplayerGameData implements Parcelable{

    private static final String TAG = "MultiplayerGameData";
    private GameInfo gameInfo;
    private List<PlayerScore> scoreList=new ArrayList<>();


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(gameInfo,flags);
        dest.writeTypedList(scoreList);

    }

    private MultiplayerGameData(Parcel in){
        try {
            gameInfo = in.readParcelable(GameInfo.class.getClassLoader());
            in.readTypedList(scoreList,PlayerScore.CREATOR);
        } catch (BadParcelableException e){
            Log.e(TAG, "Error", e);
        }
    }

    public MultiplayerGameData(GameInfo gameInfo){
        this.gameInfo=gameInfo;
    }

    public byte[] persist(){
        Parcel parcel = Parcel.obtain();
        this.writeToParcel(parcel, 0);
        byte[] bytes = parcel.marshall();
        parcel.recycle();
        return bytes;
    }


    public void setScore(PlayerScore score){
        if (scoreList.contains(score)){
            int index=scoreList.indexOf(score);
            scoreList.set(index,score);
        } else {
            scoreList.add(score);
        }
    }

    public List<PlayerScore> getScoreList(){
        return scoreList;
    }


    public GameInfo getGameInfo() {
        return gameInfo;
    }


    public static MultiplayerGameData unpack(byte[] data){
        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(data, 0, data.length);
        parcel.setDataPosition(0);
        return CREATOR.createFromParcel(parcel);
    }

    public static final Creator<MultiplayerGameData> CREATOR = new Creator<MultiplayerGameData>() {
        @Override
        public MultiplayerGameData createFromParcel(Parcel source) {
            return new MultiplayerGameData(source);
        }

        @Override
        public MultiplayerGameData[] newArray(int size) {
            return new MultiplayerGameData[size];
        }
    };

    public PlayerScore getScore(String playerId){
        PlayerScore ps =new PlayerScore(playerId);
        if (scoreList.contains(ps)){
            int index = scoreList.indexOf(ps);
            return scoreList.get(index);
        } else {
            return null;
        }

    }



    public List<ParticipantResult> getResults(List<String> participantsIds){
        List<ParticipantResult> participantResults = new ArrayList<>();

        int winner =ParticipantResult.MATCH_RESULT_WIN;
        Set<PlayerScore> copySet=new HashSet<>(scoreList);
        int i=0;
        for (String participant: participantsIds){
            PlayerScore checkingPlayer = getScore(participant);

            ParticipantResult participantResult;

            if (checkingPlayer==null){
                //checkingPlayer not even started to play
                participantResult= new ParticipantResult(participant,ParticipantResult.MATCH_RESULT_NONE,ParticipantResult.PLACING_UNINITIALIZED);

            } else {
                //checkingPlayer is a participant who played
                if (!checkingPlayer.isValid()){
                    //checkingPlayer is a participant who leaved the game
                    participantResult = new ParticipantResult(participant,ParticipantResult.MATCH_RESULT_DISCONNECT,ParticipantResult.PLACING_UNINITIALIZED);
                } else {
                    //checkingPlayer is a participant who completed the game
                    int result = winner; //Initial state, winner or tie
                    int pos=1; //initial position
                    for (PlayerScore otherPlayer: copySet){ //compare checkingPlayer with every other participant
                        if (otherPlayer.isValid() && !checkingPlayer.equals(otherPlayer)){
                        //the otherPlayer is valid and is not the same as checkingPlayer
                            if (checkingPlayer.compareTo(otherPlayer)>0) {
                                //checkingPlayer loss with otherPlayer
                                result=ParticipantResult.MATCH_RESULT_LOSS;
                                pos++;

                            } else if (checkingPlayer.compareTo(otherPlayer)==0){
                                //checkingPlayer tied with otherPlayer
                                if (pos==1){
                                    //if is still the first mark as tie
                                    result = ParticipantResult.MATCH_RESULT_TIE;
                                } else {
                                    //do nothing, is a loser already
                                }

                            } else {
                                //do nothing
                            }
                        }

                    }
                    if (result!=ParticipantResult.MATCH_RESULT_LOSS){
                        /**
                         * if is not a loser, should be a winner or tied in the first place
                         * with someone else and the winner variable should set to tie
                         * otherwise, has no effect
                         */

                        winner=result;
                    }
                    participantResult  = new ParticipantResult(participant,result,pos);

                }
            }
            participantResults.add(participantResult);
        }

        return participantResults;
    }

}
