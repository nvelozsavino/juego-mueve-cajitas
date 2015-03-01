package com.pocotopocopo.juego;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

/**
 * Created by nico on 22/02/15.
 */
public class GameData implements Parcelable{
    public GameInfo getGameInfo() {
        return gameInfo;
    }

    private GameInfo gameInfo;
    private List<PlayerScore> scoreList;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    public GameData(GameInfo gameInfo){
        this.gameInfo=gameInfo;
    }

    public byte[] persist(){
        return gameInfo.pack();
    }

    public static GameData unpack(byte[] data){
        GameInfo gameInfo=GameInfo.unpack(data);
        return new GameData(gameInfo);

    }
}
