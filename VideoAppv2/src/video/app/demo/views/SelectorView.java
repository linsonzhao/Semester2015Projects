package video.app.demo.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Custom view where the red square is drawed on to be shown on the
 * TrackingSelection activity.
 * 
 * Also, contains methods that return the X and Y coordinates from
 * the top left corner of the square, and square size, needed to 
 * send the server to track the object.
 * 
 * @author victor
 *
 */
public class SelectorView extends ImageView {
	
	float x, y;
	Bitmap bmp;
	Paint mPaint;
	float width = 100.0f;
	float height = 100.0f;
	boolean touched = false;
	Matrix m;
	
	public SelectorView(Context context) {
		super(context);
		
		x = y = 0;
		mPaint = new Paint();
		mPaint.setColor(Color.RED);
		mPaint.setStyle(Style.STROKE);
		mPaint.setStrokeWidth(5);
		
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);	
		if(touched)
			canvas.drawRect(x-(width/2), y-(height/2), x+(width/2), y+(height/2), mPaint);				
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		touched = true;
		
		x = event.getX();
		y = event.getY();
				
		invalidate();
		
		return true;
	}
	
	public void updateSquareSize(int size) {
		width = height = size;
		invalidate();
	}
	
	public int getPositionX() {
		return Math.round(x-(width/2));
	}
	
	public int getPositionY() {
		return Math.round(y-(height/2));
	}
		
	public int getSquareWidth() {
		return Math.round(width);
	}
	
	public int getSquareHeight() {
		return Math.round(height);
	}
	
	public boolean hasTouched() {
		return touched;
	}
			
}
