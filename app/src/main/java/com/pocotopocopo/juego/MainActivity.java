package com.pocotopocopo.juego;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameUtils;

import java.io.ByteArrayOutputStream;





public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG="Juego";
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


    private GoogleApiClient googleApiClient;


    private BitmapContainer bitmapContainer;
    private Button selectImageButton;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private BoxPuzzle puzzle;
    private SignInButton signInButton;
    private Button signOutButton;


    private void initViews(){
        selectImageButton = (Button) findViewById(R.id.selectImage);
        puzzle = (BoxPuzzle)findViewById(R.id.puzzle);
        moveCounterText = (TextView)findViewById(R.id.moveCounterText);
        resolvableText = (TextView)findViewById(R.id.resolvableText);
        liveFeedButton = (Button)findViewById(R.id.liveFeedButton);
        signInButton = (SignInButton)findViewById(R.id.signInButton);
        signOutButton = (Button)findViewById(R.id.signOutButton);
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

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        signInButton.setOnClickListener(singInOutClickListener);

        signOutButton.setOnClickListener(singInOutClickListener);


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Contacts: ********************************************* STARTING **********************************");
        setContentView(R.layout.activity_main);

        initViews();
        handler = new Handler(Looper.getMainLooper());
        setClickListeners();

        dummySurfaceTexture=new SurfaceTexture(0);

        bitmapContainer = new BitmapContainer();

        puzzle.setBitmapContainer(bitmapContainer);
        Intent intent = getIntent();
        int cols = intent.getExtras().getInt(StartScreen.COLS_KEY);
        int rows = intent.getExtras().getInt(StartScreen.ROWS_KEY);
        Log.d(TAG,"cols = " + cols + " - rows = " + rows);
        puzzle.setSize(cols,rows);

        bitmapContainer.setBitmap(null);



        puzzle.setOnMovePieceListener(new BoxPuzzle.OnMovePieceListener() {
            @Override
            public void onPieceMoved() {

                moveCounterText.setText("Movimientos: " + (++moveCounter));
                resolvableText.setText("ResolvableCode = " + puzzle.getResolvableNumber());
            }
        });



        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                        // add other APIs and scopes here as needed
                .build();


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
        Log.d(TAG,"onActivityResult");
        if (requestCode==REQUEST_IMAGE_CAPTURE && resultCode==RESULT_OK){

            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");
            Log.d(TAG,"bitmapContainer loaded? " + (bitmap!=null));
            Bitmap oldBitmap= bitmapContainer.getBitmap();
            bitmapContainer.setBitmap(bitmap);
            if(oldBitmap==null){
                puzzle.update();

            }
        }
        if (requestCode == RC_SIGN_IN) {
            mSignInClicked = false;
            mResolvingConnectionFailure = false;
            if (resultCode == RESULT_OK) {
                googleApiClient.connect();
            } else {
                // Bring up an error dialog to alert the user that sign-in
                // failed. The R.string.signin_failure should reference an error
                // string in your strings.xml file that tells the user they
                // could not be signed in, such as "Unable to sign in."
                BaseGameUtils.showActivityResultError(this,
                        requestCode, resultCode, R.string.signin_failure);
            }
        }
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


    @Override
    protected void onStart() {
        super.onStart();
        if (!mInSignInFlow && !mExplicitSignOut) {
            // auto sign in
            googleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG,"onConnected");
        signInButton.setVisibility(View.INVISIBLE);
        signOutButton.setVisibility(View.VISIBLE);

    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    private static int RC_SIGN_IN = 9001;

    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInFlow = true;
    private boolean mSignInClicked = false;

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        if (ConnectionResult.SIGN_IN_REQUIRED== connectionResult.getErrorCode()){

        }

        if (mResolvingConnectionFailure) {

            // already resolving
            return;
        }

        // if the sign-in button was clicked or if auto sign-in is enabled,
        // launch the sign-in flow

        if (mSignInClicked || mAutoStartSignInFlow) {

            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = true;

            // Attempt to resolve the connection failure using BaseGameUtils.
            // The R.string.signin_other_error value should reference a generic
            // error string in your strings.xml file, such as "There was
            // an issue with sign-in, please try again later."
            if (!BaseGameUtils.resolveConnectionFailure(this,googleApiClient, connectionResult,RC_SIGN_IN, "There was an issue with sign-in, please try again later.")) {
                //Log.d(TAG,"ErrorCossssde="+connectionResult.getErrorCode());
                mResolvingConnectionFailure = false;
            }

        }

        signInButton.setVisibility(View.VISIBLE);// Put code here to display the sign-in button
        signOutButton.setVisibility(View.INVISIBLE);
    }

    // Call when the sign-in button is clicked
    private void signInClicked() {
        mSignInClicked = true;
        googleApiClient.connect();
        Log.d(TAG,"sign in clicked");
    }

    // Call when the sign-out button is clicked
    private void signOutClicked() {
        mSignInClicked = false;
        signInButton.setVisibility(View.INVISIBLE);// Put code here to display the sign-in button
        signOutButton.setVisibility(View.INVISIBLE);
        PendingResult<Status> statusPendingResult =Games.signOut(googleApiClient);
        statusPendingResult.setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                if (status.isSuccess()){
                    signInButton.setVisibility(View.VISIBLE);// Put code here to display the sign-in button
                    signOutButton.setVisibility(View.INVISIBLE);
                }
            }
        });


    }


    boolean mExplicitSignOut = false;
    boolean mInSignInFlow = false; // set to true when you're in the middle of the
    // sign in flow, to know you should not attempt
    // to connect in onStart()


    private View.OnClickListener singInOutClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.signInButton) {
                // start the asynchronous sign in flow
                mSignInClicked = true;
                googleApiClient.connect();
            }
            else if (view.getId() == R.id.signOutButton) {
                // sign out.
                mExplicitSignOut = true;
                if (googleApiClient != null && googleApiClient.isConnected()) {
                    Games.signOut(googleApiClient);
                    googleApiClient.disconnect();
                    signInButton.setVisibility(View.VISIBLE);// Put code here to display the sign-in button
                    signOutButton.setVisibility(View.GONE);
                }
            }
        }
    };
}
