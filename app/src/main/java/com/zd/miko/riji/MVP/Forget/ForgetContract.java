package com.zd.miko.riji.MVP.Forget;

import android.os.Looper;

import com.zd.miko.riji.Base.BasePresenter;
import com.zd.miko.riji.Base.BaseView;

/**
 * Created by Miko on 2017/3/27.
 */

public interface ForgetContract {
    interface View extends BaseView<Presenter> {
        void showProgress(String msg);

        void closeProgress();

        void showSnackBar(String msg);

        String getPhone();

        String getRepeatPass();

        String getPassword();

        Looper getLooper();
    }

    interface Presenter extends BasePresenter {
        void doModifyPass();

        /**
         * 方便测试
         */
        void modifyPass(String phone, String password, modifyListener listener);
    }

    interface modifyListener {
        void onResult(boolean isSuccess);

        void onError(String error);
    }
}
