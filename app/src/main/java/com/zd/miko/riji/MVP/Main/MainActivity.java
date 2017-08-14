package com.zd.miko.riji.MVP.Main;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.amap.api.location.AMapLocationClientOption;
import com.zd.miko.riji.Adapter.MyVPAdapter;
import com.zd.miko.riji.EventBusBean.ActivityEvent;
import com.zd.miko.riji.MVP.Main.My.MyFragment;
import com.zd.miko.riji.MVP.World.WorldFragment;
import com.zd.miko.riji.MyApp;
import com.zd.miko.riji.R;
import com.zd.miko.riji.View.Fragment.MsgFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;
import me.majiajie.pagerbottomtabstrip.NavigationController;
import me.majiajie.pagerbottomtabstrip.PageBottomTabLayout;

public class MainActivity extends AppCompatActivity {

    private CoordinatorLayout snackBarContainer;
    private ViewPager mViewPager;
    private MyVPAdapter mAdapter;
    private PageBottomTabLayout bottomTab;

    public AMapLocationClientOption mLocationOption = null;


    private Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyApp.push(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            Fade fade = new Fade();
            fade.setDuration(500);
            getWindow().setExitTransition(fade);
        }
        PermissionGen.with(this)
                .addRequestCode(100)
                .permissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)
                .request();

        /**设置全屏*/
        setContentView(R.layout.main_act);

        initView();
        initEvent();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this,
                requestCode, permissions, grantResults);
    }

    @PermissionSuccess(requestCode = 100)
    public void doSomething() {
    }

    @PermissionFail(requestCode = 100)
    public void doFailSomething() {
        Toast.makeText(this, "获取权限失败，无法添加图片", Toast.LENGTH_SHORT).show();
    }



    public void initView() {
        List<Fragment> fragmentList = new ArrayList<>();
        WorldFragment fragment = new WorldFragment();
        fragmentList.add(new MyFragment());

        fragmentList.add(fragment);
        fragmentList.add(new MsgFragment());
        mAdapter = new MyVPAdapter(getSupportFragmentManager(), fragmentList);
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
        mViewPager.setAdapter(mAdapter);
        bottomTab = (PageBottomTabLayout) findViewById(R.id.bottom_tab_layout);
        snackBarContainer = (CoordinatorLayout) findViewById(R.id.id_snackContainer);
        NavigationController navigationController = bottomTab.material()
                .addItem(R.mipmap.ic_person_outline_black, "我的")
                .addItem(R.mipmap.ic_explore_black, "发现")
                .addItem(R.mipmap.ic_chat_black, "消息")
                .build();
        navigationController.setupWithViewPager(mViewPager);
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onSnackEvent(ActivityEvent activityEvent) {
        if (activityEvent.event == ActivityEvent.SHOW_SNACKBAR_VIEW) {
            snackbar = Snackbar.make(snackBarContainer,
                    "", Snackbar.LENGTH_INDEFINITE);

            SnackbarAddView(snackbar, activityEvent.view, 0);
            snackbar.show();
        } else if (activityEvent.event == ActivityEvent.SHOW_SNACKBAR_MSG) {
            snackbar = Snackbar.make(snackBarContainer,
                    activityEvent.message, Snackbar.LENGTH_SHORT);
            snackbar.show();
        } else if (activityEvent.event == ActivityEvent.HIDE_BOTTOM) {
            bottomTab.setVisibility(View.GONE);
        } else if (activityEvent.event == ActivityEvent.SHOW_BOTTOM) {
            bottomTab.setVisibility(View.VISIBLE);
        }
    }

    public void SnackbarAddView(Snackbar snackbar, View addView, int index) {
        View snackbarview = snackbar.getView();//获取snackbar的View(其实就是SnackbarLayout)

        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbarview;
        //将获取的View转换成SnackbarLayout

        FrameLayout.LayoutParams p = new FrameLayout.LayoutParams
                (FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT);//设置新建布局参数

        p.gravity = Gravity.CENTER_VERTICAL;//设置新建布局在Snackbar内垂直居中显示
        snackbarLayout.addView(addView, index, p);//将新建布局添加进snackbarLayout相应位置
    }

    public void initEvent() {
    }

}
