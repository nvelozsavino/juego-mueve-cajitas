package com.pocotopocopo.juego;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by nico on 29/01/15.
 */
public class BitmapContainer {
    public static final String TAG = "Juego.BitmapContainer";

    public static interface OnBitmapChangeListener{
        void bitmapChange(Bitmap bitmap);
    }

    private Set<OnBitmapChangeListener> bitmapChangeListenerList=new LinkedHashSet<>();
    private Bitmap bitmap;
    public BitmapContainer(Bitmap bitmap){
        this.bitmap=bitmap;
    }

    public void registerBitmapChangeListener(OnBitmapChangeListener bitmapChangeListener) {
        bitmapChangeListenerList.add(bitmapChangeListener);
    }

    public void unregisterBitmapChangeListener(OnBitmapChangeListener bitmapChangeListener) {
        bitmapChangeListenerList.remove(bitmapChangeListener);
    }



    public BitmapContainer(){
        this.bitmap=null;
    }
    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
//        Log.d(TAG, "setBitmap");

        this.bitmap = bitmap;
        for (OnBitmapChangeListener bitmapChangeListener:bitmapChangeListenerList){
            if (bitmapChangeListener!=null){
                bitmapChangeListener.bitmapChange(bitmap);
            }
        }

    }
}
