package com.zd.miko.riji.CustomView;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zd.miko.riji.R;

/**
 * Created by Miko on 2017/7/14.
 */

public class CircleButton extends RelativeLayout {
    private int bgColor;
    private int index;
    public TextView tv;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getBgColor() {
        return bgColor;
    }

    public CircleButton(Context context) {
        this(context,null);
        LayoutInflater.from(context).inflate(R.layout.layout_circle_button, this, true);
        tv = (TextView) findViewById(R.id.id_tv);
        tv.setTextColor(Color.WHITE);
    }

    public CircleButton(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CircleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.layout_circle_button, this, true);
        tv = (TextView) findViewById(R.id.id_tv);
        tv.setTextColor(Color.WHITE);

        setBackground(getResources().getDrawable(R.drawable.bg_bt_oval));
        this.setClickable(true);
        this.setFocusable(true);
    }

    public void setBgColor(int c) {
        this.bgColor = c;

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(c);
        gd.setShape(GradientDrawable.OVAL);
        setBackground(gd);
    }
}
