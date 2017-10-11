package com.zd.miko.riji.MVP.ModuleLogin;

/**
 * Created by Miko on 2017/3/21.
 */

public interface LoginService {
    void getUserId(String type,String account,LoginListener listener);
}
