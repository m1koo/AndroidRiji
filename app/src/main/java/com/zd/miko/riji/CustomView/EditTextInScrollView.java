package com.zd.miko.riji.CustomView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

public class EditTextInScrollView extends EditText
{
    public EditTextInScrollView(Context context)
    {
        super(context);
        // TODO Auto-generated constructor stub
    }
    public EditTextInScrollView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }
    public EditTextInScrollView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        //让父类不不拦截自己的触摸事件
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE)
            this.getParent().requestDisallowInterceptTouchEvent(true);
        return super.onTouchEvent(event);
    }
}