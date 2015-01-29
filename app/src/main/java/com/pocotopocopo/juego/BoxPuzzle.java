package com.pocotopocopo.juego;

import android.content.Context;
import android.content.res.TypedArray;
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

/**
 * Created by nico on 28/01/15.
 */
public class BoxPuzzle extends ViewGroup {
    private final static String TAG="BoxPuzzle";
    private static final int defaultRows=4;
    private static final int defaultCols=4;
    private static final int defaultPieceWidth=50;
    private static final int defaultPieceHeight=50;
    private static final int defaultBorderPaddingX=10;
    private static final int defaultBorderPaddingY=10;
    private static final int defaultPiecePaddingX=0;
    private static final int defaultPiecePaddingY=0;

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

    private List<Piece> pieceList;
    private Map<Direction,Piece> borderMap;

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
        //setWillNotDraw(false);
        init();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    private void init(){
        pieces=rows*cols-1;
        Log.d(TAG,"# pieces = " + pieces);
        physics=new Physics(rows,cols);
        pieceList=new ArrayList<>();

        for (int i=0;i<pieces;i++){
            Piece piece=new Piece(getContext(),i+1);
            pieceList.add(piece);
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
        int minWidth=getPaddingLeft()+getPaddingRight()+getSuggestedMinimumWidth();
        int w = Math.max(minWidth, MeasureSpec.getSize(widthMeasureSpec));

        int minHeight=getPaddingTop()+getPaddingBottom()+getSuggestedMinimumHeight();
        int h = Math.max(minHeight,MeasureSpec.getSize(heightMeasureSpec));

        int width;
        int height;
        width=height=Math.min(w,h);




        Log.d(TAG, "W="+width+ " H="+height );
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

        Log.d(TAG,"OnDraw");
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
        Log.d(TAG,"Touched");
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                //Log.d(TAG, "pointer down");
                int pointerIndex = event.getActionIndex();
                pointerId = event.getPointerId(pointerIndex);
                int x = (int)event.getX(pointerIndex);
                int y = (int)event.getY(pointerIndex);
                for (Piece p : pieceList) {
                    if (p.intersect(x - getPaddingLeft(), y - getPaddingTop())) {
                        //Log.d(TAG, "intersect");
                        if (pieceMovement!=null){
                            pieceMovement.getPiece().setSelected(false);
                        }
                        pieceMovement= physics.new Movement(p);
                        pieceMovement.getPiece().setSelected(true);
                        lastX = x;
                        lastY = y;
                        break;
                    }
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
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
                int pointerIndex = event.getActionIndex();
                if (pointerId!=null && pointerId==event.getPointerId(pointerIndex)){
                    if (pieceMovement!=null){
                        pieceMovement.getPiece().setSelected(false);
                        physics.snapMovement(pieceMovement);
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
                    physics.snapMovement(pieceMovement);
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

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG,"OnSizeChanged w="+w+" h="+h);

        pieceWidth=((w-(2*borderPaddingX)-piecePaddingX)/cols)-piecePaddingX;
        pieceHeight=((h-(2*borderPaddingY)-piecePaddingY)/rows)-piecePaddingY;

        for (int i=0;i<pieces;i++){
            Piece piece = physics.getPiece(i);
            if (piece!=null){
                int x=i%cols;
                int y=i/cols;

                int left = x * (pieceWidth + piecePaddingX) + borderPaddingX + piecePaddingX;
                int top = y * (pieceHeight + piecePaddingY) + borderPaddingY + piecePaddingY;

                piece.updateSize(pieceWidth,pieceHeight);
                piece.setPadding(piecePaddingX,piecePaddingY);
                piece.moveAbsolute(left,top);
                piece.layout(0, 0, w, h);
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
            border.layout(0,0,w,h);

        }

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
        Piece p11=pieceList.get(10);
        Piece p12=pieceList.get(11);
        Piece p13=pieceList.get(12);
        Piece p14=pieceList.get(13);
        Piece p15=pieceList.get(14);





        Physics.Movement movement=physics.new Movement(p4);
        movement.move(Direction.DOWN,1000);
        physics.snapMovement(movement);

        invalidate();


    }
}
