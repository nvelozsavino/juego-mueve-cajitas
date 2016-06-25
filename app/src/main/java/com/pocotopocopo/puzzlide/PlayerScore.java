package com.pocotopocopo.puzzlide;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by nico on 22/02/15.
 */

/**
 * PlayerScore
 */
public class PlayerScore implements Parcelable,Serializable, Comparable<PlayerScore> {
    private String playerId;
    private int movements=-1;
    private long time=-1;
//    private boolean played=false;

//    public boolean isPlayed() {
//        return played;
//    }
//
//    public void setPlayed(boolean played) {
//        this.played = played;
//    }



    public PlayerScore(String playerId, int movements, long time) {
        this.playerId = playerId;
        this.movements = movements;
        this.time = time;
    }
    public PlayerScore(String playerId) {
        this.playerId = playerId;
    }


    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
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
        dest.writeString(playerId);
        dest.writeInt(movements);
        dest.writeLong(time);
//        dest.writeInt(played?1:0);

    }

    public boolean isValid(){
        return  (time>0 && movements>0);
    }

    private PlayerScore(Parcel in){
        playerId=in.readString();
        movements=in.readInt();
        time=in.readLong();
//        played=in.readInt()==1?true:false;
    }

    public static final Creator<PlayerScore> CREATOR = new Creator<PlayerScore>() {
        @Override
        public PlayerScore createFromParcel(Parcel source) {
            return new PlayerScore(source);
        }

        @Override
        public PlayerScore[] newArray(int size) {
            return new PlayerScore[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (o!=null && o instanceof PlayerScore){
            String otherPlayerId = ((PlayerScore)o).playerId;
            return (otherPlayerId!=null && this.playerId!=null && otherPlayerId.equals(this.playerId));
        }
        return false;

    }

    @Override
    public int compareTo(PlayerScore another) {
        if (this.movements!=another.movements){
            return (this.movements -another.movements);
        } else {
            return (int)(this.time-another.time);
        }
    }

    public boolean update(PlayerScore playerScore){
        if (equals(playerScore)){
            this.movements=playerScore.movements;
            this.time=playerScore.time;
            return true;
        } else {
            return false;
        }
    }
}
