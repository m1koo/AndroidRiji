package com.zd.miko.riji.MVP.Login;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.zd.miko.riji.R;
import com.zd.miko.riji.Utils.ActivityUtils;

import cn.smssdk.SMSSDK;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private LoginContract.Presenter presenter;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_act);
        SMSSDK.initSDK(this, "1c556f0697018", "e0e973786c86f839270d7b385121e4d8");

        initToolbar();

        LoginFragment loginFragment = (LoginFragment) getSupportFragmentManager()
                .findFragmentById(R.id.id_contentFrame);
        if (loginFragment == null) {
            loginFragment = LoginFragment.newInstance();
            ActivityUtils.addFragmentToRoot(getSupportFragmentManager(),
                    loginFragment, R.id.id_contentFrame, null);
        }

        presenter = new LoginPresenter(this, loginFragment);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        setSupportActionBar(toolbar);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.destroySSM();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}

