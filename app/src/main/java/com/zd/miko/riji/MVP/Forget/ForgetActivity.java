package com.zd.miko.riji.MVP.Forget;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.zd.miko.riji.MVP.VerifyPhone.VerifyPhoneFragment;
import com.zd.miko.riji.MVP.VerifyPhone.VerifyPhonePresenter;
import com.zd.miko.riji.R;

import java.util.ArrayList;
import java.util.List;

import cn.smssdk.SMSSDK;

public class ForgetActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private MyVPAdapter adapter;
    private VerifyPhonePresenter presenter;
    private Toolbar toolbar;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_act);
        initDate();
        initView();
        initToolbar();
        SMSSDK.initSDK(this, "1c556f0697018", "e0e973786c86f839270d7b385121e4d8");


    }

    private void initDate() {

        List<Fragment> fragmentList = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        titles.add("找回密码");
        titles.add("短信登录");

        VerifyPhoneFragment findFragment = (VerifyPhoneFragment) getSupportFragmentManager()
                .findFragmentById(R.id.id_contentFrame);
        if (findFragment == null) {
            findFragment = VerifyPhoneFragment.newInstance();
        }
        presenter = new VerifyPhonePresenter(this, findFragment);
        presenter.setFlag(VerifyPhonePresenter.FLAG_FIND);


        VerifyPhoneFragment loginFragment = (VerifyPhoneFragment) getSupportFragmentManager()
                .findFragmentById(R.id.id_contentFrame);
        if (loginFragment == null) {
            loginFragment = VerifyPhoneFragment.newInstance();

        }
        presenter = new VerifyPhonePresenter(this, loginFragment);
        presenter.setFlag(VerifyPhonePresenter.FLAG_LOGIN);
        fragmentList.add(findFragment);
        fragmentList.add(loginFragment);

        adapter = new MyVPAdapter(getSupportFragmentManager(), titles, fragmentList);
    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.id_vp_froget);
        viewPager.setAdapter(adapter);
        tabLayout = (TabLayout) findViewById(R.id.id_tablayout);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setupWithViewPager(viewPager);
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
