package com.pocotopocopo.juego;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nico on 22/02/15.
 */
public class PlayerScore implements Parcelable {
    private int playerId;
    private int movements;
    private long time;

    public PlayerScore(int playerId, int movements, long time) {
        this.playerId = playerId;
        this.movements = movements;
        this.time = time;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getMovements() {
        return movements;
    }

    public void setMovements(int movements) {
        this.movements = movements;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
