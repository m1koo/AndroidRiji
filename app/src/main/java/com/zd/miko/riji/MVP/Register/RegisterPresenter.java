package com.zd.miko.riji.MVP.Register;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.zd.miko.riji.MVP.Main.MainActivity;

/**
 * Created by Miko on 2017/3/21.
 */

public class RegisterPresenter implements RegisterContract.Presenter {
    private RegisterContract.View view;
    private RegisterService service;
    private Context context;

    public RegisterPresenter(Context context, RegisterContract.View activity) {
        this.view = activity;
        this.context = context;
        activity.setPresenter(this);
    }

    public void initService() {
        this.service = new RegisterServiceImpl(context);
    }

    @Override
    public void start() {

    }

    @Override
    public void doRegister() {
        String pass = view.getPassword();
        String repeatPass = view.getRepeatPass();
        if (!pass.equals(repeatPass)) {
            view.showSnackBar("两次输入的密码不一致");
            return;
        }
        if (pass.length() < 6 || pass.length() > 16) {
            view.showSnackBar("你所输入的密码长度不符合要求");
            return;
        }
        if (null == service) {
            initService();
        }
        register(view.getPhone(), view.getNickName(), pass, new RegisterContract.registerListener() {
            @Override
            public void onSuccess(String md5) {
                view.showSnackBar("注册成功");
                saveMd5(md5);
                /**跳转界面*/
                context.startActivity(new Intent(context, MainActivity.class));
            }
        });
    }

    @Override
    public void register(String phone, String userName, String password, RegisterContract.registerListener listener) {
        service.register(phone, userName, password, listener);
    }

    public void saveMd5(String md5) {
        SharedPreferences sp = context
                .getSharedPreferences("md5", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sp.edit();
        editor.putString("md5", md5);
        editor.apply();
    }
}
