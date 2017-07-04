package com.zd.miko.riji.CustomView;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;

public class NoteContentEditText extends EditText {

    public NoteContentEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        // TODO Auto-generated method stub
        super.onSelectionChanged(selStart, selEnd);

        int[] locationInWindow = new int[2];
        getLocationInWindow(locationInWindow);

        Log.v("TAG","getLocationInWindow() - "+ locationInWindow[0] + " : " + locationInWindow[1]);

    }
}