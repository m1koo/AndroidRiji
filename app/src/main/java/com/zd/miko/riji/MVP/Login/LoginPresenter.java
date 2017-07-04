package com.zd.miko.riji.MVP.Login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.zd.miko.riji.DtoEntity.LoginExcution;
import com.zd.miko.riji.MVP.Main.MainActivity;

/**
 * Created by Miko on 2017/3/21.
 */

public class LoginPresenter implements LoginContract.Presenter {
    private LoginContract.View activity;
    private LoginService service;
    private Context context;

    public LoginPresenter(Context context, LoginContract.View activity) {
        this.activity = activity;
        this.context = context;
        service = new LoginServiceImpl(context);
        activity.setPresenter(this);
    }

    @Override
    public void doLogin() {
        String user = activity.getUserName();
        String password = activity.getPassword();
        login(user, password);
    }

    @Override
    public void login(String user, String password) {
        if (user.equals("") || password.equals("")) {
            activity.showSnackBar("用户名、密码不得为空");
        } else {
            activity.showProgress("登陆中，请稍后...");
            service.doLogin(user, password, new LoginListener() {
                @Override
                public void onSuccess(String msg) {
                    LoginExcution loginExcution = new Gson().fromJson(msg, LoginExcution.class);

                    activity.closeProgress();
                    activity.showSnackBar(loginExcution.getInfo());

                    if (loginExcution.isSuccess()) {
                        saveMd5(loginExcution.getMd5());
                        /**跳转界面*/
                        context.startActivity(new Intent(context, MainActivity.class));
                        //TODO 页面跳转 md5 储存
                    }
                }

                @Override
                public void onFailure(String errorMsg) {
                    activity.closeProgress();
                    activity.showSnackBar("未知网络错误");
                }
            });
        }
    }

    @Override
    public void saveMd5(String md5) {
        SharedPreferences sp = context
                .getSharedPreferences("md5", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sp.edit();
        editor.putString("md5", md5);
        editor.apply();
    }

    @Override
    public void start() {

    }
}
