package com;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.zd.miko.riji.MVP.ModuleLogin.LoginListener;
import com.zd.miko.riji.MVP.ModuleLogin.LoginService;
import com.zd.miko.riji.MVP.ModuleLogin.LoginServiceImpl;
import com.zd.miko.riji.R;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        String s = appContext.getString(R.string.app_name);

        LoginService service = new LoginServiceImpl(appContext);

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
