package com.zd.miko.riji;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;


/**
 * Created by Miko on 2017/2/3.
 */

public class MyBehavior extends CoordinatorLayout.Behavior {
    public MyBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return true;
    }


    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
//        if (dependency.getId() != R.id.id_toolbar1) {
//            return true;
//        }
//        int[] locations = new int[2];
//        dependency.getLocationInWindow(locations);
//        /**划出屏幕，隐藏显示按钮*/
//        if (locations[1] < 0) {
//                parent.findViewById(R.id.id_fbt_weather).setVisibility(View.INVISIBLE);
//                parent.findViewById(R.id.id_fbt_location).setVisibility(View.INVISIBLE);
//                parent.findViewById(R.id.id_fbt_mood).setVisibility(View.INVISIBLE);
//                parent.findViewById(id_rl_toolbar3).setVisibility(View.VISIBLE);
//
//        } else {
//                parent.findViewById(R.id.id_fbt_weather).setVisibility(View.VISIBLE);
//                parent.findViewById(R.id.id_fbt_location).setVisibility(View.VISIBLE);
//                parent.findViewById(R.id.id_fbt_mood).setVisibility(View.VISIBLE);
//                parent.findViewById(id_rl_toolbar3).setVisibility(View.INVISIBLE);
//        }
        return true;
    }
}
