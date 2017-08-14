package com.zd.miko.riji.CustomView;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;

import com.zd.miko.riji.Utils.Utils;

/**
 * Created by Miko on 2017/7/12.
 */

public class RippleView extends RelativeLayout {


    boolean isAnim;
    private WindowManager wm;
    private int extendX;
    private int extendY;
    private onRippleListener ripplelistener;

    public void setRipplelistener(onRippleListener endlistener) {
        this.ripplelistener = endlistener;
    }

    public int getScreenWidth() {
        if (wm == null)
            wm = (WindowManager) this.getContext()
                    .getSystemService(Context.WINDOW_SERVICE);
        Point p = new Point();
        wm.getDefaultDisplay().getSize(p);
        return p.x;
    }

    public int getScreenHeight() {
        if (wm == null)
            wm = (WindowManager) this.getContext()
                    .getSystemService(Context.WINDOW_SERVICE);
        Point p = new Point();
        wm.getDefaultDisplay().getSize(p);
        return p.y;
    }

    /**
     * 覆盖计数器，确保listener中的over只调用一次
     */
    private int overCounter,beginCounter;

    private float maxRadius;
    private float outRadius;
    private float centerRadius;
    private int width;//设置高
    private int height;//设置高
    private Paint mPaint;
    private int ovColor = Color.parseColor("#ba0001");
    private String contentText;
    private int extendDuration = 500, eraseDuration = 300;
    //设置一个Bitmap
    private Bitmap bitmap;
    //创建该Bitmap的画布
    private Canvas bitmapCanvas;
    private Paint mPaintCirlcle;
    private Paint mPaintRect;

    PorterDuffXfermode mode;

    public int getOvColor() {
        return ovColor;
    }

    public void setOvColor(int ovColor) {
        this.ovColor = ovColor;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public int getExtendDuration() {
        return extendDuration;
    }

    public void setExtendDuration(int extendDuration) {
        this.extendDuration = extendDuration;
    }

    public int getEraseDuration() {
        return eraseDuration;
    }

    public void setEraseDuration(int eraseDuration) {
        this.eraseDuration = eraseDuration;
    }

    public RippleView(Context context) {
        super(context);
    }

    public RippleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();//Bitmap的画笔

        mPaintCirlcle = new Paint();
        mPaintCirlcle.setAntiAlias(true);
        mPaintCirlcle.setColor(ovColor);

        mPaintRect = new Paint();
        mPaintRect.setAntiAlias(true);
        mPaintRect.setColor(Color.GRAY);
        mode = new PorterDuffXfermode(PorterDuff.Mode.XOR);

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        if (maxRadius == 0) {
            maxRadius = (float) Math.sqrt(width * width + height * height);
        }
        setMeasuredDimension(width, height);//设置宽和高
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        // 记录总高度
        // 遍历所有子视图
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if (childView.getTag() != null) {
                childView.setOnClickListener(view -> startAnimotion(view));
            }

        }
    }

    public void startBounceAnimotion(View view) {


    }

    public void startAnimotion(View view) {

        bitmap = Bitmap.createBitmap(getScreenWidth(), getScreenHeight(),
                Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(bitmap);//该画布为bitmap的


        CircleButton cb = (CircleButton) view;
        mPaintCirlcle.setColor(cb.getBgColor());
        centerRadius = 0;
        int index = ((CircleButton) view).getIndex();

        extendX = view.getLeft() + view.getWidth() / 2;
        extendY = view.getTop() + view.getHeight() / 2;

        final ValueAnimator va1 = ValueAnimator.ofFloat(0, maxRadius);
        va1.setDuration(eraseDuration);
        va1.addUpdateListener(valueAnimator -> {
            centerRadius = (float) valueAnimator.getAnimatedValue();
            invalidate();
        });
        va1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                isAnim = false;
                bitmap.recycle();
                if (ripplelistener != null)
                    ripplelistener.onEnd(index);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });


        ValueAnimator va = ValueAnimator.ofFloat(view.getWidth() / 2-10, maxRadius);
        va.setDuration(extendDuration);
        va.setInterpolator(new AccelerateDecelerateInterpolator());
        va.addUpdateListener(valueAnimator -> {
            outRadius = (float) valueAnimator.getAnimatedValue();
            invalidate();

            int i = (view.getLeft() + width / 2) * (view.getLeft() + width / 2)
                    + (getHeight() - view.getTop() - width / 2)
                    * (getHeight() - view.getTop() - width / 2);

            int overRadius = (int) Math.sqrt(i);

            if((outRadius+ Utils.dpToPx(180))>=overRadius){
                beginCounter++;
                if (ripplelistener != null && beginCounter == 1) {
                    ripplelistener.onStartOverCurrent(index);
                }

            }


            if (outRadius > overRadius) {
                overCounter++;
                if (ripplelistener != null && overCounter == 1) {
                    ripplelistener.onOver(index);
                }
            }

            if (outRadius >= maxRadius * 0.8) {
                va1.start();
            }
        });
        va.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                isAnim = true;
                if (ripplelistener != null)
                    ripplelistener.onStart(index);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                overCounter = 0;
                beginCounter = 0;
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        va.start();
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (isAnim) {
            bitmapCanvas.drawCircle(extendX, extendY, outRadius, mPaintCirlcle);
            mPaintRect.setXfermode(mode);

            bitmapCanvas.drawCircle(extendX, extendY, centerRadius, mPaintRect);

            mPaintRect.setXfermode(null);
            canvas.drawBitmap(bitmap, 0, 0, mPaint);
        }

    }

    public interface onRippleListener {
        void onEnd(int index);

        void onStart(int index);

        void onOver(int index);

        void onStartOverCurrent(int index);
    }
}

