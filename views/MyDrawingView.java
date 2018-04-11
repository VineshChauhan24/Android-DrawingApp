package gl2.kasri.younes.paintapplication.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;

import gl2.kasri.younes.paintapplication.activities.DrawActivity;
import gl2.kasri.younes.paintapplication.helpers.Circle;
import gl2.kasri.younes.paintapplication.helpers.Level;

import static gl2.kasri.younes.paintapplication.Dev.TAG;


public class MyDrawingView extends CanvasView {

    static final float DX = 30, DY = 30; // Tolérance dx et dy pour marquer un point

    private DrawActivity drawActivity; // The calling activity

    private Level currentLevel;

    private Paint drawingPaint;
    private Paint pointsPaint;

    private List<Point>  points = null;
    private List<Circle> circles = null; // Intervalles d'acceptation ( you can make it visible if you wish )


    /** Constructor */
    public MyDrawingView(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
        pointsPaint = makePaint(Color.DKGRAY, 10f);
        drawingPaint = makePaint(Color.BLACK, 40f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (points == null) {
            initPointsAndCircles();
        }

       // drawCircles();
        drawNumber();
        canvas.drawPath(path, drawingPaint);
    }

    private void initPointsAndCircles() {
        points = currentLevel.getNumberWithPoints();
        circles = new ArrayList<>();
        for (Point p : points){
            circles.add( new Circle(p.x, p.y) );
        }
    }

    boolean wasOutOfBounds;
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        float x = event.getX();
        float y = event.getY();

        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if ( isOutOfBounds(x, y) ){
                    wasOutOfBounds = true;
                    return true;
                }
                startTouch(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if ( isOutOfBounds(x, y)  ){
                    clearCanvasAndRefreshPoints();
                    wasOutOfBounds = true;
                    return true;
                }
                if (wasOutOfBounds){
                    return true;
                }
                if (marquerLePoint(x,y))
                    Log.i(TAG, "onTouchEvent: J'ai marqué le point " + x +"-"+y);
                moveTouch(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (wasOutOfBounds){
                    checkRemainingAttempts();
                    path.moveTo(x,y);
                    wasOutOfBounds = false;
                    return true;
                }
                upTouch();
                invalidate();
                if (points.size() == 0){
                    drawActivity.correctAnswer();
                }

                break;
        }
        return true;
    }

    private boolean marquerLePoint(float x, float y){

        boolean unPointEstMarque = false;
        for (int i = 0; i<points.size(); i++){
            float dx = Math.abs(x - points.get(i).x * density);
            float dy = Math.abs(y - points.get(i).y * density);

            if (dx < DX  && dy < DY){
                points.remove(i);
                unPointEstMarque = true;
            }

        }

        return unPointEstMarque;
    }

    private void checkRemainingAttempts(){
        int remainingAttempts = currentLevel.checkRemainingAttempts();
        drawActivity.showToast("Attention ! Il vous reste " + remainingAttempts +" tentative(s)");
        if ( remainingAttempts == 0 ){
            drawActivity.wrongAnswer();
            currentLevel.refreshRemainingAttempts();
        }
    }

    public boolean isOutOfBounds(float x, float y){
        for (Circle circle : circles) {
            if ( circle.doesSurround(x/density,y/density) ) {
                return false; // Le point est à l'interieur du cercle (y)
            }
        }
        return true;
    }

    public void clearCanvasAndRefreshPoints(){
        path.reset();
        points = currentLevel.getNumberWithPoints();
        invalidate();
    }


    public void showDrawingAnimation() { // TODO

        if ( canvas == null ){
            Bitmap bitmap = Bitmap.createBitmap(500, 380, Bitmap.Config.ARGB_8888);
            this.canvas = new Canvas(bitmap);
        }

        Log.i(TAG, "showDrawingAnimation: canvas = "+canvas.toString());
        initPointsAndCircles();
        for (int i = 0, n = points.size(); i<n-1; i++){
            /* Point A = points.get(i);
            Point B = points.get(i+1);

            canvas.drawLine(A.x * density, A.y * density,
                    B.x * density, B.y * density, drawingPaint);

            invalidate();

            try { Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Point A = points.get(i);
            Point B = points.get(i+1);
            canvas.drawLine(A.x * density, A.y * density,
                    B.x * density, B.y * density, drawingPaint);
*/

        //    Log.i(TAG, "showDrawingAnimation: " + A.toString() + " --> "+ B.toString());

        }
         drawCircles();

    }

    /** The calling activity */
    public void setDrawActivity(DrawActivity drawActivity){
        this.drawActivity = drawActivity;
    }

    public void setCurrentLevel(Level level) {
        currentLevel = level;
    }

    private void drawNumber(){
        for (Point p : points) {
            float radius = currentLevel.getPointsRadius(density);
            canvas.drawCircle(p.x * density, p.y * density, radius, pointsPaint);
        }
    }

    private void drawCircles() {
        for (Circle c : circles) {
            float radius = c.radius * density / currentLevel.getDifficultyLevel(); // TODO
            canvas.drawCircle(c.x * density, c.y * density, radius, c.paint);
        }
    }


      /*   protected void onSizeChanged(int w, int h, int oldw, int oldh) {
       super.onSizeChanged(w, h, oldw, oldh);

        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
    }
*/

}

