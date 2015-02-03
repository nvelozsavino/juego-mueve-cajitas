package com.pocotopocopo.juego;

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
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity{

    private static final String TAG="Juego.MainActivity";
    private static final String MOVES_COUNTER_KEY = "movesCount";
    private static final String BITMAP_KEY = "bitmapContainer";
    private static final String POS_KEY = "posNumbers";
    private static final String LIVEFEED_KEY = "liveFeed";
    private static final String OUTPUTFILE_KEY = "outputFileKey";


    private Uri outputFileUri;
    private TextView moveCounterText;
    private TextView resolvableText;

    private int moveCounter = 0;
    private Camera camera=null;
    private byte[] cameraData = null;
    private boolean liveFeedEnabled=false;
    private boolean liveFeedState=false;
    private Button liveFeedButton;
    private Handler handler;
    private SurfaceTexture dummySurfaceTexture;



    //private GoogleApiClient googleApiClient;


    private BitmapContainer bitmapContainer;
    private Button selectImageButton;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int RESULT_LOAD_IMG = 2;

    private Puzzle puzzle;
    //private LinearLayout frame;


    private void initViews(){
        selectImageButton = (Button) findViewById(R.id.selectImage);
        puzzle = (Puzzle)findViewById(R.id.puzzle);
        //frame = (LinearLayout) findViewById(R.id.frame);
        moveCounterText = (TextView)findViewById(R.id.moveCounterText);
//        resolvableText = (TextView)findViewById(R.id.resolvableText);
        liveFeedButton = (Button)findViewById(R.id.liveFeedButton);

    }

    private void setClickListeners(){
        liveFeedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                puzzle.update();
                if (!liveFeedEnabled) {
                    liveFeedEnabled = startLiveFeed();
                } else {
                    stopLiveFeed();

                }
            }
        });
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                openImageIntent();
                startSelectImage();
            }
        });
    }

    private void startSelectImage() {
        stopLiveFeed();

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


    private void openImageIntent() {

// Determine Uri of camera image to save.
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "MyDir" + File.separator);
        root.mkdirs();
            final String fName = "img_"+ System.currentTimeMillis() + ".jpg";
//
//        final String fName;
//        try {
//            ;
//        } catch (IOException e) {
//            Log.d(TAG, "algo paso con el nombre " + e.toString());
//        }
        final File sdImageMainDirectory = new File(root, fName);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for(ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));

        startActivityForResult(chooserIntent, RESULT_LOAD_IMG);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Contacts: ********************************************* STARTING **********************************");
        setContentView(R.layout.activity_main);
        Log.d(TAG,"setContentView");

        initViews();

//        puzzle = new BoxPuzzle(this, cols, rows);
//        puzzle.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
//        frame.addView((puzzle));
        handler = new Handler(Looper.getMainLooper());
        setClickListeners();

        dummySurfaceTexture=new SurfaceTexture(0);

        bitmapContainer = new BitmapContainer();

        BackgroundMode backgroundMode=BackgroundMode.PLAIN;
        GameMode gameMode=GameMode.TRADITIONAL;

        Log.d(TAG, "capturando intent");
        Intent intent = getIntent();
        Log.d(TAG,"capture intent");
        if (intent!=null) {
            Log.d(TAG,"intent no es null");
            try {
                int cols = intent.getExtras().getInt(StartScreen.COLS_KEY);
                int rows = intent.getExtras().getInt(StartScreen.ROWS_KEY);
                boolean numbersVisible= intent.getExtras().getBoolean(StartScreen.SHOW_NUMBERS);

                backgroundMode = (BackgroundMode)intent.getExtras().getSerializable(StartScreen.BACKGROUND_MODE);
                gameMode= (GameMode)intent.getExtras().getSerializable(StartScreen.GAME_MODE);

                Log.d(TAG, "cols = " + cols + " - rows = " + rows);

                puzzle.setSize(cols, rows);
                puzzle.setNumbersVisible(numbersVisible);
            }catch(Exception e){
                Log.d(TAG,"el intent no es "+ e.getMessage());
            }
        }
        puzzle.setBitmapContainer(bitmapContainer);
        Log.d(TAG,"bitmapcontainer = null");

        puzzle.setBitmapContainer(bitmapContainer);
        bitmapContainer.setBitmap(null);

        puzzle.setOnMovePieceListener(new Puzzle.OnMovePieceListener() {
            @Override
            public void onPieceMoved() {
                moveCounterText.setText("Movimientos: " + (++moveCounter));
            }

            @Override
            public void onPuzzleSolved() {
                Toast toast = Toast.makeText(getApplicationContext(),R.string.congratulation_text ,Toast.LENGTH_LONG);
                toast.show();
                if (camera!=null){
                    stopLiveFeed();
                }
            }
        });

        if (savedInstanceState!=null){
            moveCounter=savedInstanceState.getInt(MOVES_COUNTER_KEY);
            Log.d(TAG,"moveCounter = " +moveCounter );
            bitmapContainer.setBitmap((Bitmap)savedInstanceState.getParcelable(BITMAP_KEY));
            //puzzle.setBitmapContainer();
            Log.d(TAG,"Resetie el bitmapContainer" );
            puzzle.setPositions(savedInstanceState.getIntArray(POS_KEY));
            Log.d(TAG,"resetie las posiciones");
            moveCounterText.setText("Movimientos = " + moveCounter);
            liveFeedState=savedInstanceState.getBoolean(LIVEFEED_KEY);
//            outputFileUri = (Uri)savedInstanceState.getParcelable(OUTPUTFILE_KEY);
        } else {
            switch (backgroundMode){
                default:
                case PLAIN:
                    liveFeedState=false;
                    bitmapContainer.setBitmap(null);
                    break;
                case IMAGE:
                    puzzle.update();
                    liveFeedState=false;
//                    openImageIntent();
                    startSelectImage();
                    break;
                case VIDEO:
                    puzzle.update();
                    liveFeedEnabled=startLiveFeed();
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        Log.d(TAG,"onActivityResult " + requestCode + " - " +resultCode);
        if (requestCode==REQUEST_IMAGE_CAPTURE && resultCode==RESULT_OK){
            Log.d(TAG,"todo ok");
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");
            Log.d(TAG,"bitmapContainer loaded? " + (bitmap!=null));
            Bitmap oldBitmap= bitmapContainer.getBitmap();
            bitmapContainer.setBitmap(bitmap);
//            puzzle.setBitmapContainer(bitmapContainer);
            if(oldBitmap==null){
                Log.d(TAG,"oldBitmap = null");
                puzzle.update();

            }
        }
        if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                && null != data) {

            Bitmap bitmap =  activityResultChooseImage2(data, puzzle.getWidth(), puzzle.getHeight());

            if (bitmap == null){
                Log.d(TAG,"Bitmap es null");
            }else{
                Log.d(TAG,"Bitmap NO es null");
            }
            bitmapContainer.setBitmap(bitmap);
            puzzle.setBitmapContainer(bitmapContainer);
            puzzle.update();

        }

    }

    private Bitmap activityResultChooseImage2(Intent data, int rewWidth, int reqHeight){
        Log.d(TAG,"activityResultChooseImage2");
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

        return bitmap;

    }

    private Bitmap activityResultChooseImage (Intent data, int rewWidth, int reqHeight){
        final boolean isCamera;
        if(data == null)
        {
            isCamera = true;
        }
        else
        {
            final String action = data.getAction();
            if(action == null)
            {
                isCamera = false;
            }
            else
            {
                isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            }

        }

        Uri selectedImageUri;
        if(isCamera)
        {
            selectedImageUri = outputFileUri;
        }
        else
        {
            selectedImageUri = data == null ? null : data.getData();
        }
        String[] filePathColumn = { MediaStore.Images.Media.DATA };

        // Get the cursor
        Cursor cursor = getContentResolver().query(selectedImageUri,
                filePathColumn, null, null, null);
        // Move to first row
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String imgDecodableString = cursor.getString(columnIndex);
        cursor.close();
        Bitmap bitmap = BitmapCompressor.decodeSampledBitmapFromFile(imgDecodableString,rewWidth,reqHeight);

        return bitmap;

    }

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
        outState.putInt(MOVES_COUNTER_KEY,moveCounter);
        outState.putParcelable(BITMAP_KEY,puzzle.getBitmapContainer().getBitmap());
        outState.putIntArray(POS_KEY,puzzle.getPositions());
        outState.putBoolean(LIVEFEED_KEY,liveFeedState);
//        outState.putParcelable(OUTPUTFILE_KEY,outputFileUri);
        super.onSaveInstanceState(outState);

    }



}
