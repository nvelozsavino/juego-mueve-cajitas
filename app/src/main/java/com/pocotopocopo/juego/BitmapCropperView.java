package com.pocotopocopo.juego;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;



public class BitmapCropperView extends View {
//    private String mExampleString;
//    private int mExampleColor = Color.RED;
//    private float mExampleDimension = 0;
//    private Drawable mExampleDrawable;
//
//    private TextPaint mTextPaint;
//    private float mTextWidth;
//    private float mTextHeight;
    ScaleGestureDetector mScaleDetector;
    private float rectScaleFactor = 1.f;
    private static Integer INVALID_POINTER_ID = null;
    private Integer mActivePointerId = INVALID_POINTER_ID;
    private float mLastTouchX,mLastTouchY;
    private Bitmap imageBitmap;
    private Paint paint;
    private Rect rect;
    private float rectTop, rectLeft;

    private float rectSize = 1f;
    private final static String TAG = "Juego.BitmapCropperView";
    private float bitmapWidth;
    private float bitmapHeight;
    private Rect rectBitmap;
    private float bitmapScaleFactor= 1f;
    private float mLeft;
    private float mTop;
    private float mRight;
    private float mBottom;
    private float rectLeftNorm;
    private float rectTopNorm;
    private GameInfo gameInfo;


    public float getRectScaleFactor() {
        return rectScaleFactor;
    }

    public void setRectScaleFactor(float rectScaleFactor) {
        this.rectScaleFactor = rectScaleFactor;
    }

    public float getRectLeftNorm() {
        return rectLeftNorm;
    }

    public void setRectLeftNorm(float rectLeftNorm) {
        this.rectLeftNorm = rectLeftNorm;
    }

    public float getRectTopNorm() {
        return rectTopNorm;
    }

    public void setRectTopNorm(float rectTopNorm) {
        this.rectTopNorm = rectTopNorm;
    }

    public class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            rectScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            rectScaleFactor = Math.max(0.01f, Math.min(rectScaleFactor, 1.0f));
            updateRect();
//            rectSize = rectScaleFactor;
//            rect.set(rectLeft,rectTop,(int)(rectLeft+rectSize*rectScaleFactor),(int)(rectTop+rectSize*rectScaleFactor));
            invalidate();
            return true;
        }
    }

    public GameInfo getGameInfo() {
        return gameInfo;
    }

    public void setGameInfo(GameInfo gameInfo) {
        this.gameInfo = gameInfo;
    }

    //
//    public BitmapCropperView(Context context) {
//        super(context);
////        init(null, 0);
//    }

    public BitmapCropperView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG, "Constructor");
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rect = new Rect ();
        rectBitmap = new Rect();
        Log.d(TAG,"rectangulo");

        mScaleDetector = new ScaleGestureDetector(context,new ScaleListener());
        Log.d(TAG,"cree el ScaleGestureDetector");
//  init(attrs, 0);
    }

    public Bitmap getCroppedImage(){
        Log.d(TAG,"getCroppedImage()");
        float x = (rectLeft-mLeft)/bitmapScaleFactor;
        float y = (rectTop-mTop)/bitmapScaleFactor;
        x=x<0?0:x;
        y=y<0?0:y;
        float size = rectSize* rectScaleFactor /bitmapScaleFactor;

        Log.d(TAG,"bitmapScaleFactor = " + bitmapScaleFactor);
        Log.d(TAG,"x = " + x + " , y = " + y + " , size = " + size);
        Log.d(TAG,"bitmapWidth = " + imageBitmap.getWidth() + " , bitmapHeight = " + imageBitmap.getHeight());
        Bitmap bitmap = Bitmap.createBitmap(imageBitmap,(int)x,(int)y,(int)size,(int)size);
        Log.d(TAG,"estoy apunto de retornar el bitmap");
        return bitmap;
    }
    @Override
    protected void onDraw(Canvas canvas) {
//        Log.d(TAG,"onDraw");
        int strokeWidth= 3;
        if (imageBitmap!=null) {
            canvas.drawBitmap(imageBitmap, null, rectBitmap, paint);

            Log.d(TAG, "pinte el bitmap");
        }
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);
//        paint.setAlpha(50);
        canvas.drawRect(rect,paint);
        Rect rect1 = new Rect(rect);
        rect1.inset(strokeWidth,strokeWidth);
        paint.setColor(Color.YELLOW);
        canvas.drawRect(rect1,paint);
//        Log.d(TAG,"pinte rectangulo e imagen");
        super.onDraw(canvas);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d(TAG,"onSizeChanged");


        super.onSizeChanged(w, h, oldw, oldh);
        updateSizes();

        rectLeft = rectLeftNorm*(mRight-mLeft) + mLeft;
        rectTop = rectTopNorm*(mBottom-mTop) + mTop;
        updateRect();
        Log.d(TAG,"rectLeft = " + rectLeft + " , rectTop =" + rectTop + " , rectSize = " + rectSize + " , rectFactor = " + rectScaleFactor);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        finalWidth=getWidth();
//        finalHeight=getHeight();
//        rectSize = getWidth() < getHeight()? getWidth():getHeight();
//        Log.d(TAG,"rectSize = " + rectSize);
//        rect.set(0,0,rectSize,rectSize);
        Log.d(TAG,"onLayout()");
        Log.d(TAG,"rectLeft = " + rectLeft + " , rectTop =" + rectTop + " , rectSize = " + rectSize + " , rectFactor = " + rectScaleFactor);

        updateSizes();
//        rect.set((int)mLeft,(int)mTop,(int)(mLeft+rectSize),(int)(mTop+rectSize));
        updateRect();
        Log.d(TAG,"rectLeft = " + rectLeft + " , rectTop =" + rectTop + " , rectSize = " + rectSize + " , rectFactor = " + rectScaleFactor);

        super.onLayout(changed, left, top, right, bottom);
    }

    private void updateSizes (){
//        Log.d(TAG,"width = " +getWidth() + " - height = " +getHeight());
        bitmapScaleFactor = getWidth()/bitmapWidth < getHeight()/bitmapHeight ? getWidth()/bitmapWidth : getHeight()/bitmapHeight;
        rectSize = bitmapWidth > bitmapHeight ? bitmapHeight * bitmapScaleFactor : bitmapWidth * bitmapScaleFactor;
        mLeft = (getWidth()/2-bitmapWidth*bitmapScaleFactor/2);
        mTop = (getHeight()/2-bitmapHeight*bitmapScaleFactor/2);
        mRight = (getWidth()/2+bitmapWidth*bitmapScaleFactor/2);
        mBottom = (getHeight()/2+bitmapHeight*bitmapScaleFactor/2);


        Log.d(TAG,"[ " + mLeft + " , " + mTop + " , " + mRight + " , " + mBottom + "]");
        try {
            rectBitmap.set((int) mLeft, (int) mTop, (int) mRight, (int) mBottom);
        }catch(Exception e){
            Log.d(TAG,e.getMessage());
        }

//          updateRect();

    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        Log.d(TAG, "setImageBitmap");
        this.imageBitmap = imageBitmap;
        bitmapHeight= imageBitmap.getHeight();
        bitmapWidth= imageBitmap.getWidth();
        Log.d(TAG,"bitmap Width = " + bitmapWidth + " - bitmap Height = " + bitmapHeight);
        requestLayout();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return onTouchView(event);
    }




    private boolean onTouchView(MotionEvent ev) {
        Log.d(TAG,"onTouchView");
        mScaleDetector.onTouchEvent(ev);
        final int action = MotionEventCompat.getActionMasked(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                final float x = MotionEventCompat.getX(ev, pointerIndex);
                final float y = MotionEventCompat.getY(ev, pointerIndex);

                // Remember where we started (for dragging)
                mLastTouchX = x;
                mLastTouchY = y;
                // Save the ID of this pointer (for dragging)
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                // Find the index of the active pointer and fetch its position
                final int pointerIndex =
                        MotionEventCompat.findPointerIndex(ev, mActivePointerId);

                final float x = MotionEventCompat.getX(ev, pointerIndex);
                final float y = MotionEventCompat.getY(ev, pointerIndex);

                // Calculate the distance moved
                final float dx = x - mLastTouchX;
                final float dy = y - mLastTouchY;

                rectLeft += dx;
                rectTop += dy;
                updateRect();

                // Remember this touch position for the next move event
                mLastTouchX = x;
                mLastTouchY = y;

                break;
            }

            case MotionEvent.ACTION_UP: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {

                final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);

                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = MotionEventCompat.getX(ev, newPointerIndex);
                    mLastTouchY = MotionEventCompat.getY(ev, newPointerIndex);
                    mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
                }
                break;
            }
        }
        return true;
    }

    public void updateRect(){
        rectLeft = Float.isNaN(rectLeft)? 0 : rectLeft;
        rectTop = Float.isNaN(rectTop)? 0 : rectTop;

        rectLeft = rectLeft < mLeft? mLeft:rectLeft;
        rectTop= rectTop < mTop? mTop:rectTop;

        rectLeft = rectLeft+rectSize* rectScaleFactor > mRight ? (mRight-rectSize* rectScaleFactor) : rectLeft;
        rectTop= rectTop+rectSize* rectScaleFactor > mBottom ? (mBottom-rectSize* rectScaleFactor) : rectTop;

        rect.set((int)rectLeft,(int)rectTop,(int)(rectLeft+rectSize* rectScaleFactor),(int)(rectTop+rectSize* rectScaleFactor));

        rectLeftNorm = (mRight-mLeft) ==0? 0 :  (rectLeft-mLeft)/(mRight-mLeft);
        rectTopNorm = (mBottom-mTop)==0? 0 : (rectTop-mTop)/(mBottom-mTop);

        invalidate();
    }

    public void rotateBitmap(float rotation){
        Matrix matrix = new Matrix();
        matrix.postRotate(rotation);
//        Log.d(TAG,"Antes: width = " + imageBitmap.getWidth() + " , height = " +imageBitmap.getHeight());
        imageBitmap = Bitmap.createBitmap(imageBitmap,0,0,imageBitmap.getWidth(),imageBitmap.getHeight(),matrix,true);
//        Log.d(TAG,"despues: width = " + imageBitmap.getWidth() + " , height = " +imageBitmap.getHeight());
        bitmapHeight=imageBitmap.getHeight();
        bitmapWidth=imageBitmap.getWidth();

        updateSizes();
        updateRect();
    }

    public void update(){
        bitmapHeight=imageBitmap.getHeight();
        bitmapWidth=imageBitmap.getWidth();

        updateSizes();
        updateRect();

    }
}
