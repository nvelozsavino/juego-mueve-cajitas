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
    private Button multiplayerGameButton;

    private ImageView logo;




    @Override
    protected void initViews(){
        super.initViews();
        startGameButton = (Button) findViewById(R.id.startGameButton);
        quickGameButton = (Button) findViewById(R.id.quickGameButton);
        multiplayerGameButton=(Button)findViewById(R.id.multiplayerGameButton);
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

        multiplayerGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent multiplayerGameIntent=new Intent(getApplicationContext(),GameActivity.MULTIPLAYER.getActivityClass());
                startActivity(multiplayerGameIntent);
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
        setContentView(R.layout.start_screen_activity_layout);
        initViews();
        initListeners();
    }


    @Override
    public void connected() {

        super.connected();
        multiplayerGameButton.setEnabled(true);
    }

    @Override
    public void disconnected() {
        multiplayerGameButton.setEnabled(false);
        super.disconnected();
    }
}
