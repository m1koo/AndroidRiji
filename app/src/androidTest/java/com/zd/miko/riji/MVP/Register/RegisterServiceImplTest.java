package com.zd.miko.riji.MVP.Register;

import android.util.Log;

import com.zd.miko.riji.MVP.VerifyPhone.VerifyPhoneContract;

import org.junit.Test;

/**
 * Created by Miko on 2017/3/27.
 */
public class RegisterServiceImplTest {
    @Test
    public void checkPhone() throws Exception {
        RegisterServiceImpl registerService = new RegisterServiceImpl(null);
        registerService.checkPhone("15891396443", new VerifyPhoneContract.checkRegistedListener() {
            @Override
            public void onResult(boolean isRegisted) {
                Log.i("xyz","listener"+isRegisted);
            }

            @Override
            public void onError(String error) {
                Log.i("xyz",error);
            }
        });
    }

}