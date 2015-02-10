package com.pocotopocopo.juego;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class StartScreen extends BaseActivity {

    private static final String TAG = "Juego.StartScreen";


    //    private Button gameSize3x3Button;
//    private Button gameSize4x4Button;
//    private Button gameSize5x5Button;
//    private Button gameSize6x6Button;
//    private Button gameSizeCustomButton;

    private RadioGroup gameModeRadioGroup;
    private RadioGroup gameSizeGroup;
    private Button startGameButton;
    private ImageView logo;




    private RadioGroup backgroundRadioGroup;
//    private RadioButton backgroundPlainRadioButton;
//    private RadioButton backgroundImageRadioButton;
    private RadioButton backgroundVideoRadioButton;



    private CheckBox showNumbersCheckBox;



    @Override
    protected void initViews(){
        super.initViews();
//        gameSize3x3Button= (RadioButton)findViewById(R.id.gameSize3x3Button);
//        gameSize4x4Button= (Button)findViewById(R.id.gameSize4x4Button);
//        gameSize5x5Button= (Button)findViewById(R.id.gameSize5x5Button);
//        gameSize6x6Button= (Button)findViewById(R.id.gameSize6x6Button);
//        gameSizeCustomButton= (Button)findViewById(R.id.gameSizeCustomButton);
        startGameButton = (Button) findViewById(R.id.startGameButton);
        gameSizeGroup = (RadioGroup) findViewById(R.id.gameSizeGroup);
        gameModeRadioGroup=(RadioGroup)findViewById(R.id.gameModeRadioGroup);
        logo = (ImageView)findViewById(R.id.logoImage);

        showNumbersCheckBox = (CheckBox)findViewById(R.id.showNumbersCheckBox);

        backgroundRadioGroup=(RadioGroup)findViewById(R.id.backgroundRadioGroup);
//        backgroundPlainRadioButton=(RadioButton)findViewById(R.id.backgroundPlainRadioButton);
//        backgroundImageRadioButton=(RadioButton)findViewById(R.id.backgroundImageRadioButton);
        backgroundVideoRadioButton=(RadioButton)findViewById(R.id.backgroundVideoRadioButton);




    }




    private void startGame(int rows, int cols){
        int gameModeId=gameModeRadioGroup.getCheckedRadioButtonId();
        GameMode gameMode;
        BackgroundMode backgroundMode;
        switch (gameModeId){
            default:
            case R.id.gameModeTraditionalRadioButton:
                gameMode=GameMode.TRADITIONAL;
                break;
            case R.id.gameModeSpeedRadioButton:
                gameMode=GameMode.SPEED;
                break;
            case R.id.gameModeMultiplayerRadioButton:
                gameMode=GameMode.MULTIPLAYER;
                break;
        }

        int backgroundModeId=backgroundRadioGroup.getCheckedRadioButtonId();
        switch (backgroundModeId){
            default:
            case R.id.backgroundPlainRadioButton:
                backgroundMode=BackgroundMode.PLAIN;
                break;
            case R.id.backgroundImageRadioButton:
                backgroundMode=BackgroundMode.IMAGE;
                break;
            case R.id.backgroundVideoRadioButton:
                backgroundMode=BackgroundMode.VIDEO;
                break;
        }
        boolean showNumbers=showNumbersCheckBox.isChecked();
        GameActivity activityClass;
        Class<? extends Activity> actualIntentClass;
        if (gameMode.equals(GameMode.MULTIPLAYER)) {
            activityClass = GameActivity.MULTIPLAYER;
        } else if (gameMode.equals(GameMode.TRADITIONAL)){
            activityClass = GameActivity.PUZZLE;
        } else {
//            showTimeDialog();
            activityClass = GameActivity.PUZZLE;
        }
        if (backgroundMode.equals(BackgroundMode.IMAGE)){
            actualIntentClass=GameActivity.BITMAP_CHOOSER.getActivityClass();
        } else {
            actualIntentClass=activityClass.getActivityClass();
        }
        GameInfo gameInfo = new GameInfo(rows,cols,backgroundMode,gameMode,showNumbers);
        Intent startGame = new Intent(getApplicationContext(), actualIntentClass);
        if (backgroundMode.equals(BackgroundMode.IMAGE)){
            startGame.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        }
        startGame.putExtra(GameConstants.GAME_INFO,gameInfo);
        startGame.putExtra(GameConstants.NEXT_ACTIVITY,activityClass);
        Log.d(TAG, "Signed In: " + googleApiClient.isConnected());

        startActivity(startGame);
    }

//    private long showTimeDialog(){
//        long time = 0;
//        boolean clicked = false;
//        Dialog timeDialog = new Dialog(StartScreen.this);
//        timeDialog.setContentView(R.layout.select_time_dialog_widget);
//
//        final NumberPicker minutes = (NumberPicker) timeDialog.findViewById(R.id.minutesPicker);
//        final NumberPicker seconds = (NumberPicker) timeDialog.findViewById(R.id.secondsPicker);
//        Button okButton = (Button) timeDialog.findViewById(R.id.okTimeDialogButton);
//        okButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                long time = (minutes.getValue()*60+seconds.getValue())*1000;
//                ((StartScreen) Dialog.this.getOwnerActivity()).onTimeDialogOkListener(time);
//            }
//        });
//
//    }
//    public long onTimeDialogOkListener(long time){
//        return time;
//    }

    private void startGame(){
        int gameSizeId = gameSizeGroup.getCheckedRadioButtonId();
        switch (gameSizeId){
            case R.id.gameSize3x3Button:
                startGame(3,3);
                break;
            case R.id.gameSize4x4Button:
                startGame(4,4);
                break;
            case R.id.gameSize5x5Button:
                startGame(5,5);
                break;
            case R.id.gameSize6x6Button:
                startGame(6,6);
                break;
            case R.id.gameSizeCustomButton:
                startGame(4,4);
                //TODO: create a dialog asking for the size
                break;


        }
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
                switch (checkedId){
                    case R.id.backgroundPlainRadioButton:
                        showNumbersCheckBox.setChecked(true);
                        showNumbersCheckBox.setEnabled(false);
                        break;
                    default:
                    case R.id.backgroundVideoRadioButton:
                    case R.id.backgroundImageRadioButton:
                        showNumbersCheckBox.setEnabled(true);
                        break;
                }
            }
        });
        gameModeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.gameModeMultiplayerRadioButton:
                        if (backgroundRadioGroup.getCheckedRadioButtonId()==R.id.backgroundVideoRadioButton){
                            backgroundRadioGroup.check(R.id.backgroundImageRadioButton);
                            backgroundVideoRadioButton.setChecked(false);
                        }
                        backgroundVideoRadioButton.setEnabled(false);
                        break;
                    default:
                        backgroundVideoRadioButton.setEnabled(true);
                }
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        initViews();
        initListeners();

    }




}
