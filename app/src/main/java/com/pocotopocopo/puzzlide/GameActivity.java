package com.pocotopocopo.puzzlide;

import android.app.Activity;

import com.pocotopocopo.puzzlide.activities.CreateGameActivity;
import com.pocotopocopo.puzzlide.activities.MultiplayerActivity;
import com.pocotopocopo.puzzlide.activities.PuzzleActivity;
import com.pocotopocopo.puzzlide.activities.StartScreenActivity;

public enum GameActivity{
    START_SCREEN(StartScreenActivity.class),
    PUZZLE(PuzzleActivity.class),
    CREATE_GAME(CreateGameActivity.class),
    MULTIPLAYER(MultiplayerActivity.class);



    private Class<? extends Activity> activityClass;
    GameActivity(Class<? extends Activity> activityClass){
        this.activityClass = activityClass;
    }
    public Class<? extends Activity> getActivityClass(){
        return activityClass;
    }

    public static GameActivity getGameActivity (GameMode gameMode){
        switch (gameMode){
            default:
            case TRADITIONAL:
            case SPEED:
                return PUZZLE;
            case MULTIPLAYER:
                return MULTIPLAYER;
        }
    }
}
