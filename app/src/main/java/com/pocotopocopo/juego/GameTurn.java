package com.pocotopocopo.juego;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

/**
 * Created by nico on 22/02/15.
 */
public class GameTurn implements Parcelable{
    private GameInfo gameInfo;
    private List<PlayerScore> scoreList;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
