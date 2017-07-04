package com.zd.miko.riji.MVP.Forget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.zd.miko.riji.DtoEntity.LoginExcution;
import com.zd.miko.riji.MVP.Login.LoginListener;
import com.zd.miko.riji.MVP.Main.MainActivity;
import com.zd.miko.riji.MVP.VerifyPhone.PhoneLoginService;
import com.zd.miko.riji.MVP.VerifyPhone.PhoneLoginServiceImp;

/**
 * Created by Miko on 2017/3/21.
 */

public class ForgetPresenter implements ForgetContract.Presenter {
    private ForgetContract.View view;
    private ForgetService service;
    private Context context;

    private PhoneLoginService phoneLoginService;
    public ForgetPresenter(Context context, ForgetContract.View activity) {
        this.view = activity;
        this.context = context;
        activity.setPresenter(this);
    }

    public void initService() {
        this.service = new ForgetServiceImpl(context);
    }

    @Override
    public void start() {

    }

    @Override
    public void doModifyPass() {
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
        modifyPass(view.getPhone(), view.getPassword(), new ForgetContract
                .modifyListener() {
            @Override
            public void onResult(boolean isSuccess) {
                view.showSnackBar("修改成功");
                phoneLoginService = new PhoneLoginServiceImp(context);
                phoneLoginService.doLogin(view.getPhone(), new LoginListener() {
                    @Override
                    public void onSuccess(String msg) {
                        LoginExcution excution = new Gson()
                                .fromJson(msg, LoginExcution.class);
                        view.closeProgress();
                        view.showSnackBar(excution.getInfo());
                        if (excution.isSuccess()) {
                            saveMd5(excution.getMd5());
                            /**跳转界面*/
                            context.startActivity(new Intent(context, MainActivity.class));
                            //TODO 页面跳转 md5 储存
                        }
                    }

                    @Override
                    public void onFailure(String errorMsg) {

                    }
                });
            }

            @Override
            public void onError(String error) {
                view.showSnackBar("ERROR");
            }
        });

    }

    @Override
    public void modifyPass(String phone, String password, ForgetContract
            .modifyListener listener) {
        service.doModifyPass(phone, password, listener);
    }
    public void saveMd5(String md5) {
        SharedPreferences sp = context
                .getSharedPreferences("md5", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sp.edit();
        editor.putString("md5", md5);
        editor.apply();
    }

}
