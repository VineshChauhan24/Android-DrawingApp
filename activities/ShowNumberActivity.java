package gl2.kasri.younes.paintapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.TextView;

import gl2.kasri.younes.paintapplication.Dev;
import gl2.kasri.younes.paintapplication.R;
import gl2.kasri.younes.paintapplication.helpers.Level;

public class ShowNumberActivity extends AppCompatActivity {

    public final static int DRAW_NUMBER_REQUEST = 200;  // The request code

    private TextView numberTextView;

   // TODO  public static Boolean DRAW_ANIMATION = false;
    protected boolean startedDrawingActivity = false;
    protected boolean gameOver = false;

    protected Level currentLevel;

    public void endTheGame(boolean wonTheGame){

        numberTextView.setTextSize(32);
        numberTextView.setText("Game Over");
        gameOver = true;

        currentLevel = new Level(); // reinitialiser à 0

        if (wonTheGame) {
            setResult(RESULT_OK);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    private void updateNumberTextView() {
        numberTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,currentLevel.getNumbersFontSize());
        numberTextView.setText(""+currentLevel.getNumber());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_number);
        numberTextView = findViewById(R.id.number);
        currentLevel = new Level();
        updateNumberTextView();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!startedDrawingActivity && !gameOver) {
            startedDrawingActivity = true;
            Intent intent = new Intent(ShowNumberActivity.this, DrawActivity.class);
            intent.putExtra("number", currentLevel.getNumber());
            intent.putExtra("difficulty", currentLevel.getDifficultyLevel());
            startActivityForResult(intent, DRAW_NUMBER_REQUEST);
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if ( startedDrawingActivity && requestCode == DRAW_NUMBER_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Log.i(Dev.TAG, "RESULT_OK onActivityResult: moving to nextNumber");
                currentLevel.nextNumber();
                if ( currentLevel.isOver() ) {
                    endTheGame(true);
                }
            } else if (resultCode == RESULT_CANCELED ){
                Log.i(Dev.TAG, "WRONG_ANSWER onActivityResult: Try again with the same number");
            }
            if (!gameOver) {
                updateNumberTextView();
            }
        }

        startedDrawingActivity = false;
    }


}
