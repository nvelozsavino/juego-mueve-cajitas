package com.pocotopocopo.juego;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class PuzzleActivity extends BaseActivity{

    private static final String TAG="Juego.MainActivity";
    private static final String MOVES_COUNTER_KEY = "movesCount";
    private static final String BITMAP_KEY = "bitmapContainer";
    private static final String POS_KEY = "posNumbers";
    private static final String LIVEFEED_KEY = "liveFeed";
    private static final String OUTPUTFILE_KEY = "outputFileKey";
    private AudioManager audioManager;
    private SoundPool soundPool;
    private float volume,actVolume,maxVolume;
    private boolean loadedSound=false;
    private Uri outputFileUri;
    private TextView moveCounterText;
    private MilliSecondChronometer chrono;
//    private TextView resolvableText;

    private int moveCounter = 0;
    private Camera camera=null;
    private byte[] cameraData = null;
    private boolean liveFeedEnabled=false;
    private boolean liveFeedState=false;
//    private Button liveFeedButton;
    private Handler handler;
    private SurfaceTexture dummySurfaceTexture;
    private int clickId;
    private boolean startedGame=false;
    private TextView countDownText;
    CountDownTimer countDownTimer;
    private GameInfo gameInfo;

    //private GoogleApiClient googleApiClient;


    private BitmapContainer bitmapContainer;
//    private Button selectImageButton;
//    private Button selectImageButton2;

    static final int REQUEST_IMAGE_CAPTURE = 1;
//    static final int RESULT_LOAD_IMG = 2;
    static final int RESULT_LOAD_IMG2 = 3;


    private Puzzle puzzle;
    //private LinearLayout frame;

    @Override
    protected void initViews(){
        super.initViews();
//        selectImageButton = (Button) findViewById(R.id.selectImage);
//        selectImageButton2 = (Button) findViewById(R.id.selectImage2);

        puzzle = (Puzzle)findViewById(R.id.puzzle);
        //frame = (LinearLayout) findViewById(R.id.frame);
        moveCounterText = (TextView)findViewById(R.id.moveCounterText);
        moveCounterText.setText(getString(R.string.moves_text,0));
        chrono = (MilliSecondChronometer)findViewById(R.id.timerView);

//        resolvableText = (TextView)findViewById(R.id.resolvableText);
//        liveFeedButton = (Button)findViewById(R.id.liveFeedButton);

    }
//
//    private void setClickListeners(){
////        liveFeedButton.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                puzzle.update();
////                if (!liveFeedEnabled) {
////                    liveFeedEnabled = startLiveFeed();
////                } else {
////                    stopLiveFeed();
////
////                }
////            }
////        });
////        selectImageButton.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
//////                openImageIntent();
////                startSelectImage();
////            }
////        });
////        selectImageButton2.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
//////                openImageIntent();
////                startSelectImage2();
////            }
////        });
//    }

//    private void startSelectImage2(){
//        stopLiveFeed();
//        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
//                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(galleryIntent, RESULT_LOAD_IMG2);
//    }
//    private void startSelectImage() {
//        stopLiveFeed();
//
////                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
////                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
////                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
////                }
//        // Create intent to Open Image applications like Gallery, Google Photos
//        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
//                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//// Start the Intent
//        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
//    }
//
//
//    private void openImageIntent() {
//
//// Determine Uri of camera image to save.
//        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "MyDir" + File.separator);
//        root.mkdirs();
//            final String fName = "img_"+ System.currentTimeMillis() + ".jpg";
////
////        final String fName;
////        try {
////            ;
////        } catch (IOException e) {
////            Log.d(TAG, "algo paso con el nombre " + e.toString());
////        }
//        final File sdImageMainDirectory = new File(root, fName);
//        outputFileUri = Uri.fromFile(sdImageMainDirectory);
//
//        // Camera.
//        final List<Intent> cameraIntents = new ArrayList<>();
//        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//        final PackageManager packageManager = getPackageManager();
//        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
//        for(ResolveInfo res : listCam) {
//            final String packageName = res.activityInfo.packageName;
//            final Intent intent = new Intent(captureIntent);
//            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
//            intent.setPackage(packageName);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
//            cameraIntents.add(intent);
//        }
//
//        // Filesystem.
//        final Intent galleryIntent = new Intent();
//        galleryIntent.setType("image/*");
//        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
//
//        // Chooser of filesystem options.
//        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");
//
//        // Add the camera options.
//        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));
//
//        startActivityForResult(chooserIntent, RESULT_LOAD_IMG);
//    }

//        // Camera.
//        final List<Intent> cameraIntents = new ArrayList<>();
//        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//        final PackageManager packageManager = getPackageManager();
//        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
//        for(ResolveInfo res : listCam) {
//            final String packageName = res.activityInfo.packageName;
//            final Intent intent = new Intent(captureIntent);
//            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
//            intent.setPackage(packageName);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
//            cameraIntents.add(intent);
//        }
//
//        // Filesystem.
//        final Intent galleryIntent = new Intent();
//        galleryIntent.setType("image/*");
//        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
//
//        // Chooser of filesystem options.
//        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");
//
//        // Add the camera options.
//        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));
//
//        startActivityForResult(chooserIntent, RESULT_LOAD_IMG);
//    }


    private void loadSounds(){
        audioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
        actVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volume = actVolume/maxVolume;
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        soundPool = new SoundPool(10,AudioManager.STREAM_MUSIC,0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loadedSound= true;
                if (!startedGame){
//                    chrono.start();
                    startedGame=true;
                }
            }
        });
        loadedSound=false;
        clickId = soundPool.load(getApplicationContext(),R.raw.click,1);
//        loadedSound=false;
//        musicId = soundPool.load(getApplicationContext(),R.raw.music,2);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        chrono.pause();
        final Dialog pauseDialog = new Dialog(PuzzleActivity.this);
        pauseDialog.setContentView(R.layout.pause_screen_layout);
        pauseDialog.setTitle(R.string.paused_text);
        pauseDialog.setCancelable(false);
        Button resumeButton = (Button)pauseDialog.findViewById(R.id.resumeButton);
        Button exitGameButton = (Button)pauseDialog.findViewById(R.id.exitGameButton);
        resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chrono.resume();
                pauseDialog.cancel();
            }
        });
        exitGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseDialog.cancel();
                finish();
            }
        });
        pauseDialog.show();
//        chrono.resume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Contacts: ********************************************* STARTING **********************************");
        setContentView(R.layout.activity_main);
        Log.d(TAG,"setContentView");

        initViews();
        loadSounds();



        Log.d(TAG, "capturando intent");
        Intent intent = getIntent();
        Log.d(TAG,"capture intent");
        if (intent!=null && intent.getExtras()!=null) {
            Bundle extras = intent.getExtras();
            if (extras.containsKey(GameConstants.GAME_INFO)) {
                gameInfo = extras.getParcelable(GameConstants.GAME_INFO);
            } else {
                Log.e(TAG, "Error, no gameInfo in intent");
                finish();
                return;
            }
        } else {
            Log.e(TAG, "Error, null intent");
            finish();
            return;
        }
        final Dialog countDownDialog = new Dialog(PuzzleActivity.this);
        countDownDialog.setContentView(R.layout.count_down);
        countDownDialog.setCancelable(false);
        countDownDialog.setTitle(R.string.game_start_in);
        countDownText = (TextView) countDownDialog.findViewById(R.id.countDownText);

        countDownTimer = new CountDownTimer(4000,200) {
            @Override
            public void onTick(long millisUntilFinished) {
                try{
                    int time = (int)(millisUntilFinished/1000);
                    countDownText.setText(Integer.toString(time));
                }catch(Exception e){
                    Log.d(TAG,e.getMessage());
                }


            }

            @Override
            public void onFinish() {
                countDownDialog.cancel();
                chrono.start();
            }
        };
        Log.d(TAG,"cree el countdowntimer");
        countDownDialog.show();
        Log.d(TAG,"mostre el dialog");

        countDownTimer.start();
        Log.d(TAG,"arranque el countdown timer");
//        chrono.start();
//        puzzle = new BoxPuzzle(this, cols, rows);
//        puzzle.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
//        frame.addView((puzzle));
        handler = new Handler(Looper.getMainLooper());
//        setClickListeners();

        dummySurfaceTexture=new SurfaceTexture(0);

        bitmapContainer = new BitmapContainer();

        int cols = gameInfo.getCols();
        int rows= gameInfo.getRows();
        boolean numbersVisible = gameInfo.isNumbersVisible();
        GameMode gameMode = gameInfo.getGameMode();
        BackgroundMode backgroundMode = gameInfo.getBackgroundMode();

        puzzle.setSize(cols, rows);
        puzzle.setNumbersVisible(numbersVisible);


        puzzle.setBitmapContainer(bitmapContainer);
        Log.d(TAG,"bitmapcontainer = null");

        puzzle.setBitmapContainer(bitmapContainer);
        bitmapContainer.setBitmap(gameInfo.getBitmap());

        puzzle.setOnMovePieceListener(new Puzzle.OnMovePieceListener() {
            @Override
            public void onPieceMoved() {
                moveCounterText.setText(getString(R.string.moves_text,++moveCounter));
                if (loadedSound){
                    soundPool.play(clickId,volume,volume,2,0,1f);
                }
            }

            @Override
            public void onPuzzleSolved() {
                chrono.stop();
                if (camera!=null){
                    stopLiveFeed();
                }

                final Dialog winDialog = new Dialog(PuzzleActivity.this);
                winDialog.setCancelable(false);
                winDialog.setContentView(R.layout.win_screen_layout);
                winDialog.setTitle(R.string.congratulation_text);
                TextView movesWinText = (TextView) winDialog.findViewById(R.id.movesWinText);
                TextView timeWinText = (TextView) winDialog.findViewById(R.id.timeWinText);
                ImageView winImage = (ImageView) winDialog.findViewById(R.id.winImage);
                Button exitButton = (Button)winDialog.findViewById(R.id.exitWinScreenButton);
                Bitmap bitmap = puzzle.getBitmapContainer().getBitmap();
                if (bitmap==null){
                    winImage.setImageDrawable(getDrawable(R.drawable.trophy));
                }else{

                    winImage.setImageBitmap(puzzle.getBitmapContainer().getBitmap());
                }
                movesWinText.setText(getString(R.string.moves_text,moveCounter));
                timeWinText.setText(getString(R.string.time_text_win,chrono.getText().toString()));
                exitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        winDialog.cancel();
                        finish();
                    }
                });
                winDialog.show();
//                Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.congratulation_text)  + chrono.getText().toString() ,Toast.LENGTH_LONG);
//                toast.show();

            }
        });


        if (savedInstanceState!=null){


            gameInfo = savedInstanceState.getParcelable(GameConstants.GAME_INFO);

            int[] posArray= gameInfo.getPieceOrder();


            bitmapContainer.setBitmap(gameInfo.getBitmap());
            puzzle.setPositions(posArray);
            puzzle.update();
//
//            Log.d(TAG,"moveCounter = " +moveCounter );
//            bitmapContainer.setBitmap((Bitmap)savedInstanceState.getParcelable(BITMAP_KEY));
//            //puzzle.setBitmapContainer();
//            Log.d(TAG,"Resetie el bitmapContainer" );
//            puzzle.setPositions(savedInstanceState.getIntArray(POS_KEY));
//            Log.d(TAG,"resetie las posiciones");
            moveCounter=savedInstanceState.getInt(MOVES_COUNTER_KEY);
            moveCounterText.setText("Movimientos = " + moveCounter);
            liveFeedState=savedInstanceState.getBoolean(LIVEFEED_KEY);
//            outputFileUri = (Uri)savedInstanceState.getParcelable(OUTPUTFILE_KEY);
        } else {
            puzzle.update();
//            switch (backgroundMode){
//                default:
//                case PLAIN:
//                    liveFeedState=false;
//                    bitmapContainer.setBitmap(null);
//                    break;
//                case IMAGE:
//                    puzzle.update();
//                    liveFeedState=false;
////                    openImageIntent();
//                    startSelectImage();
//                    break;
//                case VIDEO:
//                    puzzle.update();
//                    liveFeedEnabled=startLiveFeed();
//                    break;
//            }
        }

    }

    @Override
    protected void onDestroy() {
        soundPool.unload(clickId);
        super.onDestroy();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode,resultCode,data);
//        Log.d(TAG,"onActivityResult " + requestCode + " - " +resultCode);
//        if (requestCode==REQUEST_IMAGE_CAPTURE && resultCode==RESULT_OK){
//            Log.d(TAG,"ok");
//            Bundle extras = data.getExtras();
//            Bitmap bitmap = (Bitmap) extras.get("data");
//            Log.d(TAG,"bitmapContainer loaded? " + (bitmap!=null));
//            Bitmap oldBitmap= bitmapContainer.getBitmap();
//            bitmapContainer.setBitmap(bitmap);
////            puzzle.setBitmapContainer(bitmapContainer);
//            if(oldBitmap==null){
//                Log.d(TAG,"oldBitmap = null");
//                puzzle.update();
//
//            }
//        }
//        if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
//                && null != data) {
//
//            Bitmap bitmap =  activityResultChooseImage2(data, puzzle.getWidth(), puzzle.getHeight());
//            if (bitmap!=null) {
//                if (bitmap.getWidth()==bitmap.getHeight()) {
//                    bitmapContainer.setBitmap(bitmap);
//                    puzzle.setBitmapContainer(bitmapContainer);
//                    puzzle.update();
//                }else{
//                    Intent intent = BitmapChooserActivity.requestImageCrop(this, bitmap);
//                    startActivityForResult(intent, BitmapChooserActivity.REQUEST_IMAGE_CROP);
//                }
//            }else{
//                puzzle.setNumbersVisible(true);
//            }
//
//        }
//        if (requestCode == BitmapChooserActivity.REQUEST_IMAGE_CROP && resultCode == RESULT_OK) {
//            Bitmap bitmap = BitmapChooserActivity.getBitmapCropped(data);
//            if (bitmap != null) {
//                bitmapContainer.setBitmap(bitmap);
//                puzzle.setBitmapContainer(bitmapContainer);
//                puzzle.update();
//            } else {
//                puzzle.setNumbersVisible(true);
//                Log.d(TAG, "bitmap era null");
//            }
//        }
//
//    }
//
//
//
//    private Bitmap activityResultChooseImage2(Intent data, int rewWidth, int reqHeight){
//        Log.d(TAG,"activityResultChooseImage2");
//        Uri selectedImage = data.getData();
//        String[] filePathColumn = { MediaStore.Images.Media.DATA };
//
//        // Get the cursor
//        Cursor cursor = getContentResolver().query(selectedImage,
//                filePathColumn, null, null, null);
//        // Move to first row
//        cursor.moveToFirst();
//
//        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//        String imgDecodableString = cursor.getString(columnIndex);
//        cursor.close();
//        Bitmap bitmap = BitmapCompressor.decodeSampledBitmapFromFile(imgDecodableString,rewWidth,reqHeight);
//
//        return bitmap;
//
//    }
//
//    private Bitmap activityResultChooseImage (Intent data, int rewWidth, int reqHeight){
//        final boolean isCamera;
//        if(data == null)
//        {
//            isCamera = true;
//        }
//        else
//        {
//            final String action = data.getAction();
//            if(action == null)
//            {
//                isCamera = false;
//            }
//            else
//            {
//                isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//            }
//
//        }
//
//        Uri selectedImageUri;
//        if(isCamera)
//        {
//            selectedImageUri = outputFileUri;
//        }
//        else
//        {
//            selectedImageUri = data == null ? null : data.getData();
//        }
//        String[] filePathColumn = { MediaStore.Images.Media.DATA };
//
//        // Get the cursor
//        Cursor cursor = getContentResolver().query(selectedImageUri,
//                filePathColumn, null, null, null);
//        // Move to first row
//        cursor.moveToFirst();
//
//        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//        String imgDecodableString = cursor.getString(columnIndex);
//        cursor.close();
//        Bitmap bitmap = BitmapCompressor.decodeSampledBitmapFromFile(imgDecodableString,rewWidth,reqHeight);
//
//        return bitmap;
//
//    }

    private boolean startLiveFeed(){
        if (camera!=null){
            stopLiveFeed();
        }
        camera=Camera.open();
        try {
            Camera.Parameters parameters = camera.getParameters();
            parameters.setPreviewSize(puzzle.getWidth(),puzzle.getHeight());
            camera.setPreviewTexture(dummySurfaceTexture);
            camera.startPreview();
            camera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    cameraData=data;
                    handler.post(liveFeed);


                }
            });

        } catch (Exception e){

        }
        return (camera!=null);
    }

    private void stopLiveFeed(){
        if (camera!=null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            handler.removeCallbacks(liveFeed);

            camera = null;
        }
        liveFeedEnabled=false;
    }

    private Runnable liveFeed = new Runnable() {
        @Override
        public void run() {
            if (camera!=null) {
                Camera.Size previewSize = camera.getParameters().getPreviewSize();
                YuvImage yuvImage = new YuvImage(cameraData, ImageFormat.NV21, previewSize.width, previewSize.height, null);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                yuvImage.compressToJpeg(new Rect(0, 0, previewSize.width, previewSize.height), 80, baos);
                byte[] jdata = baos.toByteArray();
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                Bitmap bitmap = BitmapFactory.decodeByteArray(jdata, 0, jdata.length);
                Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap , 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);


                Bitmap oldBitmap= bitmapContainer.getBitmap();

                bitmapContainer.setBitmap(rotatedBitmap);
//                puzzle.setBitmapContainer(bitmapContainer);
                if(oldBitmap==null){
                    puzzle.update();

                }
            }
        }
    };

    @Override
    protected void onResume() {

        super.onResume();
        if (liveFeedState){
            liveFeedEnabled=startLiveFeed();
        }
    }

    @Override
    protected void onPause() {
        liveFeedState=liveFeedEnabled;
        stopLiveFeed();

        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        gameInfo.setPieceOrder(puzzle.getPositions());
        outState.putParcelable(GameConstants.GAME_INFO,gameInfo);
        outState.putInt(MOVES_COUNTER_KEY,moveCounter);
        //outState.putParcelable(BITMAP_KEY,puzzle.getBitmapContainer().getBitmap());
        //outState.putIntArray(POS_KEY,puzzle.getPositions());
        outState.putBoolean(LIVEFEED_KEY,liveFeedState);
//        outState.putParcelable(OUTPUTFILE_KEY,outputFileUri);
        super.onSaveInstanceState(outState);

    }



}
