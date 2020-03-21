package com.aaen.triviaapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.aaen.triviaapp.data.AnswerListAsyncResponse;
import com.aaen.triviaapp.data.QuestionBank;
import com.aaen.triviaapp.model.Question;
import com.aaen.triviaapp.model.Score;
import com.aaen.triviaapp.util.Prefs;
import com.aaen.triviaapp.util.Utils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView statement;
    private TextView counter;
    private TextView score;
    public CardView cardView;
    List<Question> questionList;
    boolean answer;
    private Score scoreObject;
    private int currentIndex=0;
    private int currentScore=0;
    private String MESSAGE_ID="";
    private Prefs prefs;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).hide();
        }

        scoreObject = new Score();
        prefs = new Prefs(MainActivity.this);
        currentIndex = prefs.getState();

        cardView = findViewById(R.id.card);
        statement = findViewById(R.id.statement);
        score = findViewById(R.id.score);
        TextView highScore = findViewById(R.id.highScore);
        counter = findViewById(R.id.count);
        Button truebtn = findViewById(R.id.true_button);
        Button falsebtn = findViewById(R.id.false_button);
        ImageButton next = findViewById(R.id.next_button);
        ImageButton prev = findViewById(R.id.prev_button);

        truebtn.setOnClickListener(this);
        falsebtn.setOnClickListener(this);
        next.setOnClickListener(this);
        prev.setOnClickListener(this);

        highScore.setText(MessageFormat.format("High Score: {0}", String.valueOf(prefs.getHighScore())));
        score.setText(MessageFormat.format("Score: {0}", String.valueOf(scoreObject.getScore())));


        questionList = new QuestionBank().getQuestions(new AnswerListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Question> questionArrayList) {
                updateQuestion();
            }
        });

    }

    @Override
    protected void onPause() {
        prefs.setState(currentIndex);
        super.onPause();
    }

    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }

    private void updateQuestion(){
        statement.setText(questionList.get(currentIndex).getQuestion());
        String s1 = (currentIndex+1)+" out of "+questionList.size();
        counter.setText(s1);
    }

    private void increaseScore() {
        currentScore += 5;
        scoreObject.setScore(currentScore);
        score.setText(MessageFormat.format("Score: {0}", String.valueOf(scoreObject.getScore())));
    }

    private void decreaseScore() {
        currentScore--;
        scoreObject.setScore(currentScore);
        if(currentScore>=0){
            score.setText(MessageFormat.format("Score: {0}", String.valueOf(scoreObject.getScore())));
        }else {
            currentScore = 0;
            score.setText(MessageFormat.format("Score: {0}", String.valueOf(scoreObject.getScore())));
        }
    }

    @Override
    public void onClick(View v) {
        int happy = 0x1F60A, sad = 0x1F614;
        String h="", s="";
        h = getEmojiByUnicode(happy);
        s = getEmojiByUnicode(sad);
        switch (v.getId()){
            case R.id.true_button:
                    if(answer){
                        Utils.showSnackBar(v,"Yes it's correct "+h, this);
                        fadeColor();
                        currentIndex = (currentIndex + 1)% questionList.size();
                        increaseScore();
                        prefs.setHighScore(currentScore);
                        updateQuestion();
                    }else {
                        shakeAnimation();
                        Utils.showSnackBar(v, "No it's wrong "+s, this);
                        decreaseScore();
                        updateQuestion();
                    }
                break;

            case R.id.false_button:
                if(answer){
                    shakeAnimation();
                    Utils.showSnackBar(v, "No it's wrong "+s, this);
                    decreaseScore();
                    updateQuestion();
                }
                else {
                    Utils.showSnackBar(v,"Yes it's correct "+h, this);
                    fadeColor();
                    currentIndex = (currentIndex + 1)% questionList.size();
                    increaseScore();
                    prefs.setHighScore(currentScore);
                    updateQuestion();
                }
                break;

            case R.id.next_button:
                currentIndex = (currentIndex + 1)% questionList.size();
                updateQuestion();
                break;

            case R.id.prev_button:
                currentIndex--;
                if(currentIndex>=0)
                    updateQuestion();
                else {
                    currentIndex=questionList.size()-1;
                    updateQuestion();
                }
                break;
        }
    }

    private void fadeColor(){
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(350);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        cardView.setAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.GREEN);
                statement.setTextColor(Color.WHITE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
                statement.setTextColor(getResources().getColor(R.color.textColor));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void shakeAnimation(){
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake_anim);
        cardView.setAnimation(shake);

        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.RED);
                statement.setTextColor(Color.WHITE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
                statement.setTextColor(getResources().getColor(R.color.textColor));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void shareDetails(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Hey friends, my high score is "+prefs.getHighScore()+" and current score is "+currentScore+".");
        intent.putExtra(Intent.EXTRA_SUBJECT,"Hey see my score. !!");
        startActivity(intent);
    }
}
