package com.pocotopocopo.juego.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pocotopocopo.juego.BackgroundMode;
import com.pocotopocopo.juego.BackgroundPopUpWindow;
import com.pocotopocopo.juego.BitmapCompressor;
import com.pocotopocopo.juego.BitmapCropperView;
import com.pocotopocopo.juego.CountDownPickerDialog;
import com.pocotopocopo.juego.GameActivity;
import com.pocotopocopo.juego.GameConstants;
import com.pocotopocopo.juego.GameInfo;
import com.pocotopocopo.juego.GameMode;
import com.pocotopocopo.juego.R;
import com.pocotopocopo.juego.TitledButton;

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

    private long timeForSpeed= GameConstants.DEFAULT_TIME_SPEED;
    private boolean isMultiplayer=false;
    private LinearLayout signInLayout;




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

        if (isMultiplayer){
            Intent intentData = new Intent();
            intentData.putExtra(GameConstants.GAME_INFO,gameInfo);

            setResult(RESULT_OK, intentData);
            finish();
        } else {
            Intent intent = new Intent(getApplicationContext(), GameActivity.PUZZLE.getActivityClass());
            intent.putExtra(GameConstants.GAME_INFO, gameInfo);

            Log.d(TAG, "cree todo el intent y el result");

            startActivity(intent);
        }




    }



    @Override
    protected void initViews() {
        super.initViews();
        imgView= (BitmapCropperView) findViewById(R.id.bitmapCropperView);
        traditionalButton = (TitledButton)findViewById(R.id.traditionalButton);
        speedButton= (TitledButton)findViewById(R.id.speedButton);
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


        backgroundModeButton=(TitledButton)findViewById(R.id.backgroundButton);
        showNumbersButton=(TitledButton)findViewById(R.id.showNumbersButton);
        signInLayout = (LinearLayout)findViewById(R.id.signInLayout);

    }


    private void initListeners(){
        showNumbersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!gameInfo.getBackgroundMode().equals(BackgroundMode.PLAIN)) {
                    gameInfo.setNumbersVisible(!gameInfo.isNumbersVisible());
                    updateShowNumbers();
                }
            }
        });

        final BackgroundPopUpWindow backgroundPopUpWindow=new BackgroundPopUpWindow(getApplicationContext(),isMultiplayer,new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG,"click");
                BackgroundPopUpWindow.BackgroundOption item = (BackgroundPopUpWindow.BackgroundOption) parent.getAdapter().getItem(position);
                BackgroundMode backgroundMode = item.getMode();
                gameInfo.setBackgroundMode(backgroundMode);
                switch (backgroundMode){
                    case IMAGE:
                        gameInfo.setNumbersVisible(false);
                        if (originalBitmap!=null){
                            setImage(originalBitmap);
                        } else {
                            startSelectImage();
                        }
                        break;

                    default:
                    case PLAIN:
                        gameInfo.setNumbersVisible(true);
                        break;
                    case VIDEO:
                        gameInfo.setNumbersVisible(false);
                        gameInfo.setBitmap(null);
                        break;
                }
                updateBackground();
            }
        });


        backgroundModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                backgroundPopUpWindow.show(v);
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


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_game_activity_layout);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;



        initViews();






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

                    if (extras.containsKey(GameConstants.IS_MULTIPLAYER)){
                        isMultiplayer=extras.getBoolean(GameConstants.IS_MULTIPLAYER);
                    }
                    if (!isMultiplayer) {
                        Log.d(TAG, "Intent no es Null");
                        if (extras.containsKey(GameConstants.GAME_INFO)) {
                            gameInfo = extras.getParcelable(GameConstants.GAME_INFO);
                        } else {
                            Log.e(TAG, "Error, invalid intent");
                            finish();
                            return;
                        }
                    } else {
//                        if (extras.containsKey(GameConstants.MULTIPLAYER_MATCH)){
//                            match=extras.getParcelable(GameConstants.MULTIPLAYER_MATCH);
//                        } else {
//                            Log.e(TAG, "Error, invalid intent, no match");
//                            finish();
//                            return;
//                        }
                        gameInfo = new GameInfo(4,4,BackgroundMode.PLAIN,GameMode.MULTIPLAYER,true);
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
            isMultiplayer = savedInstanceState.getBoolean(GameConstants.IS_MULTIPLAYER);
            //match = savedInstanceState.getParcelable(GameConstants.MULTIPLAYER_MATCH);

//            rotateImage(totalRotation,false);
        }
        initListeners();
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
        imgView.setWithNumbers(gameInfo.isNumbersVisible());
        imgView.invalidate();
    }

    private void setAccordingGameInfo(){
        if (isMultiplayer){
            signInLayout.setVisibility(View.GONE);
            speedButton.setVisibility(View.GONE);
            traditionalButton.setTitleText(getString(R.string.start_game));
        } else {
            signInLayout.setVisibility(View.VISIBLE);
            speedButton.setVisibility(View.VISIBLE);
            traditionalButton.setTitleText(getString(R.string.game_mode_traditional));
        }
        cols = gameInfo.getCols();
        rows = gameInfo.getRows();
        colsText.setText(Integer.toString(cols));
        rowsText.setText(Integer.toString(rows));


        updateBackground();
        if (gameInfo.getBackgroundMode().equals(BackgroundMode.PLAIN)){
            gameInfo.setNumbersVisible(true);
        }
        updateShowNumbers();
        imgView.setCols(gameInfo.getCols());
        imgView.setRows(gameInfo.getRows());
        imgView.setWithNumbers(gameInfo.isNumbersVisible());

        setSpeedTime();
        if (gameInfo.getBackgroundMode().equals(BackgroundMode.IMAGE) && imageUri!=null) {
            this.new GetImageTask(imageUri).execute();
        }
        imgView.invalidate();
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
                gameInfo.setNumbersVisible(true);
                clearBitmap();
                break;
            case IMAGE:
                backgroundModeButton.setAdditionalText(getString(R.string.game_background_fixed_image));
                backgroundModeButton.setIconResource(R.drawable.picture);
                imageButtonsLayout.setVisibility(View.VISIBLE);
                gameInfo.setNumbersVisible(false);
                break;
            case VIDEO:
                backgroundModeButton.setAdditionalText(getString(R.string.game_background_video));
                backgroundModeButton.setIconResource(R.drawable.video);
                imageButtonsLayout.setVisibility(View.GONE);
                clearBitmap();
                gameInfo.setNumbersVisible(false);
                break;
        }
        updateShowNumbers();
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
        //outState.putParcelable(GameConstants.MULTIPLAYER_MATCH, match);
        outState.putBoolean(GameConstants.IS_MULTIPLAYER, isMultiplayer);


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
            Bitmap bitmap = BitmapCompressor.decodeSampledBitmapFromFile(imgDecodableString, rewWidth, reqHeight);
            Log.d(TAG,"ImageDecodableString: " + imgDecodableString + ". w=" +bitmap.getWidth()+ " h="+bitmap.getHeight());
            return bitmap;
        } finally {
            if (cursor!=null) {
                cursor.close();
            }
        }

    }

    @Override
    public void onBackPressed() {
        if (isMultiplayer){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            alertDialogBuilder.setMessage("Do you want to cancel the multiplayer match");

            alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                //Games.TurnBasedMultiplayer.cancelMatch(googleApiClient, match.getMatchId());
                                finish();
                            }
                        })
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });

            alertDialogBuilder.show();
        } else {
            super.onBackPressed();
        }
    }
}
