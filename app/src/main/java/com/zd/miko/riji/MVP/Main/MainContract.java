package com.zd.miko.riji.MVP.Main;

import com.zd.miko.riji.Base.BasePresenter;
import com.zd.miko.riji.Base.BaseView;

/**
 * Created by Miko on 2017/3/29.
 */

public interface MainContract {
    interface View extends BaseView<Presenter> {
        void setTitle(String title);

        void jumpToPage(int index);

        void showSnackBar(String msg);

        void showProgress(String msg);

        void closeProgress();

        int getCurrentPageIndex();

    }

    interface Presenter extends BasePresenter {

    }
}
