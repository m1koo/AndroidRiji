package com.zd.miko.riji.MVP.Main;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.zd.miko.riji.Adapter.MyVPAdapter;
import com.zd.miko.riji.MVP.World.WorldFragment;
import com.zd.miko.riji.R;
import com.zd.miko.riji.View.Fragment.MsgFragment;

import java.util.ArrayList;
import java.util.List;

import me.majiajie.pagerbottomtabstrip.PageBottomTabLayout;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private MyVPAdapter mAdapter;
    private PageBottomTabLayout bottomTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.main_act);

        initView();
        initEvent();
    }


    public void initView() {
        List<Fragment> fragmentList = new ArrayList<>();
        WorldFragment fragment = new WorldFragment();
        fragmentList.add(new MFragment());

        fragmentList.add(fragment);
        fragmentList.add(new MsgFragment());
        mAdapter = new MyVPAdapter(getSupportFragmentManager(), fragmentList);
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
        mViewPager.setAdapter(mAdapter);
        bottomTab = (PageBottomTabLayout) findViewById(R.id.bottom_tab_layout);
//        NavigationController navigationController = bottomTab.material()
//                .addItem(R.mipmap.ic_person_outline_black,"我的")
//                .addItem(R.mipmap.ic_explore_black,"发现")
//                .addItem(R.mipmap.ic_chat_black,"消息")
//                .build();
//        navigationController.setupWithViewPager(mViewPager);
    }

    public void initEvent() {
    }

}
