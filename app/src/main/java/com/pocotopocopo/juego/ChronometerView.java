package com.pocotopocopo.juego;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.widget.Chronometer;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 * Created by nico on 08/02/15.
 */
public class ChronometerView extends TextView {

    public static enum ChronometerState {RUNNING, PAUSED}
    private long time=0;
    private long lastTime;
    private boolean mVisible;
    private static final int TICK_WHAT = 2;
    private ChronometerState state=ChronometerState.PAUSED;
    private OnUpdateTextListener onUpdateTextListener;

    public void setOnUpdateTextListener(OnUpdateTextListener onUpdateTextListener) {
        this.onUpdateTextListener = onUpdateTextListener;
    }

    public interface OnUpdateTextListener{
        public void onUpdateText(String text);

    }


    public ChronometerView(Context context) {
        super(context);
        init();
    }

    public ChronometerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ChronometerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init(){
        updateText();

    }

    public void start(long time){
        this.time=time;
        if (state.equals(ChronometerState.PAUSED)){
            lastTime=SystemClock.elapsedRealtime();
            updateText();
            mHandler.sendMessageDelayed(Message.obtain(mHandler,
                    TICK_WHAT), 100);
        }
        state=ChronometerState.RUNNING;
    }
    public void start(){
        start(time);
    }

    public void reset(){
        time=0;
        updateText();
    }

    public void pause(long time){
        if (state.equals(ChronometerState.RUNNING)){
            mHandler.removeMessages(TICK_WHAT);
        }
        state = ChronometerState.PAUSED;
        this.time=time;
        updateText();
    }
    public void pause(){
        pause(time);
    }

    public String getTimeText(){
        return getText().toString();
    }

    public long getTime(){
        return time;
    }

    @Override
    protected void onDetachedFromWindow() {
        super .onDetachedFromWindow();
        mVisible = false;
        pause();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super .onWindowVisibilityChanged(visibility);
        mVisible = visibility == VISIBLE;
        if (!mVisible) {
            pause();
        }
    }


    private Handler mHandler = new Handler() {
        public void handleMessage(Message m) {
            if (state.equals(ChronometerState.RUNNING)) {
                updateText();
                sendMessageDelayed(Message.obtain(this , TICK_WHAT),
                        100);
            }
        }
    };


    private synchronized void updateText() {
        long now = SystemClock.elapsedRealtime();
        if (state.equals(ChronometerState.RUNNING)) {

            time += (now - lastTime);
            lastTime = now;
        }


        DecimalFormat df = new DecimalFormat("00");

        int hours = (int)(time / (3600 * 1000));
        int remaining = (int)(time % (3600 * 1000));

        int minutes = (int)(remaining / (60 * 1000));
        remaining = (int)(remaining % (60 * 1000));

        int seconds = (int)(remaining / 1000);
        remaining = (int)(remaining % (1000));

        int milliseconds = (int)(((int)time% 1000) / 100);

        String text = "";

        if (hours > 0) {
            text += df.format(hours) + ":";
        }

        text += df.format(minutes) + ":";
        text += df.format(seconds) + ":";
        text += Integer.toString(milliseconds);

        setText(text);
        if (onUpdateTextListener!=null) {
            onUpdateTextListener.onUpdateText(text);
        }
    }




}
