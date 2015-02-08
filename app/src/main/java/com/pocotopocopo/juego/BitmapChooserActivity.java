package com.pocotopocopo.juego;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;

import java.io.ByteArrayOutputStream;


public class BitmapChooserActivity extends Activity {
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
    public static final int REQUEST_IMAGE_CROP = 4;
    public static final int RESULT_LOAD_IMG = 2;
    private GameInfo gameInfo;
    private GameActivity nextActivity;
    private int screenWidth;
    private int screenHeight;


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

            Intent intent = new Intent(getApplicationContext(),nextActivity.getActivityClass());
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            Log.d(TAG,"bitmap before compression = " + resultBitmap.getByteCount());
            resultBitmap.compress(Bitmap.CompressFormat.JPEG, 50, bs);
            Log.d(TAG,"bitmap after compression = " + resultBitmap.getByteCount());
            gameInfo.setBitmap(resultBitmap);
            intent.putExtra(GameConstants.GAME_INFO,gameInfo);
            Log.d(TAG,"cree todo el intent y el result");
        }else{
            Log.e(TAG,"Error, resultBitmap = null");
        }
        finish();
    }

    public static Intent requestImageCrop (Context context, Bitmap bitmap){
        Intent intent = new Intent(context,BitmapChooserActivity.class);
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,bs);
//        Log.d(TAG, "bitmap count nuevo = " + bitmap.getByteCount());
        intent.putExtra(BitmapChooserActivity.BITMAP_KEY,bs.toByteArray());
//        Log.d(TAG,"puse el bitmap en el intent");
        return intent;
    }

    public static Bitmap getBitmapCropped (Intent data){
//            Log.d(TAG,"Activity Result Request Image crop");
            byte[] byteArray = data.getByteArrayExtra(BitmapChooserActivity.BITMAP_KEY);
//            Log.d(TAG,"tengo el byte array");
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
            return bitmap;
//            Log.d(TAG,"tengo el bitmap");
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
        if (intent!=null && intent.getExtras()!=null) {
            Bundle extras = intent.getExtras();
            Log.d(TAG, "Intent no es Null");
            if (extras.containsKey(GameConstants.GAME_INFO) && extras.containsKey(GameConstants.NEXT_ACTIVITY)) {
                gameInfo = extras.getParcelable(GameConstants.GAME_INFO);
                nextActivity = (GameActivity) extras.getSerializable(GameConstants.NEXT_ACTIVITY);
            } else {
                Log.e(TAG, "Error, invalid intent");
                finish();
                return;
            }
        } else {
            Log.e(TAG, "Error, null intent");
            finish();
            return;
        }
        if (savedInstanceState==null) {
            if (gameInfo.getBackgroundMode().equals(BackgroundMode.IMAGE)) {
                startSelectImage();
            } else {
                Log.e(TAG, "Error, not right background");
                finish();
                return;
            }
        } else {
            //TODO: load instance state
        }
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;


    }


    private void startSelectImage() {

//                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//                }
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
// Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG,"onActivityResult");
        if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                && null != data) {

            int width, height;

            width=height=Math.min(screenWidth,screenHeight);
            Log.d(TAG,"w=" + width + " h="+height);
            Bitmap bitmap =  compressBitmap(data, width,height);
            if (bitmap!=null) {
//                if (bitmap.getWidth()==bitmap.getHeight()) {
//                    bitmapContainer.setBitmap(bitmap);
//                    puzzle.setBitmapContainer(bitmapContainer);
//                    puzzle.update();
//                }else{
//                    Intent intent = BitmapChooserActivity.requestImageCrop(this, bitmap);
//                    startActivityForResult(intent, BitmapChooserActivity.REQUEST_IMAGE_CROP);
//                }
                imgView.setImageBitmap(bitmap);
                imgView.invalidate();
            }else{
                Log.e(TAG, "Error, no image result");
                finish();
                return;
            }

        }
    }

    private Bitmap compressBitmap(Intent data, int rewWidth, int reqHeight){
        Log.d(TAG,"compressBitmap");
        Uri selectedImage = data.getData();
        String[] filePathColumn = { MediaStore.Images.Media.DATA };

        // Get the cursor
        Cursor cursor = getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        // Move to first row
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String imgDecodableString = cursor.getString(columnIndex);
        cursor.close();
        Bitmap bitmap = BitmapCompressor.decodeSampledBitmapFromFile(imgDecodableString,rewWidth,reqHeight);
        Log.d(TAG,"w=" +bitmap.getWidth()+ " h="+bitmap.getHeight());
        return bitmap;

    }
}
