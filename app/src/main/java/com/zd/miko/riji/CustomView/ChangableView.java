package com.zd.miko.riji.CustomView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.zd.miko.riji.R;

/**
 * Created by Miko on 2016/2/17.
 * 可以变换颜色的自定义View
 */
public class ChangableView extends View {

    private int mBgColor = 0xFF45C01A;
    private Bitmap mIconBitmap;
    private String mText = "易迅";

    private Canvas mBgCanvas;
    private Bitmap mBgBitmap;
    private Paint mBgPaint;
    private Paint mTextPaint;
    private float mAlpha = 1.0f;

    //提前创建，避免在onMeasure中重复调用
    private Rect mIconRect = new Rect();
    private Rect mTextRect;

    private int mTextSize ;


    public ChangableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ChangeColorIconWithText);

        BitmapDrawable mDrawable = (BitmapDrawable) a.getDrawable(R.styleable.ChangeColorIconWithText_Icon);
        assert mDrawable != null;
        mIconBitmap = mDrawable.getBitmap();
        mText = a.getString(R.styleable.ChangeColorIconWithText_text);
        mBgColor = a.getColor(R.styleable.ChangeColorIconWithText_Color, 0xFF45C01A);
        mTextSize = (int) a.getDimension(R.styleable.ChangeColorIconWithText_text_size
                ,TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,15,getResources().getDisplayMetrics()));
        a.recycle();

        mTextRect = new Rect();
        mTextPaint = new Paint();
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(0Xff555555);
        mTextPaint.getTextBounds(mText, 0, mText.length(), mTextRect);

    }

    public ChangableView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChangableView(Context context) {
        this(context, null);
    }

    /**测量Icon的长与宽，以及各自的起始点*/
    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int iconWidth = Math.min(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(),
                getMeasuredHeight() - getPaddingTop() - getPaddingBottom()
                        - mTextRect.height());

        int iconLeft = (getMeasuredWidth() - iconWidth) / 2;
        int iconTop = getMeasuredHeight() / 2 - (iconWidth + mTextRect.height()) / 2;

        //避免重复创建对象
        mIconRect.set(iconLeft,iconTop,iconLeft+iconWidth,iconTop+iconWidth);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        int alpha = (int) Math.ceil(255 * mAlpha);
        canvas.drawBitmap(mIconBitmap, null, mIconRect, null);
        setupTargetBitmap(alpha);
        //在setupTargetBitmap中mBgBitmap已经完成了覆盖，已经定位完毕，因此不需要再次进行定位
        canvas.drawBitmap(mBgBitmap, 0, 0, null);

        drawSourceText(canvas, alpha);
        setupTargetText(canvas, alpha);

    }

    private void drawSourceText(Canvas canvas, int alpha) {

        mTextPaint.setColor(0xff333333);
        mTextPaint.setAlpha(255 - alpha);
        int x = (getMeasuredWidth() - mTextRect.width()) / 2;
        int y = mIconRect.bottom + mTextRect.height() / 2;
        canvas.drawText(mText, x, y, mTextPaint);
    }

     /**
      * 在mBgCanvas中对mBgBitmap进行操作
      */
    private void setupTargetBitmap(int alpha) {
        mBgBitmap = Bitmap.createBitmap(getMeasuredWidth(),
                getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        mBgCanvas = new Canvas(mBgBitmap);
        mBgPaint = new Paint();
        mBgPaint.setColor(mBgColor);
        mBgPaint.setAlpha(alpha);
        //抗锯齿
        mBgPaint.setAntiAlias(true);
        //抗抖动
        mBgPaint.setDither(true);
        mBgCanvas.drawRect(mIconRect,mBgPaint);
        mBgPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        /*
        * 用自定义的paint绘制出图形
        * 实际上是对mBgCanvas进行操作，最终影响的是mBgBitmap
        * */
        mBgCanvas.drawBitmap(mIconBitmap, null, mIconRect, mBgPaint);


    }

    public void setupTargetText(Canvas canvas, int alpha) {

        mTextPaint.setColor(mBgColor);
        mTextPaint.setAlpha(alpha);
        int x = (getMeasuredWidth() - mTextRect.width()) / 2;
        int y = mIconRect.bottom + mTextRect.height() / 2;
        canvas.drawText(mText, x, y, mTextPaint);
    }


    /*
    * setting the alpha of the icon
    * */
    public void setIconAlpha(float alpha) {
        this.mAlpha = alpha;
        invalidateView();
    }

    /*
    *Toggle of change
    * */
    public void ChangeColorToggle() {
        if (this.mAlpha == 1.0f)
            setIconAlpha(0);
        else
            setIconAlpha(1.0f);
    }

    /*
    * Change Bitmap to transport
    * */
    public void ChangeToTp() {
        setIconAlpha(0);
    }
    /*
    * Change Bitmap to opacity
    * */

    public void ChangeToOp() {
        setIconAlpha(1.0f);
    }

    private static final String INSTANCE_STATUS = "instance_status";
    private static final String STATUS_ALPHA = "status_alpha";

    @Override
    protected Parcelable onSaveInstanceState() {

        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATUS, super.onSaveInstanceState());
        bundle.putFloat(STATUS_ALPHA, mAlpha);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {

        if (state instanceof Bundle) {

            Bundle bundle = (Bundle) state;
            mAlpha = bundle.getFloat(STATUS_ALPHA);
            Parcelable parcelable = bundle.getParcelable(INSTANCE_STATUS);
            super.onRestoreInstanceState(parcelable);
            return;
        }
    }

    /**
     * 重绘
     */
    private void invalidateView() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }


}
