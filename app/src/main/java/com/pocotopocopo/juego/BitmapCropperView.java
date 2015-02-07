package com.pocotopocopo.juego;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MotionEventCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;


/**
 * TODO: document your custom view class.
 */
public class BitmapCropperView extends View {
//    private String mExampleString; // TODO: use a default from R.string...
//    private int mExampleColor = Color.RED; // TODO: use a default from R.color...
//    private float mExampleDimension = 0; // TODO: use a default from R.dimen...
//    private Drawable mExampleDrawable;
//
//    private TextPaint mTextPaint;
//    private float mTextWidth;
//    private float mTextHeight;
    ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;
    private static Integer INVALID_POINTER_ID = null;
    private Integer mActivePointerId = INVALID_POINTER_ID;
    private float mLastTouchX,mLastTouchY;
    private Bitmap imageBitmap;
    private Paint paint;
    private Rect rect;
    private int rectTop;
    private int rectLeft;

    private int rectSize = 1;
    private final static String TAG = "Juego.BitmapCropperView";
    private float bitmapWidth;
    private float bitmapHeight;
    private Rect rectBitmap;
    private float bitmapScaleFactor;
    private float mLeft;
    private float mTop;
    private float mRight;
    private float mBottom;




    public class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.01f, Math.min(mScaleFactor, 1.0f));
            updateRect();
//            rectSize = mScaleFactor;
//            rect.set(rectLeft,rectTop,(int)(rectLeft+rectSize*mScaleFactor),(int)(rectTop+rectSize*mScaleFactor));
            invalidate();
            return true;
        }
    }


    public BitmapCropperView(Context context) {
        super(context);
//        init(null, 0);
    }

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
//
//    public BitmapCropperView(Context context, AttributeSet attrs, int defStyle) {
//        super(context, attrs, defStyle);
//        init(attrs, defStyle);
//    }
//
//    private void init(AttributeSet attrs, int defStyle) {
//        // Load attributes
//        final TypedArray a = getContext().obtainStyledAttributes(
//                attrs, R.styleable.BitmapCropperView, defStyle, 0);
//
//        mExampleString = a.getString(
//                R.styleable.BitmapCropperView_exampleString);
//        mExampleColor = a.getColor(
//                R.styleable.BitmapCropperView_exampleColor,
//                mExampleColor);
//        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
//        // values that should fall on pixel boundaries.
//        mExampleDimension = a.getDimension(
//                R.styleable.BitmapCropperView_exampleDimension,
//                mExampleDimension);
//
//        if (a.hasValue(R.styleable.BitmapCropperView_exampleDrawable)) {
//            mExampleDrawable = a.getDrawable(
//                    R.styleable.BitmapCropperView_exampleDrawable);
//            mExampleDrawable.setCallback(this);
//        }
//
//        a.recycle();
//
//        // Set up a default TextPaint object
//        mTextPaint = new TextPaint();
//        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
//        mTextPaint.setTextAlign(Paint.Align.LEFT);
//
//        // Update TextPaint and text measurements from attributes
//        invalidateTextPaintAndMeasurements();
//    }
//
//    private void invalidateTextPaintAndMeasurements() {
//        mTextPaint.setTextSize(mExampleDimension);
//        mTextPaint.setColor(mExampleColor);
//        mTextWidth = mTextPaint.measureText(mExampleString);
//
//        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
//        mTextHeight = fontMetrics.bottom;
//    }

    public Bitmap getCroppedImage(){
        Log.d(TAG,"getCroppedImage()");
        float x = (rectLeft-mLeft)/bitmapScaleFactor;
        float y = (rectTop-mTop)/bitmapScaleFactor;
        float size = rectSize*mScaleFactor/bitmapScaleFactor;
        Log.d(TAG,"bitmapScaleFactor = " + bitmapScaleFactor);
        Log.d(TAG,"x = " + x + " , y = " + y + " , size = " + size);
        Log.d(TAG,"bitmapWidth = " + imageBitmap.getWidth() + " , bitmapHeight = " + imageBitmap.getHeight());
        Bitmap bitmap = Bitmap.createBitmap(imageBitmap,(int)x,(int)y,(int)size,(int)size);
        Log.d(TAG,"estoy apunto de retornar el bitmap");
        return bitmap;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG,"onDraw");

        canvas.drawBitmap(imageBitmap,null,rectBitmap,paint);
        Log.d(TAG,"pinte el bitmap");
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);
//        paint.setAlpha(50);
        canvas.drawRect(rect,paint);
        Rect rect1 = new Rect(rect);
        rect1.inset(2,2);
        paint.setColor(Color.YELLOW);
        canvas.drawRect(rect1,paint);





        Log.d(TAG,"pinte rectangulo e imagen");
        super.onDraw(canvas);

//
//        // TODO: consider storing these as member variables to reduce
//        // allocations per draw cycle.
//        int paddingLeft = getPaddingLeft();
//        int paddingTop = getPaddingTop();
//        int paddingRight = getPaddingRight();
//        int paddingBottom = getPaddingBottom();
//
//        int contentWidth = getWidth() - paddingLeft - paddingRight;
//        int contentHeight = getHeight() - paddingTop - paddingBottom;
//
//        // Draw the text.
//        canvas.drawText(mExampleString,
//                paddingLeft + (contentWidth - mTextWidth) / 2,
//                paddingTop + (contentHeight + mTextHeight) / 2,
//                mTextPaint);
//
//        // Draw the example drawable on top of the text.
//        if (mExampleDrawable != null) {
//            mExampleDrawable.setBounds(paddingLeft, paddingTop,
//                    paddingLeft + contentWidth, paddingTop + contentHeight);
//            mExampleDrawable.draw(canvas);
//        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG,"onMeasure");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        Log.d(TAG,"onSizeChanged");
        updateSizes();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        finalWidth=getWidth();
//        finalHeight=getHeight();
//        rectSize = getWidth() < getHeight()? getWidth():getHeight();
//        Log.d(TAG,"rectSize = " + rectSize);
//        rect.set(0,0,rectSize,rectSize);
        updateSizes();
        super.onLayout(changed, left, top, right, bottom);
    }

    private void updateSizes (){
//        Log.d(TAG,"width = " +getWidth() + " - height = " +getHeight());
        bitmapScaleFactor = getWidth()/bitmapWidth < getHeight()/bitmapHeight ? getWidth()/bitmapWidth : getHeight()/bitmapHeight;
//        float bitmapScaleFactor = bitmapWidth/getWidth() > bitmapHeight/getHeight() ? bitmapWidth/getWidth() : bitmapHeight/getHeight();
//        Log.d(TAG,"bitmapScaleFactor = " + bitmapScaleFactor);
        if (bitmapWidth>bitmapHeight){
//            mLeft = 0f;
//            mTop = (getHeight()/2-bitmapHeight*bitmapScaleFactor/2);
//            mRight = getWidth();
//            mBottom = (getHeight()/2+bitmapHeight*bitmapScaleFactor/2);
            rectSize = (int)(bitmapHeight*bitmapScaleFactor);
        }else{
//            mLeft = (getWidth()/2-bitmapWidth*bitmapScaleFactor/2);
//            mTop = 0;
//            mRight = (getWidth()/2+bitmapWidth*bitmapScaleFactor/2);
//            mBottom = getHeight();
            rectSize = (int)(bitmapWidth*bitmapScaleFactor);
        }
        mLeft = (getWidth()/2-bitmapWidth*bitmapScaleFactor/2);
        mTop = (getHeight()/2-bitmapHeight*bitmapScaleFactor/2);
        mRight = (getWidth()/2+bitmapWidth*bitmapScaleFactor/2);
        mBottom = (getHeight()/2+bitmapHeight*bitmapScaleFactor/2);

        rect.set((int)mLeft,(int)mTop,(int)mLeft+rectSize,(int)mTop+rectSize);
        Log.d(TAG,"[ " + mLeft + " , " + mTop + " , " + mRight + " , " + mBottom + "]");
        try {
            rectBitmap.set((int) mLeft, (int) mTop, (int) mRight, (int) mBottom);
        }catch(Exception e){
            Log.d(TAG,e.getMessage());
        }
        updateRect();
    }
    @Override
    protected void onFinishInflate() {
        Log.d(TAG,"onFinishInflate");
        super.onFinishInflate();
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        Log.d(TAG,"onCreateDrawableState");
        return super.onCreateDrawableState(extraSpace);
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


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        return super.onTouchEvent(event);
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
        rectLeft = rectLeft < (int)mLeft? (int)mLeft:rectLeft;
        rectTop= rectTop < (int)mTop? (int)mTop:rectTop;

        rectLeft = rectLeft+rectSize*mScaleFactor > mRight ? (int)(mRight-rectSize*mScaleFactor) : rectLeft;
        rectTop= rectTop+rectSize*mScaleFactor > mBottom ? (int)(mBottom-rectSize*mScaleFactor) : rectTop;

        rect.set(rectLeft,rectTop,(int)(rectLeft+rectSize*mScaleFactor),(int)(rectTop+rectSize*mScaleFactor));

        invalidate();
    }

//    /**
//     * Gets the example string attribute value.
//     *
//     * @return The example string attribute value.
//     */
//    public String getExampleString() {
//        return mExampleString;
//    }
//
//    /**
//     * Sets the view's example string attribute value. In the example view, this string
//     * is the text to draw.
//     *
//     * @param exampleString The example string attribute value to use.
//     */
//    public void setExampleString(String exampleString) {
//        mExampleString = exampleString;
//        invalidateTextPaintAndMeasurements();
//    }
//
//    /**
//     * Gets the example color attribute value.
//     *
//     * @return The example color attribute value.
//     */
//    public int getExampleColor() {
//        return mExampleColor;
//    }
//
//    /**
//     * Sets the view's example color attribute value. In the example view, this color
//     * is the font color.
//     *
//     * @param exampleColor The example color attribute value to use.
//     */
//    public void setExampleColor(int exampleColor) {
//        mExampleColor = exampleColor;
//        invalidateTextPaintAndMeasurements();
//    }
//
//    /**
//     * Gets the example dimension attribute value.
//     *
//     * @return The example dimension attribute value.
//     */
//    public float getExampleDimension() {
//        return mExampleDimension;
//    }
//
//    /**
//     * Sets the view's example dimension attribute value. In the example view, this dimension
//     * is the font size.
//     *
//     * @param exampleDimension The example dimension attribute value to use.
//     */
//    public void setExampleDimension(float exampleDimension) {
//        mExampleDimension = exampleDimension;
//        invalidateTextPaintAndMeasurements();
//    }
//
//    /**
//     * Gets the example drawable attribute value.
//     *
//     * @return The example drawable attribute value.
//     */
//    public Drawable getExampleDrawable() {
//        return mExampleDrawable;
//    }
//
//    /**
//     * Sets the view's example drawable attribute value. In the example view, this drawable is
//     * drawn above the text.
//     *
//     * @param exampleDrawable The example drawable attribute value to use.
//     */
//    public void setExampleDrawable(Drawable exampleDrawable) {
//        mExampleDrawable = exampleDrawable;
//    }
}
