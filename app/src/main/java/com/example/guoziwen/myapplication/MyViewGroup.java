package com.example.guoziwen.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by guoziwen on 15/3/29.
 */
public class MyViewGroup extends FrameLayout{
    private int screenH;
    private int mTouchSlop;
    private Paint paint;
    private boolean mAllowLongPress = true;
    private float mLastMotionX;
    private float mLastMotionY;
    private VelocityTracker mVelocityTracker;
    private static final int INVALID_POINTER = -1;
    private int mActivePointerId = INVALID_POINTER;
    private final static int TOUCH_STATE_REST = 0;
    private final static int TOUCH_STATE_SCROLLING = 1;
    private int mTouchState = TOUCH_STATE_REST;
    private static final float NANOTIME_DIV = 1000000000.0f;
    private static final float SMOOTHING_SPEED = 0.75f;
    private static final float SMOOTHING_CONSTANT = (float) (0.016 / Math.log(SMOOTHING_SPEED));
    private float mSmoothingTime;
    private float mTouchX;
    private double initAngle = -Math.PI/6;
    private double realAngle = initAngle;
    private double displayRangeAngle = 2*Math.PI/3;


    private double cursorInitAngle = Math.PI/12;
    private double cursorRealAngle = cursorInitAngle;
    private double angleDistance = cursorRealAngle - realAngle;

    private double dotsInitAngle = -2 * Math.PI/3;
    private double dotsRealAngle = dotsInitAngle;
    private double dotsAngleInterval = Math.PI/6;
    private double dotsCursorDis = cursorRealAngle - dotsRealAngle;

    private double calibInitAngle = -2 * Math.PI/3;
    private double calibRealAngle = calibInitAngle;
    private double calibAngleInterval = Math.PI/6;
    private double calibCursorDis = cursorRealAngle - calibRealAngle;

    private float circleR = 480.0f;
    private float circleX = -90.0f;
    private float circleY = 580.0f;
    private float circle_text_distance = 40.0f;
    private float circle_stroke_width = 30.0f;
    public final String cursor_tag = "image_cursor_tag";
    ImageView circleCursor;

    private int score = 698;
    public void setScore(int score){
        this.score = score;
    }

    private int getStartCalib(){
        int visibleCalib = (int)( Math.PI / 2 * calibAngleInterval);
        int unVisibleCalib = (int)(- calibInitAngle / calibAngleInterval);
        return ((score - 100 * (unVisibleCalib + visibleCalib/2))/100) *100;
    }

    public MyViewGroup(Context context) {
        this(context, null);
    }

    public MyViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    /**
     * Initializes various states for this workspace.
     */

    private void init() {
        Context context = getContext();
        screenH = this.getContext().getResources().getDisplayMetrics().heightPixels;
        paint = new Paint();
        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
        circleCursor = new ImageView(context);
        addView(circleCursor);
        circleCursor.setScaleType(ImageView.ScaleType.CENTER);
        ViewGroup.LayoutParams lp = circleCursor.getLayoutParams();
        lp.width = LayoutParams.WRAP_CONTENT;
        lp.height = LayoutParams.WRAP_CONTENT;
        circleCursor.setLayoutParams(lp);
        circleCursor.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_index_loacation));
        circleCursor.setTag(cursor_tag);
        Animation fadeAnim = AnimationUtils.loadAnimation(context, R.anim.fade_inout);
        circleCursor.startAnimation(fadeAnim);
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        double step = displayRangeAngle / count;
        double tempAngle = realAngle;
        double alpha = 1.0;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != View.GONE && !(child instanceof ImageView)) {
                final int childWidth = child.getMeasuredWidth();
                final int childHeight = child.getMeasuredHeight();
                int locationX = (int)Math.round((circleR+circle_text_distance) * Math.cos(tempAngle) + (circleX - circle_text_distance));
                int locationY =screenH - (int)Math.round((circleR+circle_text_distance) * Math.sin(tempAngle) + circleY);
                child.layout(locationX,locationY,locationX+childWidth,locationY+childHeight);
                alpha  = -1.0* Math.pow((24 * tempAngle - Math.PI) / (7 * Math.PI) ,2.0) +1.0;
                child.setAlpha((float) alpha);
                tempAngle +=step;
            }else if(child instanceof ImageView){
                final int childWidth = child.getMeasuredWidth();
                final int childHeight = child.getMeasuredHeight();
                int locationX = (int)Math.round((circleR - circle_stroke_width/2) * Math.cos(cursorRealAngle) + circleX);
                int locationY = screenH - (int)Math.round((circleR  - circle_stroke_width/2)* Math.sin(cursorRealAngle) + circleY);
                child.layout(locationX - childWidth / 2, locationY - childHeight / 2, locationX - childWidth / 2 + childWidth, locationY - childHeight / 2 + childHeight);
                android.util.Log.e("gzw", " circel R =" + circleR + " x=" + circleX + " y=" + circleY);
            }
        }
    }
    @Override
    protected void dispatchDraw(Canvas canvas) {
        paint.reset();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        canvas.drawCircle(circleX, screenH - circleY, circleR+2, paint);
        paint.setStrokeWidth(circle_stroke_width);
        paint.setColor(Color.BLACK);
        paint.setAlpha(25);
        canvas.drawCircle(circleX, screenH - circleY, circleR - circle_stroke_width / 2, paint);
        paint.reset();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(circle_stroke_width);
        float[] positions = {40f/360f,60f/360f,150f/360f};
        SweepGradient mySweepGrdient = new SweepGradient(circleX, circleY, new int[] {0xff3fccf4,0xff5dd6b2,0xffffa65e}, positions);
        paint.setShader(mySweepGrdient);
        RectF r = new RectF(circleX-(circleR - circle_stroke_width / 2),screenH-circleY-(circleR - circle_stroke_width / 2),circleX+(circleR - circle_stroke_width / 2),screenH-circleY+(circleR - circle_stroke_width / 2));
        canvas.save();
        canvas.rotate((float) (-1.0 * cursorRealAngle * 180.0 / Math.PI), circleX, screenH - circleY);
        canvas.drawArc(r, 0, 180, false, paint);
        canvas.restore();
        if(circleCursor != null){
            paint.reset();
            paint.setColor(0xff3fccf4);
            paint.setStyle(Paint.Style.FILL);
            paint.setAntiAlias(true);
            int x = circleCursor.getLeft() + circleCursor.getWidth()/2;
            int y = circleCursor.getTop() + circleCursor.getHeight()/2;
            canvas.drawCircle(x,y,circleCursor.getWidth()/3,paint);
        }//draw cursor complete
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        canvas.save();
        for(double temp = dotsRealAngle;temp < Math.PI;temp += dotsAngleInterval){
            if(Math.abs(temp - cursorRealAngle) > dotsAngleInterval/3 && temp < cursorRealAngle){
                if(temp == dotsRealAngle){
                    canvas.rotate((float)(-dotsRealAngle * 180 / Math.PI),circleX, screenH - circleY);
                }else {
                    canvas.rotate((float)(-dotsAngleInterval * 180 / Math.PI),circleX, screenH - circleY);
                }
                canvas.drawCircle(circleX + circleR - circle_stroke_width /2,screenH - circleY - circle_stroke_width /2,circle_stroke_width/6,paint);
            }
        }
        canvas.restore();//draw dots complete
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setAlpha(128);
        paint.setTextSize(28);
        paint.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        int fontHeight = (int)(fontMetrics.bottom - fontMetrics.ascent);
        int fontWidth = (int)paint.measureText("000");
        canvas.save();
        int calib = getStartCalib();
        for(double temp = calibRealAngle;temp < Math.PI;temp += calibAngleInterval){
            if(temp == calibRealAngle){
                canvas.rotate((float)(-calibRealAngle * 180 / Math.PI),circleX, screenH - circleY);
                canvas.rotate((float)(calibRealAngle * 180 / Math.PI),circleX + circleR - fontWidth * 2 / 3 - circle_stroke_width, screenH - circleY - 2);
                canvas.drawText(String.valueOf(calib), circleX + circleR - fontWidth * 2 / 3 - circle_stroke_width, screenH - circleY - 2, paint);
                canvas.rotate((float) (-calibRealAngle * 180 / Math.PI), circleX + circleR - fontWidth * 2 / 3 - circle_stroke_width, screenH - circleY - 2);
            }else {
                canvas.rotate((float)(-calibAngleInterval * 180 / Math.PI),circleX, screenH - circleY);
                canvas.rotate((float)(temp * 180 / Math.PI),circleX + circleR - fontWidth * 2 / 3 - circle_stroke_width, screenH - circleY - 2);
                canvas.drawText(String.valueOf(calib), circleX + circleR - fontWidth * 2 / 3 - circle_stroke_width, screenH - circleY - 2, paint);
                canvas.rotate((float) (-temp * 180 / Math.PI), circleX + circleR - fontWidth * 2 / 3 - circle_stroke_width, screenH - circleY - 2);

            }
            calib += 100;
        }
        canvas.restore();//draw calib complete
        super.dispatchDraw(canvas);
    }
    private void acquireVelocityTrackerAndAddMovement(MotionEvent ev) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);
    }

    private void releaseVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        /*
         * This method JUST determines whether we want to intercept the motion.
         * If we return true, onTouchEvent will be called and we do the actual
         * scrolling there.
         */

        /*
         * Shortcut the most recurring case: the user is in the dragging
         * state and he is moving his finger.  We want to intercept this
         * motion.
         */
        final int action = ev.getAction();
        if ((action == MotionEvent.ACTION_MOVE) && (mTouchState != TOUCH_STATE_REST)) {
            android.util.Log.e("gzw","intercept return true");
            return true;
        }

        acquireVelocityTrackerAndAddMovement(ev);

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_MOVE: {
                /*
                 * mIsBeingDragged == false, otherwise the shortcut would have caught it. Check
                 * whether the user has moved far enough from his original down touch.
                 */

                /*
                 * Locally do absolute value. mLastMotionX is set to the y value
                 * of the down event.
                 */
                android.util.Log.e("gzw","moving! in onInter");

                final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                final float x = ev.getX(pointerIndex);
                final float y = ev.getY(pointerIndex);
                final int xDiff = (int) Math.abs(x - mLastMotionX);
                final int yDiff = (int) Math.abs(y - mLastMotionY);

                final int touchSlop = mTouchSlop;
                boolean xMoved = xDiff > touchSlop;
                boolean yMoved = yDiff > touchSlop;

                if (xMoved || yMoved) {

                    if (yMoved) {
                        android.util.Log.e("gzw","y moved! in onInter");

                        // Scroll if the user moved far enough along the X axis
                        mTouchState = TOUCH_STATE_SCROLLING;
                        mLastMotionY = y;
                        mSmoothingTime = System.nanoTime() / NANOTIME_DIV;
                    }
                    // Either way, cancel any pending longpress
                    if (mAllowLongPress) {
                        mAllowLongPress = false;
                        // Try canceling the long press. It could also have been scheduled
                        // by a distant descendant, so use the mAllowLongPress flag to block
                        // everything
                    }
                }
                break;
            }

            case MotionEvent.ACTION_DOWN: {
                final float x = ev.getX();
                final float y = ev.getY();
                // Remember location of down touch
                mLastMotionX = x;
                mLastMotionY = y;
                mActivePointerId = ev.getPointerId(0);
                mAllowLongPress = true;
//                mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
                android.util.Log.e("gzw","touch down! in onInter, pointer id=" + mActivePointerId);
                android.util.Log.e("gzw","touch down! in onInter, touch state=" + mTouchState);
                break;
            }

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:

                // Release the drag
                mTouchState = TOUCH_STATE_REST;
                mActivePointerId = INVALID_POINTER;
                mAllowLongPress = false;
                releaseVelocityTracker();
                break;

            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;
        }

        /*
         * The only time we want to intercept motion events is if we are in the
         * drag mode.
         */
        android.util.Log.e("gzw","intercept return" + (mTouchState != TOUCH_STATE_REST));

        return mTouchState != TOUCH_STATE_REST;
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        acquireVelocityTrackerAndAddMovement(ev);

        final int action = ev.getAction();

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:

                // Remember where the motion event started
                mLastMotionX = ev.getX();
                mLastMotionY = ev.getY();
                mActivePointerId = ev.getPointerId(0);
                android.util.Log.e("gzw","touch down! in onTouch, pointer id=" + mActivePointerId);
                break;
            case MotionEvent.ACTION_MOVE:
                android.util.Log.e("gzw","moving! in onTouch");

                if (true/*mTouchState == TOUCH_STATE_SCROLLING*/) {
                    // Scroll to follow the motion event
                    final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                    final float y = ev.getY(pointerIndex);
                    final float deltaY = mLastMotionY - y;
                    mLastMotionY = y;
                    android.util.Log.e("gzw","moving! in onTouch scrolling");

                    if (deltaY < 0) {
                        if(realAngle > initAngle - Math.PI/6){
                            realAngle -= 0.04;
                            cursorRealAngle = realAngle + angleDistance;
                            dotsRealAngle = cursorRealAngle -dotsCursorDis;
                            calibRealAngle = cursorRealAngle - calibCursorDis;
                            mSmoothingTime = System.nanoTime() / NANOTIME_DIV;
                            requestLayout();
                            invalidate();
                            android.util.Log.e("gzw","move down");
                        }
                    } else if (deltaY > 0) {
                        if(realAngle < -initAngle){
                            realAngle += 0.04;
                            cursorRealAngle = realAngle + angleDistance;
                            dotsRealAngle = cursorRealAngle -dotsCursorDis;
                            calibRealAngle = cursorRealAngle - calibCursorDis;
                            mSmoothingTime = System.nanoTime() / NANOTIME_DIV;
                            requestLayout();
                            invalidate();
                            android.util.Log.e("gzw","move up");
                        }

                    } else {
                        awakenScrollBars();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                mTouchState = TOUCH_STATE_REST;
                mActivePointerId = INVALID_POINTER;
                releaseVelocityTracker();
                break;
            case MotionEvent.ACTION_CANCEL:
                mTouchState = TOUCH_STATE_REST;
                mActivePointerId = INVALID_POINTER;
                releaseVelocityTracker();
                break;
            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;
        }

        return true;
    }
    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >>
                MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            // TODO: Make this decision more intelligent.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mLastMotionX = ev.getX(newPointerIndex);
            mLastMotionY = ev.getY(newPointerIndex);
            mActivePointerId = ev.getPointerId(newPointerIndex);
            if (mVelocityTracker != null) {
                mVelocityTracker.clear();
            }
        }
    }

    public double getRealAngle(){
        return realAngle;
    }
    public void setRealAngle(double realAngle){
        this.realAngle = realAngle;
    }


}
