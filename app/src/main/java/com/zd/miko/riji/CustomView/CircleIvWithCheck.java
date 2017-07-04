package com.zd.miko.riji.CustomView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.zd.miko.riji.R;

import de.hdodenhof.circleimageview.CircleImageView;




public class CircleIvWithCheck extends RelativeLayout {
    private ImageView checkIv;
    private int color;

    public CircleIvWithCheck(Context context) {
        this(context, null);
    }

    public CircleIvWithCheck(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleIvWithCheck(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CircleIvWithCheck);
        color = array.getColor(R.styleable.CircleIvWithCheck_circleColor, Color.BLUE);
        int checkSize = (int) array.getDimension(R.styleable.CircleIvWithCheck_checkSize, 72);
        boolean isCheckVisible = array.getBoolean(R.styleable.CircleIvWithCheck_isCheckVisible
                ,false);
        array.recycle();

        CircleImageView circleImageView = new CircleImageView(context);
        ColorDrawable drawable = new ColorDrawable(color);
        circleImageView.setImageDrawable(drawable);
        RelativeLayout.LayoutParams circleIvParam = new LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        addView(circleImageView, circleIvParam);

        RelativeLayout.LayoutParams checkParam = new LayoutParams(checkSize, checkSize);
        checkParam.addRule(CENTER_IN_PARENT);
        checkIv = new ImageView(context);
        checkIv.setImageResource(R.mipmap.ic_done_white);

        if(!isCheckVisible){
            checkIv.setVisibility(INVISIBLE);
        }

        addView(checkIv, checkParam);
    }

    public void setCheckVisible(int isVisible) {
        checkIv.setVisibility(isVisible);
    }
    public int getColor(){
        return color;
    }
}
