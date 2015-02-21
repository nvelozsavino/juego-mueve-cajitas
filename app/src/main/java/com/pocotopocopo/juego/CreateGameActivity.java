package com.pocotopocopo.juego;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;


public class CreateGameActivity extends BaseActivity implements CountDownPickerDialog.CountDownPickerListener {
    private static final String ORIGINAL_IMAGE = "ORIGINAL_IMAGE";
    private BitmapCropperView imgView;

    public static final String BITMAP_KEY = "BitmapKey";
    public static final String TAG = "CreateGameActivity";
    ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;
    private static Integer INVALID_POINTER_ID = null;
    private Integer mActivePointerId = INVALID_POINTER_ID;
    private float mLastTouchX,mLastTouchY;

    private ImageView newImageButton;
    private ImageView rotateCCW;
    private ImageView rotateCW;
    private ImageView colsPlus;
    private ImageView colsMinus;
    private ImageView rowsPlus;
    private ImageView rowsMinus;
    private TextView colsText;
    private TextView rowsText;
    private Bitmap originalBitmap;

    private Uri imageUri;

//    private RadioGroup gameModeRadioGroup;
//    private RadioButton gameModeTraditionalRadioButton;
//    private RadioButton gameModeSpeedRadioButton;
//    private RadioButton gameModeMultiplayerRadioButton;
//    private CheckBox showNumbersCheckBox;
    private TitledButton showNumbersButton;
    private TitledButton backgroundModeButton;


    private TitledButton traditionalButton;
    private TitledButton speedButton;
    private TitledButton multiplayerButton;


    private LinearLayout imageButtonsLayout;
    private float totalRotation=0;

    public static final int REQUEST_IMAGE_CROP = 4;
    public static final int RESULT_LOAD_IMG = 2;
    private GameInfo gameInfo;
    //private GameActivity nextActivity;
    private int screenWidth;
    private int screenHeight;
    private static final float CCW = -90;
    private static final float CW = +90;
    private int cols=0;
    private int rows = 0;
    private boolean showNumbers=true;

    private long timeForSpeed=GameConstants.DEFAULT_TIME_SPEED;

    private void showTimeDialog(){
        CountDownPickerDialog countDownPickerDialog = new CountDownPickerDialog();

        if (timeForSpeed>0){
            int minutes, seconds;
            minutes=(int)(timeForSpeed/60);
            seconds=(int)(timeForSpeed%60);
            Bundle bundle = new Bundle();
            bundle.putInt(CountDownPickerDialog.MINUTES_KEY,minutes);
            bundle.putInt(CountDownPickerDialog.SECONDS_KEY,seconds);
            countDownPickerDialog.setArguments(bundle);
        }
        countDownPickerDialog.show(getSupportFragmentManager(),"CountDownPicker");
    }


    @Override
    public void onTimeSaved(int minutes, int seconds) {
        timeForSpeed = (minutes*60+seconds);
        gameInfo.setTimeForSpeed(timeForSpeed);
        // gameModeRadioGroup.check(R.id.gameModeSpeedRadioButton);
        gameInfo.setGameMode(GameMode.SPEED);
        setSpeedTime();
    }

    @Override
    public void onTimeSelected(int minutes, int seconds) {
        timeForSpeed = (minutes*60+seconds);
        gameInfo.setTimeForSpeed(timeForSpeed);
       // gameModeRadioGroup.check(R.id.gameModeSpeedRadioButton);
        gameInfo.setGameMode(GameMode.SPEED);
        setSpeedTime();
        startGame();
    }



    private void startGame(){
        Log.d(TAG,"startGame");
        GameActivity nextActivity = GameActivity.getGameActivity(gameInfo.getGameMode());
        Intent intent = new Intent(getApplicationContext(),nextActivity.getActivityClass());
        if (gameInfo.getBackgroundMode().equals(BackgroundMode.IMAGE)) {
            Bitmap resultBitmap = imgView.getCroppedImage();
            if (resultBitmap != null) {
                Log.d(TAG, "resultBitmap =  no null");
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                Log.d(TAG, "bitmap before compression = " + resultBitmap.getByteCount());
                resultBitmap.compress(Bitmap.CompressFormat.JPEG, 50, bs);
                Log.d(TAG, "bitmap after compression = " + resultBitmap.getByteCount());
                gameInfo.setBitmap(resultBitmap);

            } else {
                Log.e(TAG, "Error, resultBitmap = null");
                finish();

            }
        }
        intent.putExtra(GameConstants.GAME_INFO, gameInfo);

        Log.d(TAG, "cree todo el intent y el result");

        startActivity(intent);



    }
//
//    public static Intent requestImageCrop (Context context, Bitmap bitmap){
//        Intent intent = new Intent(context,CreateGameActivity.class);
//        ByteArrayOutputStream bs = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG,50,bs);
////        Log.d(TAG, "bitmap count nuevo = " + bitmap.getByteCount());
//        intent.putExtra(CreateGameActivity.BITMAP_KEY,bs.toByteArray());
////        Log.d(TAG,"puse el bitmap en el intent");
//        return intent;
//    }
//
//    public static Bitmap getBitmapCropped (Intent data){
////            Log.d(TAG,"Activity Result Request Image crop");
//            byte[] byteArray = data.getByteArrayExtra(CreateGameActivity.BITMAP_KEY);
////            Log.d(TAG,"tengo el byte array");
//            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
//            return bitmap;
////            Log.d(TAG,"tengo el bitmap");
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_game_layout);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;


        imgView= (BitmapCropperView) findViewById(R.id.bitmapCropperView);
        traditionalButton = (TitledButton)findViewById(R.id.traditionalButton);
        speedButton= (TitledButton)findViewById(R.id.speedButton);
        multiplayerButton= (TitledButton)findViewById(R.id.multiplayerButton);
        newImageButton=(ImageView)findViewById(R.id.newImageButton);
        rotateCCW = (ImageView)findViewById(R.id.rotateCCW);
        rotateCW = (ImageView)findViewById(R.id.rotateCW);
        colsPlus = (ImageView) findViewById(R.id.colsPlus);
        colsMinus = (ImageView) findViewById(R.id.colsMinus);
        rowsPlus = (ImageView) findViewById(R.id.rowsPlus);
        rowsMinus = (ImageView) findViewById(R.id.rowsMinus);
        colsText = (TextView)findViewById(R.id.colsText);
        rowsText = (TextView)findViewById(R.id.rowsText);

        imageButtonsLayout = (LinearLayout)findViewById(R.id.imageButtonsLayout);
        initViews();


        backgroundModeButton=(TitledButton)findViewById(R.id.backgroundButton);
        showNumbersButton=(TitledButton)findViewById(R.id.showNumbersButton);

        showNumbersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameInfo.setNumbersVisible(!gameInfo.isNumbersVisible());
                updateShowNumbers();
            }
        });

        backgroundModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (gameInfo.getBackgroundMode()){
                    default:
                    case PLAIN:
                        gameInfo.setBackgroundMode(BackgroundMode.IMAGE);

                        if (originalBitmap!=null){
                            setImage(originalBitmap);
                        } else {
                            startSelectImage();
                        }
                        break;
                    case IMAGE:
                        gameInfo.setBackgroundMode(BackgroundMode.VIDEO);
                        gameInfo.setBitmap(null);

                        break;
                    case VIDEO:
                        gameInfo.setBackgroundMode(BackgroundMode.PLAIN);

                        gameInfo.setBitmap(null);
                        break;
                }
                updateBackground();

            }
        });






        traditionalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameInfo.setGameMode(GameMode.TRADITIONAL);
                startGame();
            }
        });
        speedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameInfo.setGameMode(GameMode.SPEED);
                showTimeDialog();
                //startGame();
            }
        });

        multiplayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameInfo.setGameMode(GameMode.MULTIPLAYER);
                startGame();
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
                rotateImage(CCW, true);
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

        Intent intent = getIntent();
        boolean fromOtherActivity=false;

        if (intent!=null) {
            String action=intent.getAction();
            Bundle extras=intent.getExtras();
            if (Intent.ACTION_SEND.equals(action)){
                fromOtherActivity=true;
                gameInfo = new GameInfo(4,4,BackgroundMode.IMAGE,GameMode.TRADITIONAL,false);

                handleSendImage(intent);
            } else {
                if (extras != null) {

                    Log.d(TAG, "Intent no es Null");
                    if (extras.containsKey(GameConstants.GAME_INFO)) {
                        gameInfo = extras.getParcelable(GameConstants.GAME_INFO);


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
            }


        } else {
            Log.d(TAG,"saveIntanceState");
            gameInfo=savedInstanceState.getParcelable(GameConstants.GAME_INFO);
            originalBitmap = savedInstanceState.getParcelable(ORIGINAL_IMAGE);
            imgView.setRectScaleFactor(savedInstanceState.getFloat(GameConstants.RECT_SCALE_FACTOR));
            imgView.setRectLeftNorm(savedInstanceState.getFloat(GameConstants.RECT_LEFT_NORM));
            imgView.setRectTopNorm(savedInstanceState.getFloat(GameConstants.RECT_TOP_NORM));
            totalRotation = savedInstanceState.getFloat(GameConstants.ROTATION);
            imageUri = savedInstanceState.getParcelable(GameConstants.IMAGE_URI);

//            rotateImage(totalRotation,false);
        }

        setAccordingGameInfo();


    }
    private void updateShowNumbers() {
        if (gameInfo.isNumbersVisible()){
            showNumbersButton.setAdditionalText(getString(R.string.yes_showing_numbers));
            showNumbersButton.setIconResource(R.drawable.shownumbers32);
        } else {
            showNumbersButton.setAdditionalText(getString(R.string.not_showing_numbers));
            showNumbersButton.setIconResource(R.drawable.noshownumbers32);
        }
    }

    private void setAccordingGameInfo(){
        cols = gameInfo.getCols();
        rows = gameInfo.getRows();
        colsText.setText(Integer.toString(cols));
        rowsText.setText(Integer.toString(rows));
        updateShowNumbers();

        updateBackground();
        imgView.setCols(gameInfo.getCols());
        imgView.setRows(gameInfo.getRows());
        setSpeedTime();
        if (imageUri!=null) {
            this.new GetImageTask(imageUri).execute();
        }
    }

    private void setSpeedTime() {
        int minutes, seconds;
        minutes = (int) (timeForSpeed / 60);
        seconds = (int) (timeForSpeed % 60);
        speedButton.setAdditionalText(String.format("%01d:%02d", minutes,seconds));
    }


    private void updateBackground() {
        switch (gameInfo.getBackgroundMode()){
            case PLAIN:
                backgroundModeButton.setAdditionalText(getString(R.string.game_background_plain));
                backgroundModeButton.setIconResource(R.drawable.plain);
                imageButtonsLayout.setVisibility(View.GONE);
                multiplayerButton.setVisibility(View.VISIBLE);
                clearBitmap();
                break;
            case IMAGE:
                backgroundModeButton.setAdditionalText(getString(R.string.game_background_fixed_image));
                backgroundModeButton.setIconResource(R.drawable.picture);
                imageButtonsLayout.setVisibility(View.VISIBLE);
                multiplayerButton.setVisibility(View.VISIBLE);
                break;
            case VIDEO:
                backgroundModeButton.setAdditionalText(getString(R.string.game_background_video));
                backgroundModeButton.setIconResource(R.drawable.video);
                imageButtonsLayout.setVisibility(View.GONE);
                clearBitmap();
                multiplayerButton.setVisibility(View.GONE);

                break;

        }
    }

    private void clearBitmap() {
        imgView.setImageBitmap(null);
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
            rotateImage(totalRotation,false);
            //setAccordingGameInfo();
        }
    }

    void handleSendImage(Intent intent) {
        imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        totalRotation=0;

        if (imageUri != null) {
            this.new GetImageTask(imageUri).execute();
            // Update UI to reflect image being shared
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(GameConstants.GAME_INFO,gameInfo);
        outState.putFloat(GameConstants.RECT_SCALE_FACTOR,imgView.getRectScaleFactor());
        outState.putFloat(GameConstants.RECT_LEFT_NORM,imgView.getRectLeftNorm());
        outState.putFloat(GameConstants.RECT_TOP_NORM,imgView.getRectTopNorm());
        outState.putFloat(GameConstants.ROTATION,totalRotation);
        outState.putParcelable(ORIGINAL_IMAGE,originalBitmap);
        outState.putParcelable(GameConstants.IMAGE_URI, imageUri);

        super.onSaveInstanceState(outState);

    }

    private void startSelectImage() {


        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//  Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

/*    private void setImage(){
        int width, height;
        width=height=Math.min(screenWidth,screenHeight);
        Log.d(TAG,"Screen w=" + width + " h="+height);
        Bitmap bitmap =  compressBitmap(imageUri, width,height);
        setImage(bitmap);
    }
    */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG,"onActivityResult");
        if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                && null != data) {
            imageUri = data.getData();
            totalRotation=0;
            if (imageUri!=null) {
                this.new GetImageTask(imageUri).execute();
            }





        } else {
            if(imgView.getImageBitmap()==null){
                gameInfo.setBackgroundMode(BackgroundMode.PLAIN);
                updateBackground();
            }
        }
    }

    private void setImage(Bitmap bitmap){
        if (bitmap!=null) {
            imgView.setImageBitmap(bitmap);
            gameInfo.setBitmap(bitmap);
            originalBitmap=bitmap;
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
