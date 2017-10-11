package com.zd.miko.riji.MVP.ModuleLogin;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ServiceTestCase;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Method;

/**
 * Created by Miko on 2017/7/5.
 */
@RunWith(AndroidJUnit4.class)
public class LoginPresenterTest{

    private Context getTestContext() {
        try {
            Method getTestContext = ServiceTestCase.class.getMethod("getTestContext");
            return (Context) getTestContext.invoke(this);
        } catch (final Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Test
    public void name() throws Exception {
        LoginService service = new LoginServiceImpl(InstrumentationRegistry.getTargetContext());

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