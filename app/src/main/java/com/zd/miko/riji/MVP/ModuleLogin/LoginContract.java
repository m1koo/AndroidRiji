package com.zd.miko.riji.MVP.ModuleLogin;

import com.zd.miko.riji.BaseMVP.BasePresenter;
import com.zd.miko.riji.BaseMVP.BaseView;

/**
 * Created by Miko on 2017/3/27.
 */

public interface LoginContract {
    interface View extends BaseView<Presenter>{
        void showProgress(String msg);
        void closeProgress();
        void showSnackBar(String msg);
        String getPhone();
        String getModifyCode();
        void startCountDown();
        void closeKeyboard();
    }
    interface Presenter extends BasePresenter{
        void getModifyCode();
        void doVerify();
        void destroySSM();
        void getUserId(String account, String type);
    }
}
