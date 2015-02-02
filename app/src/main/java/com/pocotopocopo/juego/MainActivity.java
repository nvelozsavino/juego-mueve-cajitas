package com.pocotopocopo.juego;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameUtils;

import java.io.ByteArrayOutputStream;





public class MainActivity extends BaseActivity{

    private static final String TAG="Juego.MainActivity";
    private static final String MOVES_COUNTER_KEY = "movesCount";
    private static final String BITMAP_KEY = "bitmapContainer";
    private static final String POS_KEY = "posNumbers";
    private static final String LIVEFEED_KEY = "liveFeed";



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

    private BoxPuzzle puzzle;
    //private LinearLayout frame;


    private void initViews(){
        selectImageButton = (Button) findViewById(R.id.selectImage);
        puzzle = (BoxPuzzle)findViewById(R.id.puzzle);
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
                    liveFeedEnabled=startLiveFeed();
                } else {
                    stopLiveFeed();

                }
            }
        });
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });



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


        Log.d(TAG, "capturando intent");
        Intent intent = getIntent();
        Log.d(TAG,"capture intent");
        if (intent!=null) {
            Log.d(TAG,"intent no es null");
            try {
                int cols = intent.getExtras().getInt(StartScreen.COLS_KEY);
                int rows = intent.getExtras().getInt(StartScreen.ROWS_KEY);
                Log.d(TAG, "cols = " + cols + " - rows = " + rows);
                puzzle.setSize(cols, rows);
            }catch(Exception e){
                Log.d(TAG,"el intent no es "+ e.getMessage());
            }
        }
        puzzle.setBitmapContainer(bitmapContainer);
        Log.d(TAG,"bitmapcontainer = null");

        puzzle.setBitmapContainer(bitmapContainer);
        bitmapContainer.setBitmap(null);



        puzzle.setOnMovePieceListener(new BoxPuzzle.OnMovePieceListener() {
            @Override
            public void onPieceMoved() {

                moveCounterText.setText("Movimientos: " + (++moveCounter));
//                resolvableText.setText("ResolvableCode = " + puzzle.getResolvableNumber());
                if (puzzle.isWin()){
                    Toast toast = Toast.makeText(getApplicationContext(),"Congratulations you Win!!!",Toast.LENGTH_LONG);
                    toast.show();
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
            // Get the Image from data

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
            Bitmap bitmap = BitmapFactory.decodeFile(imgDecodableString);
            if (bitmap == null){
                Log.d(TAG,"Bitmap es null");
            }else{
                Log.d(TAG,"Bitmap NO es null");
            }
            bitmapContainer.setBitmap(bitmap);
//            puzzle.setBitmapContainer(bitmapContainer);
            puzzle.update();

        }

    }

    @Override
    public void displaySignIn() {

    }

    @Override
    public void hideSignIn() {

    }

    private boolean startLiveFeed(){
        if (camera!=null){
            stopLiveFeed();
        }
        camera=Camera.open();
        try {
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

                Bitmap bitmap = BitmapFactory.decodeByteArray(jdata, 0, jdata.length);
                Bitmap oldBitmap= bitmapContainer.getBitmap();
                bitmapContainer.setBitmap(bitmap);
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
        super.onSaveInstanceState(outState);

    }



}
