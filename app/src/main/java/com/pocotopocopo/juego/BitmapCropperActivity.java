package com.pocotopocopo.juego;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;


public class BitmapCropperActivity extends ActionBarActivity {
    private BitmapCropperView imgView;

    public static final String BITMAP_KEY = "BitmapKey";
    public static final String TAG = "Juego.BitmapCropperActivity";
    ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;
    private static Integer INVALID_POINTER_ID = null;
    private Integer mActivePointerId = INVALID_POINTER_ID;
    private float mLastTouchX,mLastTouchY;
    private Button cropButton;
    private Button cancelButton;



    public class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));

            //invalidate();
            return true;
        }
    }

    private void cropClick(){
        Log.d(TAG,"cropClick");
        Bitmap resultBitmap = imgView.getCroppedImage();
        if (resultBitmap!=null) {
            Log.d(TAG,"resultBitmap =  no null");
            Intent intent = new Intent();
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            resultBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bs);
            intent.putExtra(BITMAP_KEY, bs.toByteArray());
            setResult(Activity.RESULT_OK, intent);
            Log.d(TAG,"cree todo el intent y el result");
        }else{
            Log.d(TAG,"resultBitmap = null");
        }

        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitmap_cropper);
        imgView= (BitmapCropperView) findViewById(R.id.bitmapCropperView);
        cropButton = (Button)findViewById(R.id.cropButton);
        cancelButton= (Button)findViewById(R.id.cancelButton);

        cropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropClick();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        if (savedInstanceState!=null){

        }
        Intent intent = getIntent();
        if (intent!=null){
            Log.d(TAG, "Intent no es Null");
            try{

                Log.d(TAG,"trying");
                byte[] byteArray = intent.getByteArrayExtra(BITMAP_KEY);
                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
                imgView.setImageBitmap(bitmap);

            }catch (Exception e){
                Log.d(TAG,"Hubo un error " + e.getMessage());
                finish();
            }

        }else{
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bitmap_cropper, menu);
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
}
