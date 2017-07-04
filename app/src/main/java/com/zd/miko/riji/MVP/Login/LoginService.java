package com.zd.miko.riji.MVP.Login;

/**
 * Created by Miko on 2017/3/21.
 */

public interface LoginService {
    void doLogin(String userName,String password,LoginListener listener);
}
