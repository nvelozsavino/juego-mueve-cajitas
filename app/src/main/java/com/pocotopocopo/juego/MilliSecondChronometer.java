package com.pocotopocopo.juego;

/**
 * Created by Ale on 2/7/2015.
 */
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;
import java.text.DecimalFormat;

public class MilliSecondChronometer extends TextView {
    @SuppressWarnings("unused")
    private static final String TAG = "Juego.MilliSecondChronometer";

    public interface OnChronometerTickListener {

        void onChronometerTick(MilliSecondChronometer chronometer);
    }

    private long mBase;
    private boolean mVisible;
    private boolean mStarted;
    private boolean mRunning;
    private boolean mPaused=false;

    private OnChronometerTickListener mOnChronometerTickListener;

    private static final int TICK_WHAT = 2;

    private long timeElapsed;
    private long pausedTime=0;
//    private long pausedTimeIni;



    public MilliSecondChronometer(Context context) {
        this (context, null, 0);
    }

    public MilliSecondChronometer(Context context, AttributeSet attrs) {
        this (context, attrs, 0);
    }

    public MilliSecondChronometer(Context context, AttributeSet attrs, int defStyle) {
        super (context, attrs, defStyle);

        init();
    }

    private void init() {
        mBase = SystemClock.elapsedRealtime();
        updateText(mBase);
    }

//    public void setBase(long base) {
//        mBase = base;
//        dispatchChronometerTick();
//        updateText(SystemClock.elapsedRealtime());
//    }

    public long getBase() {
        return mBase;
    }

//    public void setOnChronometerTickListener(
//            OnChronometerTickListener listener) {
//        mOnChronometerTickListener = listener;
//    }

//    public OnChronometerTickListener getOnChronometerTickListener() {
//        return mOnChronometerTickListener;
//    }

    public void start() {
        Log.d(TAG, "start");
        pausedTime=0;
        mBase = SystemClock.elapsedRealtime();
        mStarted = true;
        updateRunning();
    }

    public void stop() {
        Log.d(TAG, "stop");
        mStarted = false;
        updateRunning();
    }
    public void pause(){
        Log.d(TAG, "pause");
//        if (mStarted && !mPaused){
            Log.d(TAG,"mbase = " + mBase);
            pausedTime = getTimeElapsed();
            Log.d(TAG,"pausedTime = " + pausedTime);
//            pausedTimeIni=System.currentTimeMillis();
            mPaused=true;
            stop();
//        }
    }
    public void resume(){
            Log.d(TAG, "resume");
//        if (!mStarted && mPaused){
            mBase = SystemClock.elapsedRealtime();
            Log.d(TAG,"mbase = " + mBase);
//            pausedTime = System.currentTimeMillis()-pausedTimeIni;
            mStarted = true;
            mPaused=false;
            updateRunning();
//        }
    }

    public long getPausedTime() {
        pausedTime = getTimeElapsed();
        return pausedTime;
    }

    public void setPausedTime(long pausedTime) {
        this.pausedTime = pausedTime;
        updateRunning();
    }

    public void setStarted(boolean started) {
        mStarted = started;
        updateRunning();
    }

    @Override
    protected void onDetachedFromWindow() {
        super .onDetachedFromWindow();
        mVisible = false;
        updateRunning();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super .onWindowVisibilityChanged(visibility);
        mVisible = visibility == VISIBLE;
        updateRunning();
    }

    private synchronized void updateText(long now) {
        timeElapsed = (now - mBase) + pausedTime;

        DecimalFormat df = new DecimalFormat("00");

        int hours = (int)(timeElapsed / (3600 * 1000));
        int remaining = (int)(timeElapsed % (3600 * 1000));

        int minutes = (int)(remaining / (60 * 1000));
        remaining = (int)(remaining % (60 * 1000));

        int seconds = (int)(remaining / 1000);
        remaining = (int)(remaining % (1000));

        int milliseconds = (int)(((int)timeElapsed % 1000) / 100);

        String text = "";

        if (hours > 0) {
            text += df.format(hours) + ":";
        }

        text += df.format(minutes) + ":";
        text += df.format(seconds) + ":";
        text += Integer.toString(milliseconds);

        setText(text);
    }

    private void updateRunning() {
        boolean running = mVisible && mStarted;
        if (running != mRunning) {
            if (running) {
                updateText(SystemClock.elapsedRealtime());
                dispatchChronometerTick();
                mHandler.sendMessageDelayed(Message.obtain(mHandler,
                        TICK_WHAT), 100);
            } else {
                mHandler.removeMessages(TICK_WHAT);
            }
            mRunning = running;
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message m) {
            if (mRunning) {
                updateText(SystemClock.elapsedRealtime());
                dispatchChronometerTick();
                sendMessageDelayed(Message.obtain(this , TICK_WHAT),
                        100);
            }
        }
    };

    void dispatchChronometerTick() {
        if (mOnChronometerTickListener != null) {
            mOnChronometerTickListener.onChronometerTick(this);
        }
    }

    public long getTimeElapsed() {
        return timeElapsed;
    }
    public void setTimeElapsed(long timeElapsed) {
        this.timeElapsed = timeElapsed;
    }

}