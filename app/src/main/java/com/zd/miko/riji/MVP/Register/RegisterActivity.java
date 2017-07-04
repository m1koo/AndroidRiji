package com.zd.miko.riji.MVP.Register;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.zd.miko.riji.MVP.VerifyPhone.VerifyPhoneFragment;
import com.zd.miko.riji.MVP.VerifyPhone.VerifyPhonePresenter;
import com.zd.miko.riji.R;
import com.zd.miko.riji.Utils.ActivityUtils;

import cn.smssdk.SMSSDK;

public class RegisterActivity extends AppCompatActivity {

    private VerifyPhonePresenter presenter;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_act);

        initToolbar();
        SMSSDK.initSDK(this, "1c556f0697018", "e0e973786c86f839270d7b385121e4d8");

        VerifyPhoneFragment registerFragment = (VerifyPhoneFragment) getSupportFragmentManager()
                .findFragmentById(R.id.id_contentFrame);
        if (registerFragment == null) {
            registerFragment = VerifyPhoneFragment.newInstance();
            ActivityUtils.addFragmentToRoot(getSupportFragmentManager(),
                    registerFragment, R.id.id_contentFrame, "register_phone");
        }
        presenter = new VerifyPhonePresenter(this, registerFragment);
        presenter.setFlag(VerifyPhonePresenter.FLAG_REGISTER);
    }


    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        presenter.destroySSM();
        super.onDestroy();
    }
}
