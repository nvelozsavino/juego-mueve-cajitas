package com.pocotopocopo.juego;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    private static final int maxPiecesW=4;
    private static final int maxPiecesH=4;
    private static final int maxPieces=(maxPiecesH*maxPiecesW)-1;
    private static final int pieceWidth=100;
    private static final int pieceHeight=100;
    private static final int paddingLeft=20;
    private static final int paddingTop=20;
    private static final int paddingPieceX=0;
    private static final int paddingPieceY=0;

    private static final String TAG="Juego";

    private RelativeLayout frame;

    List<Piece> pieceList= new ArrayList<>();
    Physics physics=new Physics();
    private Piece movingPiece;
    private Integer lastX,lastY;
    private Integer pointerId;
    private int displayWidth, displayHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Contacts: ********************************************* STARTING **********************************");
        setContentView(R.layout.activity_main);
        frame = (RelativeLayout) findViewById(R.id.frame);
        Log.d(TAG, "frame height: " + displayHeight);


        //Piece leftBorder= new Piece(getApplicationContext(),0,0,(pieceWidth+50)*maxPieces,(pieceHeight+50)*maxPieces,true);
        //leftBorder.setMovable(false);
        //pieceList.add(leftBorder);
        //frame.addView(leftBorder);

        for (int x=0;x<maxPiecesW;x++) {
            for (int y = 0; y < maxPiecesH; y++) {
                int i = x * maxPiecesH + y;
                if (i < maxPieces) {
                    int top, left;
                    left = x * (pieceWidth+paddingPieceX)+ paddingLeft+paddingPieceX;
                    top = y * (pieceHeight+paddingPieceY)+paddingTop+paddingPieceY;
                    Piece piece = new Piece(getApplicationContext(), top, left, pieceWidth, pieceHeight, i + 1);
                    pieceList.add(piece);
                    frame.addView(piece);
                }
            }
        }
        int borderTop = 0 + paddingTop;
        int borderLeft = 0 + paddingLeft;
        int borderRight= (pieceWidth+paddingPieceX)*maxPiecesW + paddingPieceX + paddingLeft;
        int borderBottom = (pieceHeight+paddingPieceY)*maxPiecesH + paddingPieceY + paddingTop;
        for (int i=0;i<4;i++) {
            int top=0, left=0, width=0, height=0;

            switch (i) {
                case 0://border left
                    top = borderTop - paddingTop;
                    left = borderLeft - paddingLeft;
                    width = paddingLeft;
                    height = borderBottom-top;
                    break;
                case 1://border top
                    top = borderTop-paddingTop;
                    left = borderLeft;
                    width = borderRight;
                    height = paddingTop;
                    break;
                case 2://border right
                    top = borderTop;
                    left = borderRight;
                    width = paddingLeft;
                    height = borderBottom;
                    break;
                case 3://border bottom
                    top = borderBottom;
                    left = borderLeft-paddingLeft;
                    width = borderRight-left;
                    height = paddingTop;
                    break;
            }

            Piece border = new Piece(getApplicationContext(), top, left, width, height, false);
            border.setMovable(false);
            pieceList.add(border);
            frame.addView(border);
        }







        frame.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return touchEvent(event);
            }
        });

        physics.addPieces(pieceList);


        Piece p1=pieceList.get(0);
        Piece p2=pieceList.get(1);
        Piece p3=pieceList.get(2);
        Piece p4=pieceList.get(3);
        Piece p5=pieceList.get(4);
        Piece p6=pieceList.get(5);
        Piece p7=pieceList.get(6);
        Piece p8=pieceList.get(7);
        Piece p9=pieceList.get(8);
        Piece p10=pieceList.get(9);

        Log.d(TAG,"Connections 0: \n" + physics);
        physics.movePiece(p4,Orientation.X,210);
//        Log.d(TAG,"Connections 1: \n" + physics);
//        physics.movePiece(p7,Orientation.Y,50);
//        Log.d(TAG,"Connections 2: \n" + physics);
//
//        physics.movePiece(p3,Orientation.X,110);
//        Log.d(TAG,"Connections 3: \n" + physics);
//
//        physics.movePiece(p3,Orientation.Y,20);
//        Log.d(TAG,"Connections 4: \n" + physics);
//

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
                int x = (int)event.getX(pointerIndex);
                int y = (int)event.getY(pointerIndex);
                for (Piece p : pieceList) {
                    if (p.intersect(x-frame.getPaddingLeft(), y-frame.getPaddingTop())) {
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
                    int x = (int)event.getX(pointerIndex);
                    int y = (int)event.getY(pointerIndex);
                    int dx=x-lastX;
                    int dy=y-lastY;
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

    private void movePiece(Piece piece, int dx, int dy){

        physics.movePiece(piece,Orientation.X,dx);
        physics.movePiece(piece,Orientation.Y,dy);

    }

}
