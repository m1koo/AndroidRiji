package com.zd.miko.riji.MVP.Login;

/**
 * Created by Miko on 2017/3/21.
 */
public interface LoginListener {
    void onSuccess(String msg);
    void onFailure(String errorMsg);
}
