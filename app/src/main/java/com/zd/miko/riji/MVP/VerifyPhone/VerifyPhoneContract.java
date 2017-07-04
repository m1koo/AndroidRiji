package com.zd.miko.riji.MVP.VerifyPhone;

import android.os.Looper;

import com.zd.miko.riji.Base.BasePresenter;
import com.zd.miko.riji.Base.BaseView;

/**
 * Created by Miko on 2017/3/27.
 */

public interface VerifyPhoneContract {
    interface View extends BaseView<Presenter> {
        void showProgress(String msg);

        void closeProgress();

        void showSnackBar(String msg);

        void updateHintButton(String text, boolean isClickable);

        String getPhone();

        String getPassword();

        Looper getLooper();

        boolean isActive();

        void setTitle(String title);

        void setEdPhoneHint(String hint);
    }

    interface Presenter extends BasePresenter {
        void getVerifyCode();

        void doVerify();

        void destroySSM();

        void checkPhoneRegisted(checkRegistedListener listener);

        /**免密登录时使用*/
        void doLoginByPhone();
    }

    interface checkRegistedListener {
        void onResult(boolean isRegisted);

        void onError(String error);
    }
}
