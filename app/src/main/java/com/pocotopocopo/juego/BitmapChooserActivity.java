package com.pocotopocopo.juego;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;


public class BitmapChooserActivity extends BaseActivity {
    private BitmapCropperView imgView;

    public static final String BITMAP_KEY = "BitmapKey";
    public static final String TAG = "Juego.BitmapCropperActivity";
    ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;
    private static Integer INVALID_POINTER_ID = null;
    private Integer mActivePointerId = INVALID_POINTER_ID;
    private float mLastTouchX,mLastTouchY;
    private ImageView cropButton;
    private ImageView cancelButton;
    private ImageView newImageButton;
    private ImageView rotateCCW;
    private ImageView rotateCW;
    private ImageView colsPlus;
    private ImageView colsMinus;
    private ImageView rowsPlus;
    private ImageView rowsMinus;
    private TextView colsText;
    private TextView rowsText;

    private RadioGroup gameModeRadioGroup;
    private RadioButton gameModeTraditionalRadioButton;
    private RadioButton gameModeSpeedRadioButton;
    private RadioButton gameModeMultiplayerRadioButton;
    private CheckBox showNumbersCheckBox;


    private float totalRotation=0;

    public static final int REQUEST_IMAGE_CROP = 4;
    public static final int RESULT_LOAD_IMG = 2;
    private GameInfo gameInfo;
    private GameActivity nextActivity;
    private int screenWidth;
    private int screenHeight;
    private static final float CCW = 90;
    private static final float CW = -90;
    private int cols=0;
    private int rows = 0;



    private void cropClick(){
        Log.d(TAG,"cropClick");
        Bitmap resultBitmap = imgView.getCroppedImage();
        if (resultBitmap!=null) {
            Log.d(TAG,"resultBitmap =  no null");

            Intent intent = new Intent(getApplicationContext(),nextActivity.getActivityClass());
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            Log.d(TAG,"bitmap before compression = " + resultBitmap.getByteCount());
            resultBitmap.compress(Bitmap.CompressFormat.JPEG, 50, bs);
            Log.d(TAG, "bitmap after compression = " + resultBitmap.getByteCount());
            gameInfo.setBitmap(resultBitmap);
            intent.putExtra(GameConstants.GAME_INFO, gameInfo);

            Log.d(TAG, "cree todo el intent y el result");
            Log.d(TAG, nextActivity.toString());
            startActivity(intent);
            finish();
            //return;
        }else{
            Log.e(TAG,"Error, resultBitmap = null");
            finish();

        }

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

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;


        imgView= (BitmapCropperView) findViewById(R.id.bitmapCropperView);
        cropButton = (ImageView)findViewById(R.id.cropButton);
        cancelButton= (ImageView)findViewById(R.id.cancelButton);
        newImageButton=(ImageView)findViewById(R.id.newImageButton);
        rotateCCW = (ImageView)findViewById(R.id.rotateCCW);
        rotateCW = (ImageView)findViewById(R.id.rotateCW);
        colsPlus = (ImageView) findViewById(R.id.colsPlus);
        colsMinus = (ImageView) findViewById(R.id.colsMinus);
        rowsPlus = (ImageView) findViewById(R.id.rowsPlus);
        rowsMinus = (ImageView) findViewById(R.id.rowsMinus);
        colsText = (TextView)findViewById(R.id.colsText);
        rowsText = (TextView)findViewById(R.id.rowsText);

/*
        cropButton.setImageResource(R.drawable.ok_icon32);
        cancelButton.setImageResource(R.drawable.cancelicon32);
        newImageButton.setImageResource(R.drawable.openicon);
        rotateCCW.setImageResource(R.drawable.rotateccw);
        rotateCW.setImageResource(R.drawable.rotatecw);
        */

        gameModeRadioGroup=(RadioGroup)findViewById(R.id.gameModeRadioGroup);
        gameModeTraditionalRadioButton=(RadioButton)findViewById(R.id.gameModeTraditionalRadioButton);
        gameModeSpeedRadioButton=(RadioButton)findViewById(R.id.gameModeSpeedRadioButton);
        gameModeMultiplayerRadioButton=(RadioButton)findViewById(R.id.gameModeMultiplayerRadioButton);
        showNumbersCheckBox = (CheckBox)findViewById(R.id.showNumbersCheckBox);

        gameModeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    default:
                    case R.id.gameModeTraditionalRadioButton:
                        gameInfo.setGameMode(GameMode.TRADITIONAL);
                        break;
                    case R.id.gameModeSpeedRadioButton:
                        gameInfo.setGameMode(GameMode.SPEED);
                        break;
                    case R.id.gameModeMultiplayerRadioButton:
                        gameInfo.setGameMode(GameMode.MULTIPLAYER);
                        break;
                }
            }
        });

        showNumbersCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                gameInfo.setNumbersVisible(isChecked);
            }
        });




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
        newImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSelectImage();
            }
        });
        rotateCCW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotateImage(CCW,true);
            }
        });
        rotateCW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotateImage(CW,true);
            }
        });
        colsPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cols<10) {
                    cols++;
                }else{
                    cols=10;
                }
                gameInfo.setCols(cols);
                imgView.setCols(cols);
                colsText.setText(Integer.toString(cols));
            }
        });

        colsMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cols>3) {
                    cols--;
                }else{
                    cols=3;
                }
                gameInfo.setCols(cols);
                imgView.setCols(cols);
                colsText.setText(Integer.toString(cols));
            }

        });
        rowsPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rows<10) {
                    rows++;
                }else{
                    rows=10;
                }
                gameInfo.setRows(rows);
                imgView.setRows(rows);
                rowsText.setText(Integer.toString(rows));

            }
        });

        rowsMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rows>3) {
                    rows--;
                }else{
                    rows=3;
                }
                gameInfo.setRows(rows);
                imgView.setRows(rows);
                rowsText.setText(Integer.toString(rows));
            }
        });

        if (savedInstanceState!=null){

        }
        Intent intent = getIntent();
        boolean fromOtherActivity=false;

        if (intent!=null) {
            String action=intent.getAction();
            Bundle extras=intent.getExtras();
            if (Intent.ACTION_SEND.equals(action)){
                fromOtherActivity=true;
                gameInfo = new GameInfo(3,3,BackgroundMode.IMAGE,GameMode.TRADITIONAL,false);
                nextActivity = GameActivity.PUZZLE;
                handleSendImage(intent);
            } else {
                if (extras != null) {

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
            }
        }
        if (savedInstanceState==null) {
            if (gameInfo.getBackgroundMode().equals(BackgroundMode.IMAGE)) {
                if (!fromOtherActivity) {
                    startSelectImage();
                }
            } else {
                Log.e(TAG, "Error, not right background");
                finish();
                return;
            }



        } else {
            Log.d(TAG,"saveIntanceState");
            gameInfo=savedInstanceState.getParcelable(GameConstants.GAME_INFO);
            imgView.setRectScaleFactor(savedInstanceState.getFloat(GameConstants.RECT_SCALE_FACTOR));
            imgView.setRectLeftNorm(savedInstanceState.getFloat(GameConstants.RECT_LEFT_NORM));
            imgView.setRectTopNorm(savedInstanceState.getFloat(GameConstants.RECT_TOP_NORM));
            totalRotation = savedInstanceState.getFloat(GameConstants.ROTATION);
//            rotateImage(totalRotation,false);
        }
        setAccordingGameInfo();


    }


    private void setAccordingGameInfo(){
        cols = gameInfo.getCols();
        rows = gameInfo.getRows();
        colsText.setText(Integer.toString(cols));
        rowsText.setText(Integer.toString(rows));
        showNumbersCheckBox.setChecked(gameInfo.isNumbersVisible());

        switch (gameInfo.getGameMode()){
            default:
            case TRADITIONAL:
                gameModeTraditionalRadioButton.setChecked(true);
                gameModeSpeedRadioButton.setChecked(false);
                gameModeMultiplayerRadioButton.setChecked(false);
                break;
            case SPEED:
                gameModeTraditionalRadioButton.setChecked(false);
                gameModeSpeedRadioButton.setChecked(true);
                gameModeMultiplayerRadioButton.setChecked(false);

                break;
            case MULTIPLAYER:
                gameModeTraditionalRadioButton.setChecked(false);
                gameModeSpeedRadioButton.setChecked(false);
                gameModeMultiplayerRadioButton.setChecked(true);
                break;
        }
        imgView.setCols(gameInfo.getCols());
        imgView.setRows(gameInfo.getRows());
    }

    private void rotateImage(float rot, boolean updateTotalRotation){
        if (updateTotalRotation){
            totalRotation+=rot;
        }
        imgView.rotateBitmap(rot);
    }

    private class GetImageTask extends AsyncTask<Void,Void,Void>{
        private Uri imageUri;
        private Bitmap bitmap;
        private int width, height;

        public GetImageTask(Uri imageUri){
            this.imageUri=imageUri;
            Log.d(TAG,"Screen w=" + width + " h="+height);
            width=height=Math.min(screenWidth,screenHeight);
        }

        @Override
        protected Void doInBackground(Void... params) {



            bitmap =  compressBitmap(imageUri, width,height);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            setImage(bitmap);
            setAccordingGameInfo();
        }
    }

    void handleSendImage(Intent intent) {
        final Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);

        if (imageUri != null) {
            this.new GetImageTask(imageUri).execute();
            // Update UI to reflect image being shared
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Bitmap bitmap = gameInfo.getBitmap();
        if (bitmap!=null){
            imgView.setImageBitmap(bitmap);
            rotateImage(totalRotation,false);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(GameConstants.GAME_INFO,gameInfo);
        outState.putFloat(GameConstants.RECT_SCALE_FACTOR,imgView.getRectScaleFactor());
        outState.putFloat(GameConstants.RECT_LEFT_NORM,imgView.getRectLeftNorm());
        outState.putFloat(GameConstants.RECT_TOP_NORM,imgView.getRectTopNorm());
        outState.putFloat(GameConstants.ROTATION,totalRotation);


        super.onSaveInstanceState(outState);

    }

    private void startSelectImage() {

//                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//                }
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//  Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG,"onActivityResult");
        if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                && null != data) {
            Uri selectedImage = data.getData();

            int width, height;

            width=height=Math.min(screenWidth,screenHeight);
            Log.d(TAG,"Screen w=" + width + " h="+height);
            Bitmap bitmap =  compressBitmap(selectedImage, width,height);
            setImage(bitmap);

        } else {
            if(imgView.getImageBitmap()==null){
                finish();
            }
        }
    }

    private void setImage(Bitmap bitmap){
        if (bitmap!=null) {
            imgView.setImageBitmap(bitmap);
            gameInfo.setBitmap(bitmap);
        }else{
            Log.e(TAG, "Error, no image result");
            finish();
            return;
        }
    }




    private Bitmap compressBitmap(Uri imageUri, int rewWidth, int reqHeight){
        Log.d(TAG,"rewWidth: " + rewWidth+ ". reqHeight: " +reqHeight);
        Cursor cursor = null;
        try {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            // Get the cursor
            cursor= getContentResolver().query(imageUri,
                    filePathColumn, null, null, null);
            // Move to first row
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imgDecodableString = cursor.getString(columnIndex);
            Bitmap bitmap = BitmapCompressor.decodeSampledBitmapFromFile(imgDecodableString,rewWidth,reqHeight);
            Log.d(TAG,"ImageDecodableString: " + imgDecodableString + ". w=" +bitmap.getWidth()+ " h="+bitmap.getHeight());
            return bitmap;
        } finally {
            if (cursor!=null) {
                cursor.close();
            }
        }

    }
}
