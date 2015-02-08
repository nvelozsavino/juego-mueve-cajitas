package com.pocotopocopo.juego;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nico on 07/02/15.
 */
public class GameInfo implements Parcelable {
    private int rows,cols;
    private BackgroundMode backgroundMode;
    private GameMode gameMode;
    private int[] pieceOrder=null;
    private boolean numbersVisible =true;
    private Bitmap bitmap=null;
    private String bitmapUrl=null;

    public GameInfo(int rows, int cols, BackgroundMode backgroundMode, GameMode gameMode,boolean numbersVisible, Bitmap bitmap){
        this.rows=rows;
        this.cols=cols;
        this.backgroundMode=backgroundMode;
        this.gameMode=gameMode;
        this.numbersVisible = numbersVisible;
        this.bitmap=bitmap;
    }

    public GameInfo(int rows, int cols, BackgroundMode backgroundMode, GameMode gameMode){
        this(rows,cols,backgroundMode,gameMode,true,null);
    }

    public GameInfo(int rows,int cols,BackgroundMode backgroundMode, GameMode gameMode, boolean numbersVisible){
        this(rows, cols, backgroundMode,gameMode, numbersVisible,null);
    }
    public GameInfo(int rows, int cols, BackgroundMode backgroundMode, GameMode gameMode, Bitmap bitmap){
        this(rows,cols, backgroundMode,gameMode,true,bitmap);
    }



    public BackgroundMode getBackgroundMode() {
        return backgroundMode;
    }

    public void setBackgroundMode(BackgroundMode backgroundMode) {
        this.backgroundMode = backgroundMode;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public int[] getPieceOrder() {
        return pieceOrder;
    }

    public void setPieceOrder(int[] pieceOrder) {
        this.pieceOrder = pieceOrder;
    }

    public boolean isNumbersVisible() {
        return numbersVisible;
    }

    public void setNumbersVisible(boolean numbersVisible) {
        this.numbersVisible = numbersVisible;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getBitmapUrl() {
        return bitmapUrl;
    }

    public void setBitmapUrl(String bitmapUrl) {
        this.bitmapUrl = bitmapUrl;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getCols() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    private GameInfo(Parcel in){
        rows=in.readInt();
        cols=in.readInt();
        backgroundMode = (BackgroundMode)in.readSerializable();
        gameMode = (GameMode)in.readSerializable();
        pieceOrder=in.createIntArray();
        numbersVisible =in.readByte()!=0;
        bitmap=in.readParcelable(Bitmap.class.getClassLoader());
        bitmapUrl=in.readString();
    }

    public static final Creator<GameInfo> CREATOR = new Creator<GameInfo>() {
        @Override
        public GameInfo createFromParcel(Parcel source) {
            return new GameInfo(source);
        }

        @Override
        public GameInfo[] newArray(int size) {
            return new GameInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(rows);
        dest.writeInt(cols);
        dest.writeSerializable(backgroundMode);
        dest.writeSerializable(gameMode);
        dest.writeIntArray(pieceOrder);
        dest.writeByte((byte) (numbersVisible ? 1 : 0));
        dest.writeParcelable(bitmap,flags);
        dest.writeString(bitmapUrl);
    }
}
