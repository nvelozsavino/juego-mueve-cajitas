package com.pocotopocopo.juego;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class BoxPuzzle extends ViewGroup {
    private final static String TAG="Juego.BoxPuzzle";
    private static final int defaultRows=4;
    private static final int defaultCols=4;
    private static final int defaultPieceWidth=50;
    private static final int defaultPieceHeight=50;
    private static final int defaultBorderPaddingX=10;
    private static final int defaultBorderPaddingY=10;
    private static final int defaultPiecePaddingX=3;
    private static final int defaultPiecePaddingY=3;

    private int rows=defaultRows;
    private int cols=defaultCols;
    private int pieceWidth=defaultPieceWidth;
    private int pieceHeight=defaultPieceHeight;
    private int borderPaddingX=defaultBorderPaddingX;
    private int borderPaddingY=defaultBorderPaddingY;
    private int piecePaddingX=defaultPiecePaddingX;
    private int piecePaddingY=defaultPiecePaddingY;
    private int pieces;
    private Physics physics;

    private Physics.Movement pieceMovement;
    private Integer lastX,lastY;
    private Integer pointerId;

    //private List<Piece> pieceList;
    private Map<Direction,Piece> borderMap;

    private BitmapContainer bitmapContainer;

    private OnMovePieceListener listener;
    public static interface OnMovePieceListener{
        void onPieceMoved();
    }

    public void randomizeBoard(){
        Random rnd = new Random();
        List<Piece> pieceListTemp = new ArrayList<>();
//        List<Piece> pieceListTemp2 = physics.getPieceList();
//        Log.d(TAG,"pieceList.Size anres de randomizar = " +physics.getPieceList().size());
        do{

            //        pieceListTemp.addAll(pieceList);
            for (int i = 0; i < physics.getPieceList().size(); i++) {
                int rndPos = rnd.nextInt(physics.getPieceList().size());
                Piece piece =physics.getPieceList().get(rndPos);
                if (piece!=null) {
                    pieceListTemp.add(piece);
                }else {
                    pieceListTemp.add(null);
                }
                physics.getPieceList().remove(rndPos);

            }
            physics.getPieceList().addAll(pieceListTemp);
            pieceListTemp.clear();
//            Log.d(TAG,"randomizando...");
        }while(!isResolvable());
//        Log.d(TAG,"randomize");
//        Log.d(TAG,"pieceList.Size antes de updatear = " +physics.getPieceList().size());
        //update();
    }

    public int getResolvableNumber() {
        List<Piece> pieceList = physics.getPieceList();
        int cont = 0;
        int e = 0;
        for (int i = 0; i < pieceList.size(); i++) {
            Piece piece1 = pieceList.get(i);
            if (piece1 != null) {
                int number1 = piece1.getNumber();
                for (int j = i + 1; j < pieceList.size(); j++) {
                    Piece piece2 = pieceList.get(j);
                    if (piece2 != null) {
                        int number2 = piece2.getNumber();
                        if (number2 < number1) {
                            cont++;
                        }
                    }
                }

            } //else {
//                e = i / cols;
//            }
        }
        return cont;
    }

    public boolean isWin(){
//        boolean win = true;
        for (int i = 0;i<physics.getPieceList().size();i++){
            Piece piece = physics.getPieceList().get(i);
            if (piece!=null){
                if(i!=piece.getNumber()-1){
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isResolvable(){
        int cont = getResolvableNumber();
        int e=-1;
        for (int i =0;i<physics.getPieceList().size();i++){
            if (physics.getPieceList().get(i)==null){
                e=i/cols;
            }
        }
        if (pieces%2==1){
            if ((cont+e)%2==1){
                return true;
            }else{
                return false;
            }
        }else{
            if (cont%2==0){
                return true;
            }else{
                return false;
            }
        }

    }

    public void setOnMovePieceListener(OnMovePieceListener listener){
        this.listener=listener;
    }

    public Physics getPhysics() {
        return physics;
    }

    public BitmapContainer getBitmapContainer() {
        return bitmapContainer;
    }

    public void setPhysics(Physics physics) {
        this.physics = physics;
    }

    public BoxPuzzle(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray=context.getTheme().obtainStyledAttributes(attrs,R.styleable.BoxPuzzle,0,0);
        try{
            this.rows = typedArray.getInteger(R.styleable.BoxPuzzle_rows,defaultRows);
            this.cols = typedArray.getInteger(R.styleable.BoxPuzzle_cols,defaultCols);

            this.pieceWidth = typedArray.getInteger(R.styleable.BoxPuzzle_pieceWidth,defaultPieceWidth);
            this.pieceHeight = typedArray.getInteger(R.styleable.BoxPuzzle_pieceHeight,defaultPieceHeight);

            this.piecePaddingX = typedArray.getInteger(R.styleable.BoxPuzzle_piecePaddingX,defaultPiecePaddingX);
            this.piecePaddingY = typedArray.getInteger(R.styleable.BoxPuzzle_piecePaddingY,defaultPiecePaddingY);

            this.borderPaddingX = typedArray.getInteger(R.styleable.BoxPuzzle_borderPaddingX,defaultBorderPaddingX);
            this.borderPaddingY = typedArray.getInteger(R.styleable.BoxPuzzle_borderPaddingY,defaultBorderPaddingY);


        } finally {
            typedArray.recycle();
        }
        setWillNotDraw(false);
//        Log.d(TAG,"listo para init()");
        init();
    }

    public void setSize(int cols, int rows){
//        Log.d(TAG,"setSize");
        this.cols=cols;
        this.rows=rows;
        physics = null;
        removeAllViews();
        init();

    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        Log.d(TAG,"onLayout()");
    }

    public void setBitmapContainer(BitmapContainer bitmapContainer){
        this.bitmapContainer=bitmapContainer;
//        Log.d(TAG,"setBitmapContainer");
        for (Piece piece: physics.getPieceList()){
            if (piece!=null) {
//                Log.d(TAG, "setBitmapContainer piece " + piece.getNumber());
                piece.setBitmap(bitmapContainer);
            }
        }
        update();
//        Log.d(TAG,"updatie");
        invalidate();
//        Log.d(TAG,"invalide");
        requestLayout();
//        Log.d(TAG,"requestie");

    }

    private void init(){

        pieces=rows*cols-1;
//        Log.d(TAG,"# pieces = " + pieces);
        physics=new Physics(rows,cols);
        //pieceList=new ArrayList<>();

        for (int i=0;i<pieces;i++){
            Piece piece=new Piece(getContext(),i+1);
            //pieceList.add(piece);
            physics.addPiece(piece);
            addView(piece);
        }
        physics.moveHole(rows * cols - 1);
        borderMap=new HashMap<>(4);

        for (int i=0;i<4;i++){
            Direction direction=Direction.values()[i];
            Piece piece = new Piece(getContext(),false);
            piece.setMovable(false);
            borderMap.put(direction,piece);
            physics.addBorder(piece,direction);
            addView(piece);
        }
       randomizeBoard();
    }


    public int[] getPositions(){
        int[] posNumbers = new int[physics.getPieceList().size()];
        for (int i=0;i<physics.getPieceList().size();i++){
            Piece piece = physics.getPieceList().get(i);
            if (piece!=null){
                posNumbers[i] = piece.getNumber();

            }else{
                posNumbers[i] = 0;
            }
        }
        return posNumbers;
    }

    public void setPositions(int[] posNumbers){
        List<Piece> pieceList = physics.getPieceList();
        List<Piece> pieceList1 = new ArrayList<>();
        for (int i=0; i< posNumbers.length; i++){

            for (int j=0;j<pieceList.size();j++){
                Piece piece = pieceList.get(j);
                if (piece!=null){
                    if (piece.getNumber()==posNumbers[i]){
                        pieceList.remove(piece);
                        pieceList1.add(i,piece);
                    }

                }else{
                    if (posNumbers[i]==0){
                        pieceList.remove(j);
                        pieceList1.add(i,null);
                    }
                }
            }

        }
//        Log.d(TAG,"hice el for");

        physics.getPieceList().addAll(pieceList1);
//        Log.d(TAG,"reagregue las piezas");

        update();
//        Log.d(TAG,"updatie");

    }


    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
        invalidate();
        requestLayout();
    }

    public int getCols() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
        invalidate();
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        Log.d(TAG,"onMeassure");
        int minWidth=getPaddingLeft()+getPaddingRight()+getSuggestedMinimumWidth();
        int w = Math.max(minWidth, MeasureSpec.getSize(widthMeasureSpec));

        int minHeight=getPaddingTop()+getPaddingBottom()+getSuggestedMinimumHeight();
        int h = Math.max(minHeight,MeasureSpec.getSize(heightMeasureSpec));

        int width;
        int height;
        width=height=Math.min(w,h);




//        Log.d(TAG, "W="+width+ " H="+height );
        setMeasuredDimension(width,height);



        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        return 2*borderPaddingX+piecePaddingX+(pieceWidth+piecePaddingX)*cols;
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return 2*borderPaddingY+piecePaddingY+(pieceHeight+piecePaddingY)*rows;
    }

    @Override
    protected void onDraw(Canvas canvas) {

//        Log.d(TAG,"OnDraw");
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        int stroke=5;
        paint.setStrokeWidth(stroke);

        Rect rBorder = new Rect((int)(0+stroke/2), (int)(0+stroke/2), (int)(0 + this.getWidth()-stroke/2), (int)(0 + getHeight()-stroke/2));
        canvas.drawRect(rBorder, paint);
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        Log.d(TAG,"Touched");
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
//                Log.d(TAG, "pointer down");
                int pointerIndex = event.getActionIndex();
                pointerId = event.getPointerId(pointerIndex);
                int x = (int)event.getX(pointerIndex);
                int y = (int)event.getY(pointerIndex);
//                Log.d(TAG,"piezas = " + physics.getPieceList().size());
                for (Piece p : physics.getPieceList()) {
                    if (p!=null) {

                        if (p.intersect(x - getPaddingLeft(), y - getPaddingTop())) {
//                            Log.d(TAG, "intersect");
                            if (pieceMovement != null) {
                                pieceMovement.getPiece().setSelected(false);
                            }
                            pieceMovement = physics.new Movement(p);
                            pieceMovement.getPiece().setSelected(true);
                            lastX = x;
                            lastY = y;
                            break;
                        }
                    }
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
//                Log.d(TAG,"MOVE" );
                int pointerIndex = event.getActionIndex();
                if (pointerId!=null && pointerId ==event.getPointerId(pointerIndex) && pieceMovement!=null) {
                    int x = (int)event.getX(pointerIndex);
                    int y = (int)event.getY(pointerIndex);
                    int dx=x-lastX;
                    int dy=y-lastY;
                    lastX=x;
                    lastY=y;
                    movePiece(pieceMovement,dx,dy);

                }
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
//                Log.d(TAG,"Pointer UP" );
                int pointerIndex = event.getActionIndex();
                if (pointerId!=null && pointerId==event.getPointerId(pointerIndex)){
                    if (pieceMovement!=null){
                        pieceMovement.getPiece().setSelected(false);

                        if (physics.snapMovement(pieceMovement)){
                            callListener();

                        }
                        update();

                    }

                    pieceMovement=null;
                    pointerId=null;
                    lastY=null;
                    lastX=null;
                    //snapPiece(pieceList,positions,tol);
                }
                break;
            }
            case MotionEvent.ACTION_UP:

                if (pieceMovement!=null){
                    pieceMovement.getPiece().setSelected(false);

                    if (physics.snapMovement(pieceMovement)) {
                        callListener();


                    }
                    update();
                }
                pieceMovement=null;
                pointerId=null;
                lastY=null;
                lastX=null;
//                if (snapPiece(pieceList,positions,tol)){
//                    moveCounter++;
//                    TextView text = (TextView)findViewById(R.id.moveCounterText);
//                    text.setText("Moves = " + moveCounter);
//                    text.invalidate();
//                    if(checkWin(pieceList)){
//                        Log.d(TAG,"you win");
//                        Toast toast = Toast.makeText(getApplicationContext(),"you win",Toast.LENGTH_LONG);
//                        //toast.setText("Congratulations you Won in " + moveCounter + "moves");
//                        //toast.setDuration(Toast.LENGTH_LONG);
//                        toast.show();
//                    }
//                }
                break;


        }

        return true;
    }

    private void callListener(){
        if (listener!=null){
            listener.onPieceMoved();
        }
    }


    private void movePiece(Physics.Movement movement, int dx, int dy){
        Direction direction;
        if (dx>0){
            direction=Direction.RIGHT;
        } else {
            direction=Direction.LEFT;
        }
        movement.move(direction,Math.abs(dx));
        if (dy>0){
            direction=Direction.DOWN;
        } else {
            direction=Direction.UP;
        }
        movement.move(direction,Math.abs(dy));

    }



    public void update(){
//        Log.d(TAG,"update()");
//        Map<Integer,Rect> rectList = new HashMap<>();
        int miniBitmapWidth=0,miniBitmapHeight=0;
        if (bitmapContainer.getBitmap()!=null){
//            Log.d(TAG,"bitmapcontainer.bitmap no es null");
            miniBitmapWidth=bitmapContainer.getBitmap().getWidth()/cols;
            miniBitmapHeight=bitmapContainer.getBitmap().getHeight()/rows;
//            Log.d(TAG,"tengo las imagenes");
        }
//        Log.d(TAG,"pieceList.Size = " +physics.getPieceList().size());
        for (int i=0;i<physics.getPieceList().size();i++){
            Piece piece = physics.getPiece(i);
            if (piece!=null){
                int x=i%cols;
                int y=i/cols;
                int left = x * (pieceWidth + piecePaddingX) + borderPaddingX + piecePaddingX;
                int top = y * (pieceHeight + piecePaddingY) + borderPaddingY + piecePaddingY;

                int number = piece.getNumber()-1;
                int xNum = number%cols;
                int yNum = number/cols;
                // int leftNum = xNum * (pieceWidth + piecePaddingX) + borderPaddingX + piecePaddingX;
                // int topNum = yNum * (pieceHeight + piecePaddingY) + borderPaddingY + piecePaddingY;

                //if (bitmap!=null) {
//                Log.d(TAG,"antes de hacer el rectangulo " + piece.getNumber());
                Rect rect = new Rect(xNum * miniBitmapWidth, yNum * miniBitmapHeight, (xNum + 1) * miniBitmapWidth, (yNum + 1) * miniBitmapHeight);
//                Log.d(TAG,"despues de hacer el rectangulo " + piece.getNumber());
                piece.setrInit(rect);
//                Log.d(TAG,"setie el rect en la pieza "+ piece.getNumber());
                // piece.setBitmapContainer(bitmap);
//                    Bitmap pieceBitmap = Bitmap.createBitmap(bitmap,x*miniBitmapWidth,y*miniBitmapHeight,miniBitmapWidth,miniBitmapHeight);

//                    piece.setBitmapContainer(pieceBitmap);
                //}

                piece.updateSize(pieceWidth,pieceHeight);
                piece.setPadding(piecePaddingX,piecePaddingY);
                piece.moveAbsolute(left,top);
                piece.layout(0, 0, getWidth(), getHeight());
            }

        }

        int borderTop = 0 + borderPaddingY;
        int borderLeft = 0 + borderPaddingX;
        int borderRight= (pieceWidth+piecePaddingX)*cols + piecePaddingX + borderPaddingX;
        int borderBottom = (pieceHeight+piecePaddingY)*rows + piecePaddingY + borderPaddingY;
        for (Map.Entry<Direction,Piece> entry: borderMap.entrySet()) {

            Direction direction=entry.getKey();
            Piece border = entry.getValue();
            int top = 0, left = 0, width = 0, height = 0;

            switch (direction) {
                case LEFT://border left
                    top = borderTop - borderPaddingY;
                    left = borderLeft - borderPaddingX;
                    width = borderPaddingX;
                    height = borderBottom - top;
                    break;
                case UP://border top
                    top = borderTop - borderPaddingY;
                    left = borderLeft;
                    width = borderRight;
                    height = borderPaddingY;
                    break;
                case RIGHT://border right
                    top = borderTop;
                    left = borderRight;
                    width = borderPaddingX;
                    height = borderBottom;
                    break;
                case DOWN://border bottom
                    top = borderBottom;
                    left = borderLeft - borderPaddingX;
                    width = borderRight - left;
                    height = borderPaddingX;
                    break;
            }
            border.updateSize(width,height);
            border.moveAbsolute(left,top);
            border.layout(0,0,getWidth(),getHeight());

        }

//        Piece p1=pieceList.get(0);
//        Piece p2=pieceList.get(1);
//        Piece p3=pieceList.get(2);
//        Piece p4=pieceList.get(3);
//        Piece p5=pieceList.get(4);
//        Piece p6=pieceList.get(5);
//        Piece p7=pieceList.get(6);
//        Piece p8=pieceList.get(7);
//        Piece p9=pieceList.get(8);
//        Piece p10=pieceList.get(9);
//        Piece p11=pieceList.get(10);
//        Piece p12=pieceList.get(11);
//        Piece p13=pieceList.get(12);
//        Piece p14=pieceList.get(13);
//        Piece p15=pieceList.get(14);
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d(TAG,"OnSizeChanged w=" + w + " h="+h);
        super.onSizeChanged(w, h, oldw, oldh);


        pieceWidth=((w-(2*borderPaddingX)-piecePaddingX)/cols)-piecePaddingX;
        pieceHeight=((h-(2*borderPaddingY)-piecePaddingY)/rows)-piecePaddingY;

        update();




//
//        Physics.Movement movement=physics.new Movement(p4);
//        movement.move(Direction.DOWN,1000);
//        physics.snapMovement(movement);

        invalidate();


    }
}