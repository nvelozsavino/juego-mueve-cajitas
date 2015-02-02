package com.pocotopocopo.juego;

import android.app.Activity;
import android.content.Intent;
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

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Game;

public class StartScreen extends Activity {
    private EditText cols;
    private EditText rows;
    private Button start;
    private static final String TAG = "Juego.StartScreen";
    public static final String COLS_KEY = "colsNumber";
    public static final String ROWS_KEY = "rowsNumber";
    public static final String GAME_MODE= "gameMode";
    public static final String BACKGROUND_MODE= "backgroundMode";

    public static final String GOOGLE_CLIENT= "googleClient";

    public static final String SHOW_NUMBERS = "showNumbers";

    private GoogleApiClient googleApiClient;
    private Button gameSize3x3Button;
    private Button gameSize4x4Button;
    private Button gameSize5x5Button;
    private Button gameSize6x6Button;
    private Button gameSizeCustomButton;

    private RadioGroup gameModeRadioGroup;



    private RadioGroup backgroundRadioGroup;



    private CheckBox showNumbersCheckBox;

    private void initViews(){
        gameSize3x3Button= (Button)findViewById(R.id.gameSize3x3Button);
        gameSize4x4Button= (Button)findViewById(R.id.gameSize4x4Button);
        gameSize5x5Button= (Button)findViewById(R.id.gameSize5x5Button);
        gameSize6x6Button= (Button)findViewById(R.id.gameSize6x6Button);
        gameSizeCustomButton= (Button)findViewById(R.id.gameSizeCustomButton);

        gameModeRadioGroup=(RadioGroup)findViewById(R.id.gameModeRadioGroup);

        showNumbersCheckBox = (CheckBox)findViewById(R.id.showNumbersCheckBox);

        backgroundRadioGroup=(RadioGroup)findViewById(R.id.backgroundRadioGroup);


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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        initViews();
        initListeners();

    }


}
