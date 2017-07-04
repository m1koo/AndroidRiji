package com.zd.miko.riji.MVP.Register;

import android.os.Looper;

import com.zd.miko.riji.Base.BasePresenter;
import com.zd.miko.riji.Base.BaseView;

/**
 * Created by Miko on 2017/3/27.
 */

public interface RegisterContract {
    interface View extends BaseView<Presenter> {
        void showProgress(String msg);

        void closeProgress();

        void showSnackBar(String msg);

        String getPhone();

        String getNickName();

        String getRepeatPass();

        String getPassword();

        Looper getLooper();
    }

    interface Presenter extends BasePresenter {
        void doRegister();
        /**方便测试*/
        void register(String phone, String userName, String password, registerListener listener);
    }

    interface registerListener{
        /**注册成功并登录返回md5*/
        void onSuccess(String md5);
    }

    interface checkRegistedListener {
        void onResult(boolean isRegisted);

        void onError(String error);
    }
}
