package com.pocotopocopo.juego;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nico on 21/01/15.
 */
public class Piece extends View {
    private static final String TAG = "Juego.Piece";
    private static int count;
    private int top, left;
    private int width, height;
    private int number;
    private boolean selected=false;
    public List<Piece> contactTop, contactLeft, contactRight, contactBottom;
    private Border borderLeft, borderRight, borderTop, borderBottom;
    private boolean movable=true;
    private boolean numerable=false;
    public boolean border=false;
    private int lastPos;
    private BitmapContainer bitmapContainer;
    private Rect rInit;
    private int paddingX=0;
    private int paddingY=0;
    private boolean numberVisible =true;

    private BitmapContainer.OnBitmapChangeListener bitmapChangeListener = new BitmapContainer.OnBitmapChangeListener() {
        @Override
        public void bitmapChange(Bitmap bitmap) {
            if (bitmap!=null) {
                invalidate();
            }
        }
    };




    public boolean isMovable() {
        return movable;
    }

    public void setMovable(boolean movable) {
        this.movable = movable;
        invalidate();
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        invalidate();
    }


    public int getPieceWidth() {
        return width;
    }

    public int getPieceHeight() {
        return height;
    }

    public Piece(Context context,int number){
        this(context,0,0,0,0,number);

    }

    public Piece(Context context,boolean border){
        this(context,0,0,0,0,border);

    }
    public static void resetCount(){
        count=0;
    }

    public Piece(Context context, int top, int left, int width, int height,boolean border) {
        super(context);
        this.top = top;
        this.left = left;
        this.width = width;
        this.height = height;
        this.border=border;
        if (border) {
            borderRight = new Border(Orientation.Y, left, top, height);
            borderLeft = new Border(Orientation.Y, left + width, top, height);
            borderBottom = new Border(Orientation.X, left, top, width);
            borderTop = new Border(Orientation.X, left, top + height, width);
        } else {
            borderLeft = new Border(Orientation.Y, left, top, height);
            borderRight = new Border(Orientation.Y, left + width, top, height);
            borderTop = new Border(Orientation.X, left, top, width);
            borderBottom = new Border(Orientation.X, left, top + height, width);

        }
        contactTop = new ArrayList<>();
        contactRight = new ArrayList<>();
        contactLeft = new ArrayList<>();
        contactBottom = new ArrayList<>();
        numerable=false;


    }
    public Piece(Context context, int top, int left, int width, int height, int number) {
        this(context,top,left,width,height,false);
        numerable=true;
        movable=true;
        this.number=number;
        count++;


    }

    public void setPadding(int paddingX,int paddingY){
        this.paddingX=paddingX;
        this.paddingY=paddingY;
    }

    public int getPaddingX() {
        return paddingX;
    }

    public int getPaddingY() {
        return paddingY;
    }

    public void updateSize(int width, int height){
        this.width=width;
        this.height=height;
        updateBorders();
        invalidate();
    }

    public void moveAbsolute(int x, int y){
        this.left=x;
        this.top=y;
        updateBorders();
        invalidate();
    }

    private void updateBorders() {
        if (border) {
            borderRight.update(left, top,height);
            borderLeft.update(left + width, top,height);
            borderBottom.update(left, top,width);
            borderTop.update(left, top + height,width);
        } else {
            borderLeft.update(left, top,height);
            borderRight.update(left + width, top,height);
            borderTop.update(left, top,width);
            borderBottom.update(left, top + height,width);

        }
    }

    public BitmapContainer getBitmap() {
        return bitmapContainer;
    }

    public void setBitmap(BitmapContainer bitmapContainer) {
        this.bitmapContainer = bitmapContainer;
        if (bitmapContainer!=null){
            bitmapContainer.registerBitmapChangeListener(bitmapChangeListener);
        }
    }

    public Rect getrInit() {
        return rInit;
    }

    public void setrInit(Rect rInit) {
        this.rInit = rInit;
    }

    public int getLastPos() {
        return lastPos;
    }

    public void setLastPos(int lastPos) {
        this.lastPos = lastPos;
    }

    public int getNumber() {
        return number;
    }


    public int getTopPos() {
        return top;
    }

    public int getLeftPos() {
        return left;
    }

    public boolean isNumberVisible() {
        return numberVisible;
    }

    public void setNumberVisible(boolean numberVisible) {
        this.numberVisible = numberVisible;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect r = new Rect((int)left, (int)top, (int)(left + width), (int)(top + height));
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setStyle(Paint.Style.FILL);

        if (movable) {

            paint.setColor(Color.GREEN);
        } else {
            paint.setColor(Color.GRAY);
        }
        canvas.drawRect(r, paint);



        if (bitmapContainer!=null && bitmapContainer.getBitmap()!=null) {
            Rect rect = new Rect(rInit);
            rect.inset(paddingX,paddingY);
            canvas.drawBitmap(bitmapContainer.getBitmap(), rect, r, paint);

        }

        if (numerable && numberVisible) {
            int maxNumber = count;
            int digits = Integer.toString(maxNumber).length();
            float sizeText = (height*digits/1.5f > width ? width*1.5f/digits : height)*0.75f;
            paint.setTextSize(sizeText);
            paint.setColor(Color.BLUE);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(Integer.toString(number), left + (width / 2), top + (height / 2) + (sizeText *0.3f), paint);
            //canvas.drawText(Integer.toString(i+1), x0+(x + 0.5f) * widthX, y0+((y+0.5f) * widthY)+sizeText*0.3f, paint);

        }
        if (selected) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.BLACK);
            int stroke=5;
            paint.setStrokeWidth(stroke);

            Rect rBorder = new Rect((int)(left+stroke/2), (int)(top+stroke/2), (int)(left + width-stroke/2), (int)(top + height-stroke/2));
            canvas.drawRect(rBorder, paint);
        }

    }

    public synchronized boolean intersect(int x, int y) {
//        Log.d(TAG, "intersect");
        if (border){
            return false;
        }

        boolean intersection = ((x > left && x < (left + width)) && (y > top && y < (top + height)));
//        Log.d(TAG, "number=" + number + " square=(" + left + "," + top + "," + (left + width) + "," + (top + height) + ")  (x,y)=(" + x + "," + y + ") intersect=" + intersection);
        return intersection;
    }

    public void move(int dx, int dy) {
        //Log.d(TAG, "Moving " + number);
        left += dx;
        top += dy;
        updateBorders();
        invalidate();
    }




    public Border getBorder(Direction direction) {
        switch (direction) {
            case LEFT:
                return borderLeft;
            case RIGHT:
                return borderRight;
            case UP:
                return borderTop;
            case DOWN:
                return borderBottom;
        }
        return null;
    }

    @Override
    public String toString() {
        return Integer.toString(number);
    }


}
