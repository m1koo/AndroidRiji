package com.zd.miko.riji.MVP.PageMyDiary;

import com.zd.miko.riji.BaseMVP.BasePresenter;
import com.zd.miko.riji.BaseMVP.BaseView;

/**
 * Created by Miko on 2017/7/6.
 */

public class MyDiaryContract {
    interface View extends BaseView<Presenter>{

        /**初始化圆圈的颜色位置，大小，文字*/
        void initOval();

        /**开始圆圈的入场动画*/
        void startEnterAnim();

        /**开始改变月份的动画，即两个圆圈之间改变位置*/
        void startChangeAnim(int changedIndex);

        /**刷新recyclerView中的日记列表*/
        void refreshList();

        /**打开日历的Dialog*/
        void openCalendarDialog();


    }
    interface Presenter extends BasePresenter{

    }

}
