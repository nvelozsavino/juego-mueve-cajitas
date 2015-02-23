package com.pocotopocopo.juego;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.ByteArrayOutputStream;

/**
 * Created by nico on 07/02/15.
 */
public class GameInfo implements Parcelable {
    private int rows,cols;
    private BackgroundMode backgroundMode;
    private GameMode gameMode;
    private int[] pieceOrder=null;
    private boolean numbersVisible =true;
    private byte[] bitmapBytes;
    private String bitmapUrl=null;
    private long timeForSpeed=-1;

    public GameInfo(int rows, int cols, BackgroundMode backgroundMode, GameMode gameMode,boolean numbersVisible, Bitmap bitmap){
        this.rows=rows;
        this.cols=cols;
        this.backgroundMode=backgroundMode;
        this.gameMode=gameMode;
        this.numbersVisible = numbersVisible;
        setBitmap(bitmap);
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

    public long getTimeForSpeed() {
        return timeForSpeed;
    }

    public void setTimeForSpeed(long timeForSpeed) {
        this.timeForSpeed = timeForSpeed;
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
        return getBitmap(bitmapBytes);
    }

    public void setBitmap(Bitmap bitmap) {
        if (bitmap!=null) {
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bs);
//        Log.d(TAG, "bitmap count nuevo = " + bitmap.getByteCount());
            bitmapBytes = bs.toByteArray();
        } else{
         bitmapBytes=null;
        }
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

    private Bitmap getBitmap(byte[] bitmapBytes){
        if (bitmapBytes!=null) {
            return BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
        } else {
            return null;
        }
    }

    private GameInfo(Parcel in){
        rows=in.readInt();
        cols=in.readInt();
        backgroundMode = (BackgroundMode)in.readSerializable();
        gameMode = (GameMode)in.readSerializable();
        pieceOrder=in.createIntArray();
        numbersVisible =in.readByte()!=0;
        bitmapBytes = in.createByteArray();
        bitmapUrl=in.readString();
        timeForSpeed=in.readLong();

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
        dest.writeByteArray(bitmapBytes);
        dest.writeString(bitmapUrl);
        dest.writeLong(timeForSpeed);
    }


    public byte[] pack(){
        Parcel parcel = Parcel.obtain();
        this.writeToParcel(parcel, 0);
        byte[] bytes = parcel.marshall();
        parcel.recycle();
        return bytes;
    }
    static public GameInfo unpack(byte[] bytes){
        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(bytes,0, bytes.length);
        parcel.setDataPosition(0);
        return CREATOR.createFromParcel(parcel);
    }
}
