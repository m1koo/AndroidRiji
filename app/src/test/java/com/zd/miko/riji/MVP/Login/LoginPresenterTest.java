package com.zd.miko.riji.MVP.Login;

import android.test.ActivityTestCase;
import android.util.Log;

import com.zd.miko.riji.MVP.Main.MainActivity;

import org.junit.Test;

/**
 * Created by Miko on 2017/7/5.
 */
public class LoginPresenterTest extends ActivityTestCase {

    @Test
    public void name() throws Exception {
        LoginService service = new LoginServiceImpl(new MainActivity());

        service.getUserId("phone", "15891396443", new LoginListener() {
            @Override
            public void onSuccess(String msg) {
                Log.i("xyz",msg);
            }

            @Override
            public void onFailure(String errorMsg) {
                Log.i("xyz",errorMsg);
            }
        });
    }
}