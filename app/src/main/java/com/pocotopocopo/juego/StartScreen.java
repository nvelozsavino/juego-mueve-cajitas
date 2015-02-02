package com.pocotopocopo.juego;

import android.app.Activity;
import android.content.Intent;
import android.provider.BaseColumns;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.games.Game;
import com.google.android.gms.games.Games;

public class StartScreen extends BaseActivity {
    private EditText cols;
    private EditText rows;
    private Button start;
    private static final String TAG = "Juego.StartScreen";
    public static final String COLS_KEY = "colsNumber";
    public static final String ROWS_KEY = "rowsNumber";
    public static final String GAME_MODE= "gameMode";
    public static final String BACKGROUND_MODE= "backgroundMode";


    public static final String SHOW_NUMBERS = "showNumbers";

    private Button gameSize3x3Button;
    private Button gameSize4x4Button;
    private Button gameSize5x5Button;
    private Button gameSize6x6Button;
    private Button gameSizeCustomButton;

    private RadioGroup gameModeRadioGroup;




    private RadioGroup backgroundRadioGroup;
//    private RadioButton backgroundPlainRadioButton;
//    private RadioButton backgroundImageRadioButton;
//    private RadioButton backgroundVideoRadioButton;



    private CheckBox showNumbersCheckBox;

    private SignInButton signInButton;
    private Button signOutButton;


    private void initViews(){
        gameSize3x3Button= (Button)findViewById(R.id.gameSize3x3Button);
        gameSize4x4Button= (Button)findViewById(R.id.gameSize4x4Button);
        gameSize5x5Button= (Button)findViewById(R.id.gameSize5x5Button);
        gameSize6x6Button= (Button)findViewById(R.id.gameSize6x6Button);
        gameSizeCustomButton= (Button)findViewById(R.id.gameSizeCustomButton);

        gameModeRadioGroup=(RadioGroup)findViewById(R.id.gameModeRadioGroup);

        showNumbersCheckBox = (CheckBox)findViewById(R.id.showNumbersCheckBox);

        backgroundRadioGroup=(RadioGroup)findViewById(R.id.backgroundRadioGroup);
//        backgroundPlainRadioButton=(RadioButton)findViewById(R.id.backgroundPlainRadioButton);
//        backgroundImageRadioButton=(RadioButton)findViewById(R.id.backgroundImageRadioButton);
//        backgroundVideoRadioButton=(RadioButton)findViewById(R.id.backgroundVideoRadioButton);


        signInButton=(SignInButton)findViewById(R.id.signInButton);
        signOutButton=(Button)findViewById(R.id.signOutButton);


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
        Intent startGame = new Intent(getApplicationContext(), MainActivity.class);
        startGame.putExtra(COLS_KEY, cols);
        startGame.putExtra(ROWS_KEY, rows);
        startGame.putExtra(BACKGROUND_MODE,backgroundMode);
        startGame.putExtra(GAME_MODE,gameMode);
        startGame.putExtra(SHOW_NUMBERS,showNumbers);
        startGame.putExtra(BaseActivity.SIGNED_IN,mSignInClicked);
        startActivity(startGame);
    }

    private void initListeners(){
        gameSize3x3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(3,3);
            }
        });
        gameSize4x4Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(4,4);
            }
        });
        gameSize5x5Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(5,5);
            }
        });
        gameSize6x6Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(6,6);
            }
        });
        gameSizeCustomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: create a dialog asking for the size
                startGame(4,4);
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
                displaySignIn();

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
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        initViews();
        initListeners();

    }

    @Override
    public void displaySignIn() {
        signInButton.setVisibility(View.VISIBLE);// Put code here to display the sign-in button
        signOutButton.setVisibility(View.GONE);
    }

    @Override
    public void hideSignIn() {
        signInButton.setVisibility(View.GONE);// Put code here to display the sign-in button
        signOutButton.setVisibility(View.VISIBLE);

    }


}
