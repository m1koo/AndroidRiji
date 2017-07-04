package com.zd.miko.riji.MVP.VerifyPhone;

import com.zd.miko.riji.MVP.Login.LoginListener;

/**
 * Created by Miko on 2017/4/10.
 */

public interface PhoneLoginService  {
    void doLogin(String phone,LoginListener listener);
}
