package com.pocotopocopo.juego;

import android.app.Activity;

public enum GameActivity{
    START_SCREEN(StartScreen.class),
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
}
