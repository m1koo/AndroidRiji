package com.zd.miko.riji.MVP.Login;

import com.zd.miko.riji.Base.BasePresenter;
import com.zd.miko.riji.Base.BaseView;

/**
 * Created by Miko on 2017/3/27.
 */

public interface LoginContract {
    interface View extends BaseView<Presenter>{
        void showProgress(String msg);
        void closeProgress();
        void showSnackBar(String msg);
        String getUserName();
        String getPassword();
    }
    interface Presenter extends BasePresenter{
        void doLogin();
        void login(String userName,String password);
        void saveMd5(String md5);
    }
}
