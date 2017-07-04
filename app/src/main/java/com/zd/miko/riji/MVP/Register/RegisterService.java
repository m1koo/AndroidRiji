package com.zd.miko.riji.MVP.Register;

import com.zd.miko.riji.MVP.VerifyPhone.VerifyPhoneContract;

/**
 * Created by Miko on 2017/3/27.
 */

public interface RegisterService {
    void checkPhone(String phone, VerifyPhoneContract.checkRegistedListener listener);

    void register(String phone, String userName, String password,
                  RegisterContract.registerListener listener);
}
