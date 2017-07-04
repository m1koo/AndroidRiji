package com.zd.miko.riji.MVP.Forget;

/**
 * Created by Miko on 2017/3/21.
 */

public interface ForgetService {
    void doModifyPass(String phone, String password, ForgetContract.modifyListener listener);
}