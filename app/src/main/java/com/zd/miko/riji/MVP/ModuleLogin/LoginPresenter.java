package com.zd.miko.riji.MVP.ModuleLogin;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.zd.miko.riji.Bean.LoginExcution;
import com.zd.miko.riji.Bean.VerifyError;
import com.zd.miko.riji.Enums.LoginStateEnum;

import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by Miko on 2017/3/21.
 */

public class LoginPresenter implements LoginContract.Presenter {
    private LoginContract.View activity;
    private LoginService service;
    private Context context;
    private EventHandler eh;

    public LoginPresenter(Context context, LoginContract.View activity) {
        this.activity = activity;
        this.context = context;
        activity.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void getModifyCode() {
        String phone = activity.getPhone().replace(" ", "");
        if (phone.length() != 11) {
            activity.showSnackBar("手机号格式不正确");
            return;
        }
        activity.startCountDown();
        registerCallback();
        sendCode();
    }

    @Override
    public void doVerify() {

        if (eh == null) {
            activity.showSnackBar("验证码错误");
            return;
        }
        if (!"".equals(activity.getPhone())) {
            SMSSDK.submitVerificationCode
                    ("86", activity.getPhone(),
                            activity.getModifyCode());
            activity.showProgress("验证中，请稍后...");
        } else {
            activity.showSnackBar("验证码不得为空");
        }
    }

    @Override
    public void destroySSM() {
        /**避免懒加载空指针*/
        if (eh != null) {
            Log.i("xyz", "ssm已销毁");
            eh = null;
            SMSSDK.unregisterEventHandler(eh);
        }
    }

    /**
     * 用来获取用户id，通用于手机号，qq微信
     */
    @Override
    public void getUserId(String account, String type) {

        LoginService service = new LoginServiceImpl(context);
        service.getUserId(type, account, new LoginListener() {
            @Override
            public void onSuccess(String msg) {
                if (msg == null) {
                    return;
                }
                LoginExcution e = new Gson().fromJson(msg, LoginExcution.class);
                if (e == null) {
                    return;
                }

                String id = e.getId();

                SharedPreferences sp = context.getSharedPreferences("account",
                        Context.MODE_PRIVATE);

                sp.edit().putString("id", id).apply();

                if (e.getState() == LoginStateEnum.ID_HAD_CREATE.getState()) {
                    Log.i("xyz", "old");
                } else if (e.getState() == LoginStateEnum.CREATE_NEW_ID.getState()) {
                    Log.i("xyz", "new");
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                activity.showSnackBar(errorMsg);
                Log.e("xyz", errorMsg);
            }
        });
    }

    public void sendCode() {
        SMSSDK.getVerificationCode("86", activity.getPhone());
    }


    public void registerCallback() {
        if (null == eh) {
            Log.i("xyz", "ssm重建");
            //懒加载
            initEv();
            SMSSDK.registerEventHandler(eh); //注册短信回调
        }
    }

    void initEv() {
        eh = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {

                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        activity.closeProgress();

                        HashMap<String, Object> hashMap = (HashMap<String, Object>) data;
                        if (hashMap.get("phone").toString().
                                equals(activity.getPhone())) {
                            activity.showSnackBar("验证成功");
                            onVerifySuccess();
                        }
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        activity.showSnackBar("验证码已发送");
                    }
                } else {
                    activity.closeProgress();
                    ((Throwable) data).printStackTrace();
                    VerifyError error = new Gson().fromJson(((Throwable) data).getMessage()
                            , VerifyError.class);
                    activity.showSnackBar(error.getDetail());
                }
            }
        };
    }

    /**
     * 验证成功
     */
    private void onVerifySuccess() {
        getUserId(activity.getPhone(), "phone");
    }
}