package com.pocotopocopo.juego;

import android.app.Activity;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class MainActivity extends Activity {

    private static final int maxPieces=4;
    private static final float pieceWidth=100.0f;
    private static final float pieceHeight=100.0f;

    private static final String TAG="Juego";

    private RelativeLayout frame;
    List<Piece> pieceList=new ArrayList<>();
    private Piece movingPiece;
    private Float lastX,lastY;
    private Integer pointerId;
    private int displayWidth, displayHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Contacts: ********************************************* STARTING **********************************");
        setContentView(R.layout.activity_main);
        frame = (RelativeLayout) findViewById(R.id.frame);
        Log.d(TAG, "frame height: " + displayHeight);


        Piece leftBorder= new Piece(getApplicationContext(),0,0,(pieceWidth+50)*maxPieces,(pieceHeight+50)*maxPieces,true);
        leftBorder.setMovable(false);
        pieceList.add(leftBorder);
        frame.addView(leftBorder);

        for (int i=0;i<maxPieces;i++){
            float top,left;
            top=i*(pieceHeight+20);
            left=20;

            Piece piece = new Piece(getApplicationContext(),top,left,pieceWidth,pieceHeight,i+1);
            pieceList.add(piece);
            frame.addView(piece);



        }
        frame.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return touchEvent(event);
            }
        });

        Piece p0=pieceList.get(0);
        Piece p1=pieceList.get(1);
        Piece p2=pieceList.get(2);
        Piece p3=pieceList.get(3);
        Piece p4=pieceList.get(4);

        movePiece(p1,0,41);
        //movePiece(p1,0,20);






/*
        Piece topBorder= new Piece(getApplicationContext(),0,0,720,displayWidth);
        topBorder.setMovable(false);
        pieceList.add(topBorder);
        frame.addView(topBorder);

*/

    }

    @Override
    protected void onResume() {
        super.onResume();



    }


    private void showPieces(){
        for (Piece p: pieceList){
            Log.d(TAG,"piece: " + p.getNumber());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {

            // Get the size of the display so this View knows where borders are
            displayWidth = frame.getWidth();
            displayHeight = frame.getHeight();
            Log.d(TAG,"Width = " + displayWidth);
            Log.d(TAG,"Height = " + displayHeight);
            Log.d(TAG,frame.getMeasuredWidth()+" " +frame.getMeasuredHeight());
        }
    }

    private boolean touchEvent(MotionEvent event) {

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                //Log.d(TAG, "pointer down");
                int pointerIndex = event.getActionIndex();
                pointerId = event.getPointerId(pointerIndex);
                float x = event.getX(pointerIndex);
                float y = event.getY(pointerIndex);
                for (Piece p : pieceList) {
                    if (p.intersect(x, y)) {
                        //Log.d(TAG, "intersect");
                        if (movingPiece!=null){
                            movingPiece.setSelected(false);
                        }
                        movingPiece = p;
                        movingPiece.setSelected(true);
                        lastX = x;
                        lastY = y;
                        break;
                    }
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                int pointerIndex = event.getActionIndex();
                if (pointerId!=null && pointerId ==event.getPointerId(pointerIndex) && movingPiece!=null) {
                    float x = event.getX(pointerIndex);
                    float y = event.getY(pointerIndex);
                    float dx=x-lastX;
                    float dy=y-lastY;
                    lastX=x;
                    lastY=y;
                    movePiece(movingPiece,dx,dy);

                }
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                int pointerIndex = event.getActionIndex();
                if (pointerId!=null && pointerId==event.getPointerId(pointerIndex)){
                    if (movingPiece!=null){
                        movingPiece.setSelected(false);
                    }
                    movingPiece=null;
                    pointerId=null;
                    lastY=null;
                    lastX=null;
                }
                break;
            }
            case MotionEvent.ACTION_UP:
                if (movingPiece!=null){
                    movingPiece.setSelected(false);
                }
                movingPiece=null;
                pointerId=null;
                lastY=null;
                lastX=null;
                break;


        }

        return true;
    }

    private void movePiece(Piece piece, float dx, float dy){

        piece.checkAllCollisions(pieceList,dx,Orientation.VERTICAL);
        piece.checkAllCollisions(pieceList,dy,Orientation.HORIZONTAL);

    }

}
