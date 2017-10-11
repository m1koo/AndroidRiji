package com.zd.miko.riji.MVP.PageMyDiary;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zd.miko.riji.R;
import com.zd.miko.riji.Utils.Utils;
import com.zd.miko.riji.CustomView.CircleButton;

public class MyDiaryBehavior extends CoordinatorLayout.Behavior<View> {

    public MyDiaryBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    private static final int INITFLAG = 1000000;

    private int oriTvX = INITFLAG;
    private int oriTvY = INITFLAG;
    int tvMoveX;
    int tvMoveY;
    float oriTvSize;

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        //如果dependency是TempView的实例，说明它就是我们所需要的Dependency
        return dependency instanceof AppBarLayout;
    }

    //每次dependency位置发生变化，都会执行onDependentViewChanged方法
    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View btn, View dependency) {

        //根据dependency的位置，设置Button的位置

        CircleButton ovS1 = (CircleButton) dependency.findViewById(R.id.id_ov_s1);

        CircleButton ovS2 = (CircleButton) dependency.findViewById(R.id.id_ov_s2);

        CircleButton ovS3 = (CircleButton) dependency.findViewById(R.id.id_ov_s3);

        CircleButton ovCurrent = (CircleButton) dependency.findViewById(R.id.id_ov_current);

        CircleButton ovCalender = (CircleButton) dependency.findViewById(R.id.id_ov_calender);

        Toolbar toolbar = (Toolbar) dependency.findViewById(R.id.toolbar);

        TextView tvCurrent = (TextView) dependency.findViewById(R.id.id_tv_currmonth);

        TextView tvMeasure = (TextView) parent.findViewById(R.id.id_tv_measure);
        int height = dependency.getHeight();
        int width = dependency.getWidth();

        int maxYOffset = height - toolbar.getHeight();

        int currYOffset = height - dependency.getBottom();

        double currRatio = (double) currYOffset / (double) maxYOffset;

        int widthOfOvCurr = width - ovCurrent.getRight();

        /**x轴需要扩展的总倍数*/
        double maxScaleX = (double) width / (double) widthOfOvCurr;

        ovCurrent.setScaleX((float) ((currRatio * maxScaleX * 2) + 1));
        ovCurrent.setScaleY((float) ((currRatio * maxScaleX * 0.8) + 1));

        ovS1.setAlpha((float) (1 - currRatio * 4));
        ovS2.setAlpha((float) (1 - currRatio * 3.5));
        ovS3.setAlpha((float) (1 - currRatio * 4));


        ovCalender.setScaleX((float) (1 - currRatio));
        ovCalender.setScaleY((float) (1 - currRatio));


        /**currentMonth文字动画*/
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) tvCurrent.
                getLayoutParams();
        /**终态的Text的size*/
        int endTextSpSize = 18;

        if (oriTvX == INITFLAG) {
            /**获取最终状态的Text的高度*/
            int endHeight = tvMeasure.getHeight();

            /**初始化原始状态的margin*/
            oriTvX = layoutParams.leftMargin;
            oriTvY = layoutParams.topMargin;
            /**设定向右移动的px*/
            tvMoveX = 100;
            /**计算最终状态的marginTop*/
            int endTextMarginTop = toolbar.getPaddingTop() + (toolbar.getHeight()
                    - toolbar.getPaddingTop()) / 2 - endHeight/2;
            /**计算整个过程中tv需要上移的高度*/
            tvMoveY = oriTvY - endTextMarginTop;
            /**原始的tv字体大小*/
            oriTvSize = tvCurrent.getTextSize();
        }

        layoutParams.leftMargin = (int) (oriTvX + tvMoveX * currRatio);
        /**移动的同时也在滚动*/
        layoutParams.topMargin = (int) (oriTvY - tvMoveY * currRatio) + currYOffset;

        /**计算字体大小的偏移值*/
        int sizeOffset = (int) (oriTvSize - Utils.spToPx(endTextSpSize));

        /**动态设置大小*/
        tvCurrent.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                (float) (oriTvSize - sizeOffset * currRatio));

        /**刷新界面*/
        tvCurrent.requestLayout();
        return true;
    }


}