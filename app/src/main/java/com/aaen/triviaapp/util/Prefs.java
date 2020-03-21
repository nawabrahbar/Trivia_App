package com.aaen.triviaapp.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {
    private SharedPreferences sharedPreferences;

    public Prefs(Activity activity) {
        this.sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
    }

    public void setHighScore(int score){
        int highScore = sharedPreferences.getInt("highScore", 0);

        if(score >highScore){
            //here we're saving the HighScore.
            sharedPreferences.edit().putInt("highScore", score).apply();
        }
    }

    public int getHighScore(){
        return sharedPreferences.getInt("highScore",0);
    }

    public void setState(int index){
        sharedPreferences.edit().putInt("state", index).apply();
    }

    public int getState(){
        return sharedPreferences.getInt("state", 0);
    }
}
