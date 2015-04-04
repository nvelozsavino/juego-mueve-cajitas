package com.pocotopocopo.juego.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.turnbased.LoadMatchesResponse;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchBuffer;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;
import com.pocotopocopo.juego.BackgroundMode;
import com.pocotopocopo.juego.GameActivity;
import com.pocotopocopo.juego.GameConstants;
import com.pocotopocopo.juego.GameInfo;
import com.pocotopocopo.juego.GameMode;
import com.pocotopocopo.juego.R;

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

        GameInfo gameInfo = new GameInfo(4,4, BackgroundMode.PLAIN, GameMode.TRADITIONAL,true);
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
        /*
         * trying to get the match
         */
        //TODO: figure it out if the Google Notification send the match
        Intent data = getIntent();
        if (data!=null) {
            TurnBasedMatch matchReturned = data.getParcelableExtra(Multiplayer.EXTRA_TURN_BASED_MATCH); //not working
            if (matchReturned != null) {
                Toast.makeText(this, "Hay Juego", Toast.LENGTH_SHORT);
            } else {
                Toast.makeText(this, "NO Juego", Toast.LENGTH_SHORT);
            }
        }
    }


    @Override
    public void connected() {
        //TODO: Update multiplayer button with this information
        int[] statusFlags={TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN,TurnBasedMatch.MATCH_TURN_STATUS_INVITED,TurnBasedMatch.MATCH_TURN_STATUS_COMPLETE,TurnBasedMatch.MATCH_TURN_STATUS_THEIR_TURN};
        Games.TurnBasedMultiplayer.loadMatchesByStatus(googleApiClient,statusFlags).setResultCallback(new ResultCallback<TurnBasedMultiplayer.LoadMatchesResult>() {
            @Override
            public void onResult(TurnBasedMultiplayer.LoadMatchesResult loadMatchesResult) {
                LoadMatchesResponse response=loadMatchesResult.getMatches();


                if (response.hasData()){

                    int invitations=response.getInvitations()!=null? response.getInvitations().getCount():0;
                    int turn = response.getMyTurnMatches()!=null? response.getMyTurnMatches().getCount():0;
                    int completed = response.getCompletedMatches()!=null? response.getCompletedMatches().getCount():0;
                    int their_turn = response.getTheirTurnMatches()!=null? response.getTheirTurnMatches().getCount():0;
                    TurnBasedMatchBuffer buffer = response.getCompletedMatches();

                    for (int i=0;i<completed;i++){
                        TurnBasedMatch match = buffer.get(i);
                        String msg;
                        switch (match.getStatus()){
                            case TurnBasedMatch.MATCH_STATUS_AUTO_MATCHING:
                                msg="Auto matching";
                                break;
                            case TurnBasedMatch.MATCH_STATUS_ACTIVE:
                                msg="Active";
                                break;
                            case TurnBasedMatch.MATCH_STATUS_COMPLETE:
                                msg="Complete";
                                break;
                            case TurnBasedMatch.MATCH_STATUS_EXPIRED:
                                msg="Expired";
                                break;
                            case TurnBasedMatch.MATCH_STATUS_CANCELED:
                                msg="Canceled";
                                break;
                            default:
                                msg="Don't know";
                                break;
                        }
                        Log.d(TAG,"Completed Matches: " + i + " status: "+ msg);
                    }


                    //Toast.makeText(getApplicationContext(),"Invitations: " + invitations + ", Turn: " + turn + ", Completed: " + completed + ", Their turn: " + their_turn,Toast.LENGTH_SHORT).show();
                    response.release();
                }


            }
        });

        super.connected();
        multiplayerGameButton.setEnabled(true);
    }

    @Override
    public void disconnected() {
        multiplayerGameButton.setEnabled(false);
        super.disconnected();
    }
}
