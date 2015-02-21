package com.pocotopocopo.juego;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class StartScreenActivity extends BaseActivity{

    private static final String TAG = "StartScreenActivity";

    private Button startGameButton;
    private Button quickGameButton;

    private ImageView logo;




    @Override
    protected void initViews(){
        super.initViews();
        startGameButton = (Button) findViewById(R.id.startGameButton);
        quickGameButton = (Button) findViewById(R.id.quickGameButton);
        logo = (ImageView)findViewById(R.id.logoImage);
    }


    private void startGame(){

        GameInfo gameInfo = new GameInfo(4,4,BackgroundMode.PLAIN,GameMode.TRADITIONAL,true);
        Intent startGame = new Intent(getApplicationContext(), GameActivity.CREATE_GAME.getActivityClass());
        startGame.putExtra(GameConstants.GAME_INFO,gameInfo);
        startActivity(startGame);

    }

    private void initListeners(){

        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame();
            }
        });


        quickGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQuickGame();
            }
        });

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame();
            }
        });


    }

    private void startQuickGame() {
        GameInfo gameInfo = new GameInfo(4,4,BackgroundMode.PLAIN,GameMode.TRADITIONAL,true);
        Intent startGame = new Intent(getApplicationContext(), GameActivity.PUZZLE.getActivityClass());
        startGame.putExtra(GameConstants.GAME_INFO,gameInfo);
        startActivity(startGame);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        initViews();
        initListeners();
    }

}
