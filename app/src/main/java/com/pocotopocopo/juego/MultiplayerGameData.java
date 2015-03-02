package com.pocotopocopo.juego;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nico on 22/02/15.
 */
public class MultiplayerGameData implements Parcelable{

    private static final String TAG = "MultiplayerGameData";
    private GameInfo gameInfo;
    private HashMap<String,PlayerScore> scoreList=new HashMap<>();


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(gameInfo,flags);
        dest.writeSerializable(scoreList);

    }

    private MultiplayerGameData(Parcel in){
        try {
            gameInfo = in.readParcelable(GameInfo.class.getClassLoader());
            scoreList = (HashMap<String, PlayerScore>) in.readSerializable();
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

    public void setScore(String playerId,PlayerScore score){
        scoreList.put(playerId,score);
    }

    public HashMap<String,PlayerScore> getScoreList(){
        return scoreList;
    }


    public GameInfo getGameInfo() {
        return gameInfo;
    }


    public static MultiplayerGameData unpack(byte[] data){
        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(data,0, data.length);
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
        if (scoreList.containsKey(playerId)){
            return scoreList.get(playerId);
        } else {
            return null;
        }

    }

    public Map<String,PlayerScore> getWinner(){
        int minMovements=-1;
        Map<String,PlayerScore> winners=new HashMap<>();
        for (Map.Entry<String,PlayerScore> entry:scoreList.entrySet()){
            PlayerScore score=entry.getValue();
            String playerId=entry.getKey();
            if (minMovements<0 || score.getMovements() <minMovements) {
                winners.clear();
                winners.put(playerId, score);
                minMovements=score.getMovements();
            } else if (minMovements<0 || score.getMovements() == minMovements){
                winners.put(playerId, score);
            }
        }

        if (scoreList.size()>1){
            Map<String,PlayerScore> tie=new HashMap<>();
            long minTime=-1;
            for (Map.Entry<String,PlayerScore> entry:winners.entrySet()){
                PlayerScore score = entry.getValue();
                String playerId=entry.getKey();
                if (minTime<0|| score.getTime()<minTime){
                    tie.clear();
                    tie.put(playerId, score);
                    minTime=score.getTime();
                } else if (minTime<0 || score.getTime()==minTime){
                    tie.put(playerId, score);
                }
            }
            return tie;
        } else {
            return winners;
        }
    }

}
