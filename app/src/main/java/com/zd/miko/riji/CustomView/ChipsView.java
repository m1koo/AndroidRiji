package com.zd.miko.riji.CustomView;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Miko on 2017/2/10.
 */

public class ChipsView extends View {
    private int rectBgColor;
    private int circleBgColor;
    private String content;

    private Paint rectPaint;
    private Paint circlePaint;
    public ChipsView(Context context) {
        super(context,null);
    }

    public ChipsView(Context context, AttributeSet attrs) {
        super(context, attrs,0);
    }

    public ChipsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


    }


}
