package com.zd.miko.riji.Utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.Layout;
import android.text.Selection;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.zd.miko.riji.CustomView.NoteContentEditText;
import com.zd.miko.riji.MyApp;

import java.util.ArrayList;

/**
 * Created by Miko on 2017/2/5.
 */

public class Utils {

    public static int getStatusBarHeight() {
        int result = 0;
        int resourceId = MyApp.getContextObject().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result =  MyApp.getContextObject().getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static void hintKeyboard(Context context) {
        InputMethodManager imm = (InputMethodManager)context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm.isActive()&&((Activity)context).getCurrentFocus()!=null){
            if (((Activity)context).getCurrentFocus().getWindowToken()!=null) {
                imm.hideSoftInputFromWindow(((Activity)context).getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
    public static ArrayList<View> getViewsByTag(ViewGroup root, String tag){
        ArrayList<View> views = new ArrayList<View>();
        final int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = root.getChildAt(i);
            if (child instanceof ViewGroup) {
                views.addAll(getViewsByTag((ViewGroup) child, tag));
            }

            final Object tagObj = child.getTag();
            if (tagObj != null && tagObj.equals(tag)) {
                views.add(child);
            }

        }
        return views;
    }
    public static int[] colorToRgb(int color){
        int rgb[] = new int[3];
        rgb[0] = (color & 0xff0000) >> 16;
        rgb[1] = (color & 0x00ff00) >> 8;
        rgb[2] = (color & 0x0000ff);
        return rgb;
    }

    public static boolean isLightColor(int color){
        int rgb[] = colorToRgb(color);
        double grayLevel = rgb[0]* 0.299 + rgb[1] * 0.587 + rgb[2] * 0.114;
        Log.i("xyz", String.valueOf(grayLevel));
        return grayLevel >= 110;
    }

    public static int dpToPx(int dp) {
        Context context = MyApp.getContextObject();

        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp
                , context.getResources().getDisplayMetrics());
    }

    public static int getCurrentCursorLine(NoteContentEditText editText) {
        int selectionStart = Selection.getSelectionStart(editText.getText());
        Layout layout = editText.getLayout();

        if (selectionStart != -1) {
            return layout.getLineForOffset(selectionStart) + 1;
        }
        return -1;
    }

    public static Bitmap getBitmap(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();  //启用DrawingCache并创建位图
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache()); //创建一个DrawingCache的拷贝，因为DrawingCache得到的位图在禁用后会被回收
        view.setDrawingCacheEnabled(false);  //禁用DrawingCahce否则会影响性能
        return bitmap;
    }

}
