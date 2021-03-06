package com.pocotopocopo.juego.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pocotopocopo.juego.BackgroundMode;
import com.pocotopocopo.juego.BitmapContainer;
import com.pocotopocopo.juego.ChronometerView;
import com.pocotopocopo.juego.GameConstants;
import com.pocotopocopo.juego.GameInfo;
import com.pocotopocopo.juego.GameMode;
import com.pocotopocopo.juego.GameStatus;
import com.pocotopocopo.juego.Puzzle;
import com.pocotopocopo.juego.R;

import java.io.ByteArrayOutputStream;


public class PuzzleActivity extends BaseActivity{

    private static final String TAG="Juego.MainActivity";
    private static final String MOVES_COUNTER_KEY = "movesCount";
    private static final String LIVEFEED_KEY = "liveFeed";
    private static final String GAME_STATUS_KEY= "gameStatusKey";
    private static final String TIME_ELAPSED_KEY = "timeElapsedKey";

    private static final String SOUND_ENABLED_KEY = "SoundEnabledKey";
    private AudioManager audioManager;
    private SoundPool soundPool;
    private float volume,actVolume,maxVolume;
    private boolean loadedSound=false;
    private TextView moveCounterText;
    private ChronometerView chrono;
    private ImageView soundButton;

    private int moveCounter = 0;
    private Camera camera=null;
    private byte[] cameraData = null;
    private boolean liveFeedEnabled=false;
    private boolean liveFeedState=false;
    private Handler handler;
    private SurfaceTexture dummySurfaceTexture;
    private int clickId;
    private int beepId;
    private int applause;
    private int buuuu;
    private boolean startedGame=false;


    private GameInfo gameInfo;
    private GameStatus gameStatus;

    private Dialog countDownDialog;
    private Dialog pauseDialog;
    private Dialog winDialog;
    private Dialog gameOverDialog;
    private boolean soundEnabled;


    private BitmapContainer bitmapContainer;


    private Puzzle puzzle;
    private boolean isMultiplayer=false;

    @Override
    protected void initViews(){
        super.initViews();

        puzzle = (Puzzle)findViewById(R.id.puzzle);
        moveCounterText = (TextView)findViewById(R.id.moveCounterText);
        moveCounterText.setText(getString(R.string.moves_text,0));
        chrono= (ChronometerView)findViewById(R.id.timerView);


        soundButton = (ImageView)findViewById(R.id.soundButton);
        if (soundEnabled){

            soundButton.setImageResource(R.drawable.sound_off);
        }else{
            soundButton.setImageResource(R.drawable.sound_on);
        }
        soundButton.invalidate();
    }

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
        clickId = soundPool.load(getApplicationContext(),R.raw.click,2);
        loadedSound=false;
        beepId = soundPool.load(getApplicationContext(),R.raw.beep,1);
        loadedSound=false;
        buuuu = soundPool.load(getApplicationContext(),R.raw.buu,1);
        loadedSound=false;
        applause = soundPool.load(getApplicationContext(),R.raw.applause,1);


//        loadedSound=false;
//        musicId = soundPool.load(getApplicationContext(),R.raw.music,2);
    }

    @Override
    public void onBackPressed() {
        if (gameStatus.equals(GameStatus.PLAYING)) {
            pauseGame();
        }
    }

    private void pauseGame(){
        Log.d(TAG,"pauseGame");
        gameStatus=GameStatus.PAUSED;
        chrono.pause();
        pauseDialog = new Dialog(PuzzleActivity.this);

        pauseDialog.setContentView(R.layout.pause_dialog_layout);
        pauseDialog.setTitle(R.string.paused_text);
        pauseDialog.setCancelable(true);

        pauseDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                chrono.start();
                gameStatus=GameStatus.PLAYING;
                Log.d(TAG,"prueba");
            }
        });
        Button resumeButton = (Button)pauseDialog.findViewById(R.id.resumeButton);
        Button exitGameButton = (Button)pauseDialog.findViewById(R.id.exitGameButton);
        Button retryButton = (Button) pauseDialog.findViewById(R.id.retryButton);
        resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseDialog.cancel();
            }
        });
        exitGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isMultiplayer){
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(pauseDialog.getContext());

                    alertDialogBuilder.setMessage("Do you want to leave the multiplayer match");

                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            pauseDialog.dismiss();
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
                    pauseDialog.dismiss();
                    finish();
                }
            }
        });
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseDialog.dismiss();
                restartGame();

            }
        });
        if (isMultiplayer){
            retryButton.setVisibility(View.GONE);
        }
        pauseDialog.show();
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!hasFocus && gameStatus.equals(GameStatus.PLAYING)){
            pauseGame();
        }
    }

    private void setGameInfo(Bundle extras){
        if (extras.containsKey(GameConstants.GAME_INFO)) {
            gameInfo = extras.getParcelable(GameConstants.GAME_INFO);
        } else {
            Log.e(TAG, "Error, no gameInfo in intent");
            finish();
            return;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TODO: is this still valid?: when the image is recreated, the code should check if the game is over, if not, resume the time



        //TODO: improve UI

        super.onCreate(savedInstanceState);
        Log.d(TAG, "Contacts: ********************************************* STARTING **********************************");
        setContentView(R.layout.puzzle_activity_layout);
        Log.d(TAG, "setContentView");

        initViews();
        loadSounds();


//        chrono.setCountUp(false);
//        chrono.setTime(10000);
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        soundEnabled = sharedPreferences.getBoolean(SOUND_ENABLED_KEY,true);


        Log.d(TAG, "capturando intent");
        Intent intent = getIntent();
        Log.d(TAG,"capture intent");
        if (intent!=null && intent.getExtras()!=null) {
            Bundle extras = intent.getExtras();
            if (extras.containsKey(GameConstants.IS_MULTIPLAYER)) {
                isMultiplayer = extras.getBoolean(GameConstants.IS_MULTIPLAYER);
            }
            setGameInfo(extras);

        } else {
            Log.e(TAG, "Error, null intent");
            finish();
            return;
        }

        handler = new Handler(Looper.getMainLooper());

        dummySurfaceTexture=new SurfaceTexture(0);

        bitmapContainer = new BitmapContainer();

        int cols = gameInfo.getCols();
        int rows= gameInfo.getRows();
        boolean numbersVisible = gameInfo.isNumbersVisible();
        BackgroundMode backgroundMode = gameInfo.getBackgroundMode();
        GameMode gameMode = gameInfo.getGameMode();
        if (gameMode.equals(GameMode.SPEED)){
            chrono.setCountUp(false);
            chrono.setTime(gameInfo.getTimeForSpeed()*1000);
        }

        puzzle.setSize(cols, rows);
        puzzle.setNumbersVisible(numbersVisible);


        puzzle.setBitmapContainer(bitmapContainer);
        Log.d(TAG, "bitmapcontainer = null");

        puzzle.setBitmapContainer(bitmapContainer);
        bitmapContainer.setBitmap(gameInfo.getBitmap());


        initListeners();



        if (savedInstanceState!=null){



            isMultiplayer=savedInstanceState.getBoolean(GameConstants.IS_MULTIPLAYER);
            gameInfo = savedInstanceState.getParcelable(GameConstants.GAME_INFO);


            int[] posArray= gameInfo.getPieceOrder();


            bitmapContainer.setBitmap(gameInfo.getBitmap());
            puzzle.setPositions(posArray);
            puzzle.update();
            moveCounter=savedInstanceState.getInt(MOVES_COUNTER_KEY);
            moveCounterText.setText(getString(R.string.moves_text,moveCounter));
            liveFeedState=savedInstanceState.getBoolean(LIVEFEED_KEY);
            gameStatus=(GameStatus)savedInstanceState.getSerializable(GAME_STATUS_KEY);
            long time = savedInstanceState.getLong(TIME_ELAPSED_KEY);
            chrono.pause(time);
            switch (gameStatus){
                case PAUSED:
                    pauseGame();
                    break;
                case STARTING:
                    startCountdown();
                    break;
                case FINISHED:
                    showWinDialog();
                    break;
                case PLAYING:
                    chrono.start();
                    break;
                default:

            }


            Log.d(TAG,"gameStatus = " + gameStatus.toString());

        } else {

            if (backgroundMode.equals(BackgroundMode.VIDEO)){
                liveFeedState=true;
            }
            puzzle.update();
            gameStatus=GameStatus.STARTING;
            startCountdown();
        }


    }

    private void playSound(int sound, float rate){
        if (soundEnabled && loadedSound){
            soundPool.play(sound,volume,volume,1,1,rate);
        }
    }

    private void initListeners() {

        puzzle.setOnMovePieceListener(new Puzzle.OnMovePieceListener() {
            @Override
            public void onPieceMoved() {
                moveCounterText.setText(getString(R.string.moves_text, ++moveCounter));
                playSound(clickId,1f);
            }

            @Override
            public void onPuzzleSolved() {
                chrono.pause();
                gameStatus=GameStatus.FINISHED;
                if (camera!=null){
                    stopLiveFeed();
                }
                showWinDialog();
            }
        });

        soundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (soundEnabled){
                    soundButton.setImageResource(R.drawable.sound_off);
                    soundEnabled=false;
                }else{
                    soundButton.setImageResource(R.drawable.sound_on);
                    soundEnabled=true;
                }

                soundButton.invalidate();
            }
        });

        chrono.setOnFinishListener(new ChronometerView.OnFinishListener() {
            @Override
            public void onFinish() {
                gameStatus = GameStatus.FINISHED;
                gameOver();
                //Toast.makeText(getApplicationContext(),"se acabo el tiempo",Toast.LENGTH_LONG).show();

            }
        });



    }


    private void gameOver(){
        playSound(buuuu,1f);
        gameOverDialog = new Dialog(PuzzleActivity.this);
        gameOverDialog.setCancelable(false);
        gameOverDialog.setContentView(R.layout.game_over_dialog_layout);
        gameOverDialog.setTitle(R.string.game_over_title);
        Button exitButton = (Button) gameOverDialog.findViewById(R.id.exitGameOverButton);
        Button retryButton = (Button) gameOverDialog.findViewById(R.id.retryGameOverButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameOverDialog.cancel();
                finish();
            }
        });
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameOverDialog.cancel();
                restartGame();
            }
        });
        gameOverDialog.show();


    }
    private void showWinDialog(){
        playSound(applause,1f);
        winDialog = new Dialog(PuzzleActivity.this);
        winDialog.setCancelable(false);
        winDialog.setContentView(R.layout.win_dialog_layout);
        winDialog.setTitle(R.string.congratulation_text);
        TextView movesWinText = (TextView) winDialog.findViewById(R.id.movesWinText);
        TextView timeWinText = (TextView) winDialog.findViewById(R.id.timeWinText);
        ImageView winImage = (ImageView) winDialog.findViewById(R.id.winImage);
        Button exitButton = (Button)winDialog.findViewById(R.id.exitWinScreenButton);
        Button retryButton = (Button) winDialog.findViewById(R.id.retryButton);
        Bitmap bitmap = puzzle.getBitmapContainer().getBitmap();
        if (bitmap==null){
            Bitmap bitmapTrophy = BitmapFactory.decodeResource(getResources(),R.drawable.trophy);
            winImage.setImageBitmap(bitmapTrophy);
        }else{

            winImage.setImageBitmap(puzzle.getBitmapContainer().getBitmap());
        }
        movesWinText.setText(getString(R.string.moves_text,moveCounter));
        timeWinText.setText(getString(R.string.time_text_win,chrono.getTimeText()));
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMultiplayer){
                    Intent intentData = new Intent();
                    intentData.putExtra(GameConstants.WIN_MOVEMENTS,moveCounter);
                    intentData.putExtra(GameConstants.WIN_TIME,chrono.getTime());

                    setResult(RESULT_OK,intentData);
                    finish();
                } else {
                    winDialog.cancel();
                    finish();
                }
            }
        });
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                winDialog.cancel();
                restartGame();
            }
        });
        if (isMultiplayer){
            retryButton.setVisibility(View.GONE);
        }

        winDialog.show();
    }

    public void restartGame(){
        moveCounter=0;
        moveCounterText.setText(getString(R.string.moves_text,moveCounter));
        if (gameInfo.getGameMode()==GameMode.SPEED){
            chrono.setCountUp(false);
            chrono.setTime(gameInfo.getTimeForSpeed()*1000);
            Log.d(TAG,"gamemode = speed " + gameInfo.getGameMode().toString());
            Log.d(TAG,"time = " + chrono.getTime());
        }
//        chrono.reset();
        gameStatus = GameStatus.STARTING;
        puzzle.randomizeBoard();
        puzzle.update();
        startCountdown();
    }

    private void startCountdown(){
        countDownDialog = new Dialog(PuzzleActivity.this);

        countDownDialog.setContentView(R.layout.count_down_widget_layout);
        countDownDialog.setCancelable(false);
        countDownDialog.setTitle(R.string.game_start_in);
        final TextView countDownText = (TextView) countDownDialog.findViewById(R.id.countDownText);
        CountDownTimer countDownTimer;
        countDownTimer = new CountDownTimer(3600,200) {
            @Override
            public void onTick(long millisUntilFinished) {
                try{
                    int time = (int)(millisUntilFinished/1000);
                    countDownText.setText(Integer.toString(time));
                    if (millisUntilFinished%1000>800) {
                        playSound(beepId,1f);
                    }
                }catch(Exception e){
                    Log.d(TAG,e.getMessage());
                }


            }

            @Override
            public void onFinish() {
                countDownDialog.dismiss();
                playSound(beepId,0.25f);
                chrono.start();
                gameStatus=GameStatus.PLAYING;
            }
        };

        Log.d(TAG,"cree el countdowntimer");
        countDownDialog.show();
        Log.d(TAG,"mostre el dialog");

        countDownTimer.start();
        Log.d(TAG,"arranque el countdown timer");
    }

    @Override
    protected void onDestroy() {
        soundPool.unload(clickId);
        if (countDownDialog!=null && countDownDialog.isShowing()){
            countDownDialog.dismiss();
        }
        if (pauseDialog!=null && pauseDialog.isShowing()){
            pauseDialog.dismiss();
        }
        if (winDialog!=null && winDialog.isShowing()){
            winDialog.dismiss();
        }
        super.onDestroy();
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

        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SOUND_ENABLED_KEY,soundEnabled);
        editor.commit();

        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        gameInfo.setPieceOrder(puzzle.getPositions());
        outState.putParcelable(GameConstants.GAME_INFO, gameInfo);
        outState.putBoolean(GameConstants.IS_MULTIPLAYER,isMultiplayer);
        outState.putInt(MOVES_COUNTER_KEY,moveCounter);
        outState.putBoolean(LIVEFEED_KEY,liveFeedState);
        outState.putSerializable(GAME_STATUS_KEY,gameStatus);
        outState.putLong(TIME_ELAPSED_KEY, chrono.getTime());
        super.onSaveInstanceState(outState);

    }
}
