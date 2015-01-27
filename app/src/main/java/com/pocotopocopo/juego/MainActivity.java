package com.pocotopocopo.juego;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class MainActivity extends Activity {

    private static final int maxPiecesW=4;
    private static final int maxPiecesH=4;
    private static final int maxPieces=(maxPiecesH*maxPiecesW)-1;
    private static final int pieceWidth=50;
    private static final int pieceHeight=50;
    private static final int paddingLeft=10;
    private static final int paddingTop=10;
    private static final int paddingPieceX=0;
    private static final int paddingPieceY=0;
    private Bitmap bitmap;
    private static final String TAG="Juego";

    private RelativeLayout frame;

    List<Piece> pieceList= new ArrayList<>();
    private final int[] tol = new int[2];
    List<Integer[]> positions = new ArrayList<>();
    Physics physics=new Physics();
    private Piece movingPiece;
    private Integer lastX,lastY;
    private Integer pointerId;
    private int displayWidth, displayHeight;
    private int moveCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Contacts: ********************************************* STARTING **********************************");
        setContentView(R.layout.activity_main);
        frame = (RelativeLayout) findViewById(R.id.frame);
        bitmap = decodeSampledBitmapFromResource(getResources(),R.drawable.imagen,maxPiecesW*pieceWidth,maxPiecesH*pieceHeight);
        Log.d(TAG,"bitmap = "+ bitmap.getWidth()+" , " + bitmap.getHeight());
        int miniBitmapSizeW=bitmap.getWidth()/maxPiecesW;
        int miniBitmapSizeH=bitmap.getHeight()/maxPiecesH;
        Map<Integer,Rect> rectList = new HashMap<>();
        //Piece leftBorder= new Piece(getApplicationContext(),0,0,(pieceWidth+50)*maxPieces,(pieceHeight+50)*maxPieces,true);
        //leftBorder.setMovable(false);
        //pieceList.add(leftBorder);
        //frame.addView(leftBorder);
        tol[0]=pieceWidth/2;
        tol[1]=pieceHeight/2;
        for (int y=0;y<maxPiecesH;y++) {
            for (int x = 0; x < maxPiecesW; x++) {
                //int i = x * maxPiecesH + y;
                int top, left;
                left = x * (pieceWidth + paddingPieceX) + paddingLeft + paddingPieceX;
                top = y * (pieceHeight + paddingPieceY) + paddingTop + paddingPieceY;

                Integer[] pos = new Integer[2];
                pos[0] = left;//-paddingLeft-paddingPieceX;
                pos[1] = top;//-paddingTop-paddingPieceY;

                Rect rect = new Rect(x*miniBitmapSizeW,y*miniBitmapSizeH,(x+1)*miniBitmapSizeW,(y+1)*miniBitmapSizeH);

                rectList.put(x+y*maxPiecesH,rect);

                positions.add(pos);
            }
        }
        List<Integer[]> posRand = new ArrayList<>();
        posRand.addAll(positions);
        for (int i=0;i<maxPieces;i++){
            Random rnd = new Random();
            int posIndex = rnd.nextInt(posRand.size());
            int left = posRand.get(posIndex)[0];
            int top = posRand.get(posIndex)[1];

            posRand.remove(posIndex);
            if (i < maxPieces) {

                Piece piece = new Piece(getApplicationContext(), top, left, pieceWidth, pieceHeight, i + 1);
                //Log.d(TAG,"no he agregado la imagen de la pieza " + i);
                //Matrix matrix = new Matrix();
                //matrix.postScale((float)(pieceWidth/miniBitmapSizeW),(float)(pieceHeight/miniBitmapSizeH));
                //Log.d(TAG,"llegue aqui");
                //Bitmap bmp=Bitmap.createBitmap(bitmap,left,top,pieceWidth,pieceHeight,matrix,false);

                Rect rect = rectList.get(i+1);
                piece.setBitmap(bitmap);
                piece.setrInic(rect);
                //Log.d(TAG, "pieza "+ (i+1) + "-" +  rectList.get(posIndex).toString());
                //Log.d(TAG,"agregue la imagen de la pieza " + i);
                pieceList.add(piece);
                piece.setLastPos(i);
                frame.addView(piece);
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

       // Log.d(TAG,"Connections 0: \n" + physics);
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

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
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
            //Log.d(TAG,"Width = " + displayWidth);
            //Log.d(TAG,"Height = " + displayHeight);
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
                    snapPiece(pieceList,positions,tol);
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
                if (snapPiece(pieceList,positions,tol)){
                    moveCounter++;
                    TextView text = (TextView)findViewById(R.id.moveCounterText);
                    text.setText("Moves = " + moveCounter);
                    text.invalidate();
                    if(checkWin(pieceList)){
                        Log.d(TAG,"you win");
                        Toast toast = Toast.makeText(getApplicationContext(),"you win",Toast.LENGTH_LONG);
                        //toast.setText("Congratulations you Won in " + moveCounter + "moves");
                        //toast.setDuration(Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
                break;


        }

        return true;
    }

    private void movePiece(Piece piece, int dx, int dy){

        physics.movePiece(piece, Orientation.X, dx);
        physics.movePiece(piece, Orientation.Y, dy);

    }

    private boolean snapPiece(List<Piece> pieceList, List<Integer[]> positions, int[] tol){
        boolean hasBeenMoved=false;
        for (Piece piece : pieceList){

            if (piece.isMovable()) {
                int k=0;
                for (Integer[] pos : positions) {
                    int x = pos[0];
                    int y = pos[1];
                    if (piece.getLeftPos() - x < tol[0] && piece.getLeftPos() - x >= -tol[0]
                            && piece.getTopPos() - y < tol[1] && piece.getTopPos() - y >= -tol[1]) {

                        int dx = x-piece.getLeftPos();
                        int dy = y-piece.getTopPos();
                        movePiece(piece,dx,dy);
                         if(piece.getLastPos()!=k) {
                             hasBeenMoved = true;
                             piece.setLastPos(k);
                         }


                        //piece.setLeft(x);
                        //piece.setTop(y);
                        //piece.invalidate();
                    }
                    k++;
                }
            }

        }
        return hasBeenMoved;
    }

    private boolean checkWin(List<Piece> pieceList){
        boolean win=true;
        for (Piece piece:pieceList){
            if (piece.getNumber()!=piece.getLastPos()){
                win = false;
            }
        }

        return win;
    }
}
