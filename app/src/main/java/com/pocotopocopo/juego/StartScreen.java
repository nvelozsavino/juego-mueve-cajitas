package com.pocotopocopo.juego;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class StartScreen extends BaseActivity implements CountDownPickerDialog.CountDownPickerListener{

    private static final String TAG = "Juego.StartScreen";




    private RadioGroup gameModeRadioGroup;
//    private RadioButton gameModeTraditionalRadioButton;
    private RadioButton gameModeSpeedRadioButton;
//    private RadioButton gameModeMultiplayerRadioButton;

    private RadioGroup gameSizeGroup;
//    private RadioButton gameSize3x3Button;
//    private RadioButton gameSize4x4Button;
//    private RadioButton gameSize5x5Button;
//    private RadioButton gameSize6x6Button;
    private RadioButton gameSizeCustomButton;

    private Button startGameButton;
    private ImageView logo;

    private long timeForSpeed=-1;
    private BoardSize boardSize;




    private RadioGroup backgroundRadioGroup;
//    private RadioButton backgroundPlainRadioButton;
//    private RadioButton backgroundImageRadioButton;
    private RadioButton backgroundVideoRadioButton;



    private CheckBox showNumbersCheckBox;
    private GameInfo gameInfo;



    @Override
    protected void initViews(){
        super.initViews();

        startGameButton = (Button) findViewById(R.id.startGameButton);
        gameSizeGroup = (RadioGroup) findViewById(R.id.gameSizeGroup);
//        gameSize3x3Button= (RadioButton)findViewById(R.id.gameSize3x3Button);
//        gameSize4x4Button= (RadioButton)findViewById(R.id.gameSize4x4Button);
//        gameSize5x5Button= (RadioButton)findViewById(R.id.gameSize5x5Button);
//        gameSize6x6Button= (RadioButton)findViewById(R.id.gameSize6x6Button);
        gameSizeCustomButton= (RadioButton)findViewById(R.id.gameSizeCustomButton);

        gameModeRadioGroup=(RadioGroup)findViewById(R.id.gameModeRadioGroup);

//        gameModeTraditionalRadioButton=(RadioButton)findViewById(R.id.gameModeTraditionalRadioButton);
        gameModeSpeedRadioButton=(RadioButton)findViewById(R.id.gameModeSpeedRadioButton);
//        gameModeMultiplayerRadioButton=(RadioButton)findViewById(R.id.gameModeMultiplayerRadioButton);


        logo = (ImageView)findViewById(R.id.logoImage);

        showNumbersCheckBox = (CheckBox)findViewById(R.id.showNumbersCheckBox);

        backgroundRadioGroup=(RadioGroup)findViewById(R.id.backgroundRadioGroup);
//        backgroundPlainRadioButton=(RadioButton)findViewById(R.id.backgroundPlainRadioButton);
//        backgroundImageRadioButton=(RadioButton)findViewById(R.id.backgroundImageRadioButton);
        backgroundVideoRadioButton=(RadioButton)findViewById(R.id.backgroundVideoRadioButton);




    }


    private GameActivity getActivityClass(GameMode gameMode){
        GameActivity activityClass;
        if (gameMode.equals(GameMode.MULTIPLAYER)) {
            activityClass = GameActivity.MULTIPLAYER;
        } else {

            activityClass = GameActivity.PUZZLE;
        }
        return activityClass;
    }
    private Class<? extends Activity> getActualIntentClass(BackgroundMode backgroundMode, GameActivity activityClass) {
        Class<? extends Activity> actualIntentClass;
        if (backgroundMode.equals(BackgroundMode.IMAGE)) {
            actualIntentClass = GameActivity.CREATE_GAME.getActivityClass();
        } else {
            actualIntentClass = activityClass.getActivityClass();
        }
        return actualIntentClass;
    }
//    private BackgroundMode getBackgroundMode(){
//        BackgroundMode backgroundMode;
//        int backgroundModeId=backgroundRadioGroup.getCheckedRadioButtonId();
//        switch (backgroundModeId){
//            default:
//            case R.id.backgroundPlainRadioButton:
//                backgroundMode=BackgroundMode.PLAIN;
//                break;
//            case R.id.backgroundImageRadioButton:
//                backgroundMode=BackgroundMode.IMAGE;
//                break;
//            case R.id.backgroundVideoRadioButton:
//                backgroundMode=BackgroundMode.VIDEO;
//                break;
//
//        }
//        return backgroundMode;
//    }
//    private GameMode getGameMode(){
//      int gameModeId=gameModeRadioGroup.getCheckedRadioButtonId();
//      GameMode gameMode;
//
//      switch (gameModeId){
//          default:
//          case R.id.gameModeTraditionalRadioButton:
//              gameMode=GameMode.TRADITIONAL;
//              break;
//          case R.id.gameModeSpeedRadioButton:
//              gameMode=GameMode.SPEED;
//              showTimeDialog();
//              break;
//          case R.id.gameModeMultiplayerRadioButton:
//              gameMode=GameMode.MULTIPLAYER;
//              break;
//      }
//      return gameMode;
//
//    }
//    private void startGame2(GameInfo gameInfo, GameActivity activityClass, BackgroundMode backgroundMode, Class<? extends Activity> actualIntentClass, long time){
//
//    }
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

//        final Dialog dialog = new Dialog(StartScreen.this);
//        dialog.setContentView(R.layout.select_time_dialog_widget);
//        dialog.setCancelable(true);
//        final Button okButton  = (Button) dialog.findViewById(R.id.okTimeDialogButton);
//        final NumberPicker minutesPicker = (NumberPicker) dialog.findViewById(R.id.minutesPicker);
//        final NumberPicker secondsPicker = (NumberPicker) dialog.findViewById(R.id.secondsPicker);
//        okButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                timeForSpeed = (minutesPicker.getValue()*60+secondsPicker.getValue())*1000;
//                dialog.dismiss();
//            }
//        });
//        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialog) {
//                timeForSpeed = -1;
//                dialog.dismiss();
//            }
//        });
//        dialog.setOnDismissListener(this);
//        dialog.show();

    }

//    @Override
//    public void onDismiss(DialogInterface dialog) {
//       if (timeForSpeed!=-1){
//           GameMode gameMode = getGameMode();
//           BackgroundMode backgroundMode = getBackgroundMode();
//           GameActivity activityClass = getActivityClass(gameMode);
//           Class<? extends Activity> actualIntentClass = getActualIntentClass(backgroundMode,activityClass);
//           boolean showNumbers=showNumbersCheckBox.isChecked();
//           GameInfo gameInfo = new GameInfo(boardSize.rows,boardSize.cols,backgroundMode,gameMode,showNumbers);
//           startGame2(gameInfo, activityClass, backgroundMode, actualIntentClass, timeForSpeed);
//       }
//    }

    @Override
    public void onTimeSelected(int minutes, int seconds) {
        timeForSpeed = (minutes*60+seconds)*1000;
        gameInfo.setTimeForSpeed(timeForSpeed);
        gameModeRadioGroup.check(R.id.gameModeSpeedRadioButton);
        gameInfo.setGameMode(GameMode.SPEED);
    }

    public static class BoardSize{
        public int rows;
        public int cols;

        public BoardSize(int rows, int cols) {
            this.rows = rows;
            this.cols = cols;
        }
    }

    private void startGame(){

        //boardSize = new BoardSize(rows,cols);
//        GameMode gameMode = getGameMode();
//        BackgroundMode backgroundMode = getBackgroundMode();
        GameActivity activityClass = getActivityClass(gameInfo.getGameMode());
        Class<? extends Activity> actualIntentClass = getActualIntentClass(gameInfo.getBackgroundMode(),activityClass);
//
//        boolean showNumbers=showNumbersCheckBox.isChecked();
//
//        GameInfo gameInfo = new GameInfo(rows,cols,backgroundMode,gameMode,showNumbers);
//        gameInfo.setTimeForSpeed(timeForSpeed);
        Intent startGame = new Intent(getApplicationContext(), actualIntentClass);
        if (gameInfo.getBackgroundMode().equals(BackgroundMode.IMAGE)){
            //startGame.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        }
        startGame.putExtra(GameConstants.GAME_INFO,gameInfo);
        startGame.putExtra(GameConstants.NEXT_ACTIVITY,activityClass);
        Log.d(TAG, "Signed In: " + googleApiClient.isConnected());

        startActivity(startGame);

    }

    private void initListeners(){

        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame();
            }
        });

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame();
            }
        });



        backgroundRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d(TAG, "BackgroundMode Changed");
                switch (checkedId){
                    case R.id.backgroundPlainRadioButton:
                        Log.d(TAG, "Plain");
                        Log.d(TAG, "Set number visible true and disabled");

                        showNumbersCheckBox.setEnabled(false);
                        gameInfo.setNumbersVisible(true);
                        gameInfo.setBackgroundMode(BackgroundMode.PLAIN);
                        break;

                    case R.id.backgroundVideoRadioButton:
                        Log.d(TAG, "Video");
                        gameInfo.setBackgroundMode(BackgroundMode.VIDEO);
                        gameInfo.setNumbersVisible(false);
                        showNumbersCheckBox.setEnabled(true);
                        break;
                    default:
                        Log.d(TAG, "default");
                    case R.id.backgroundImageRadioButton:
                        Log.d(TAG, "Image");
                        gameInfo.setNumbersVisible(false);
                        showNumbersCheckBox.setEnabled(true);
                        gameInfo.setBackgroundMode(BackgroundMode.IMAGE);
                        break;
                }
                showNumbersCheckBox.setChecked(gameInfo.isNumbersVisible());
            }
        });
        gameModeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d(TAG, "GameMode Changed");
                switch (checkedId){
                    case R.id.gameModeMultiplayerRadioButton:
                        Log.d(TAG, "Multiplayer");
                        if (backgroundRadioGroup.getCheckedRadioButtonId()==R.id.backgroundVideoRadioButton){
                            backgroundRadioGroup.check(R.id.backgroundImageRadioButton);
                            gameInfo.setBackgroundMode(BackgroundMode.IMAGE);
                            backgroundVideoRadioButton.setChecked(false);
                        }
                        backgroundVideoRadioButton.setEnabled(false);
                        gameInfo.setGameMode(GameMode.MULTIPLAYER);
                        break;
                    default:
                        Log.d(TAG, "default");
                    case R.id.gameModeTraditionalRadioButton:
                        Log.d(TAG, "Traditional");
                        gameInfo.setGameMode(GameMode.TRADITIONAL);
                        backgroundVideoRadioButton.setEnabled(true);
                        break;
                    case R.id.gameModeSpeedRadioButton:
                        Log.d(TAG, "Speed");
                        //gameInfo.setGameMode(GameMode.SPEED);
                        //backgroundVideoRadioButton.setEnabled(true);
                        break;
                }
            }
        });
        showNumbersCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "Number Visible Changed " + isChecked);
                gameInfo.setNumbersVisible(isChecked);
            }
        });
        gameModeSpeedRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Speed Click");
                showTimeDialog();
            }
        });

        gameSizeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d(TAG, "onSize Changed: ");
                switch (checkedId){
                    case R.id.gameSize3x3Button:
                        Log.d(TAG, "3x3 ");
                        gameInfo.setRows(3);
                        gameInfo.setCols(3);
                        break;
                    default:
                        Log.d(TAG, "default");
                    case R.id.gameSize4x4Button:
                        Log.d(TAG, "4x4");
                        gameInfo.setRows(4);
                        gameInfo.setCols(4);
                        break;
                    case R.id.gameSize5x5Button:
                        Log.d(TAG, "5x5");
                        gameInfo.setRows(5);
                        gameInfo.setCols(5);
                        break;
                    case R.id.gameSize6x6Button:
                        Log.d(TAG, "6x6");
                        gameInfo.setRows(6);
                        gameInfo.setCols(6);
                        break;
                    case R.id.gameSizeCustomButton:
                        Log.d(TAG, "?x?");
                        gameInfo.setRows(4); //TODO Create Dialog
                        gameInfo.setCols(4);
                        break;
                }
            }
        });
        gameSizeCustomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Create Dialog
            }
        });

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        initViews();


        if (savedInstanceState==null){
            gameInfo=new GameInfo(4,4,BackgroundMode.IMAGE,GameMode.TRADITIONAL,false);
        } else {
            gameInfo=savedInstanceState.getParcelable(GameConstants.GAME_INFO);
        }
        setAccordingToGameInfo();
        initListeners();
    }

    private void setAccordingToGameInfo() {
        int cols, rows;
        cols=gameInfo.getCols();
        rows=gameInfo.getRows();

        Log.d(TAG, "Setting Size: " + gameInfo.getCols() +"x" + gameInfo.getRows());
        if (cols==rows){
            switch (cols){
                case 3:
                    gameSizeGroup.check(R.id.gameSize3x3Button);
                    break;
                case 4:
                    gameSizeGroup.check(R.id.gameSize4x4Button);
                    break;
                case 5:
                    gameSizeGroup.check(R.id.gameSize5x5Button);
                    break;
                case 6:
                    gameSizeGroup.check(R.id.gameSize6x6Button);
                    break;
                default:
                    gameSizeGroup.check(R.id.gameSizeCustomButton);
            }

        } else {
            gameSizeGroup.check(R.id.gameSizeCustomButton);
        }

        Log.d(TAG, "Setting GameMode: " + gameInfo.getGameMode());
        switch (gameInfo.getGameMode()){
            default:
            case TRADITIONAL:
                gameModeRadioGroup.check(R.id.gameModeTraditionalRadioButton);
                break;
            case SPEED:
                gameModeRadioGroup.check(R.id.gameModeSpeedRadioButton);
                break;
            case MULTIPLAYER:
                gameModeRadioGroup.check(R.id.gameModeMultiplayerRadioButton);
                break;
        }
        showNumbersCheckBox.setEnabled(true);
        Log.d(TAG, "Setting BackgroundMode: " + gameInfo.getBackgroundMode());
        switch (gameInfo.getBackgroundMode()){

            default:
            case IMAGE:
                backgroundRadioGroup.check(R.id.backgroundImageRadioButton);
                break;
            case PLAIN:
                backgroundRadioGroup.check(R.id.backgroundPlainRadioButton);
                gameInfo.setNumbersVisible(true);
                showNumbersCheckBox.setEnabled(false);
                break;
            case VIDEO:
                backgroundRadioGroup.check(R.id.backgroundVideoRadioButton);
                break;
        }
        Log.d(TAG, "Setting NumberVisible " + gameInfo.isNumbersVisible());
        showNumbersCheckBox.setChecked(gameInfo.isNumbersVisible());

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(GameConstants.GAME_INFO,gameInfo);
        super.onSaveInstanceState(outState);

    }
}
