package com.zd.miko.riji.MVP.VerifyPhone;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;
import com.zd.miko.riji.DtoEntity.LoginExcution;
import com.zd.miko.riji.DtoEntity.VerifyError;
import com.zd.miko.riji.MVP.Forget.ForgetContract;
import com.zd.miko.riji.MVP.Forget.FindFragment;
import com.zd.miko.riji.MVP.Forget.ForgetPresenter;
import com.zd.miko.riji.MVP.Login.LoginListener;
import com.zd.miko.riji.MVP.Main.MainActivity;
import com.zd.miko.riji.MVP.Register.RegisterContract;
import com.zd.miko.riji.MVP.Register.RegisterFragment;
import com.zd.miko.riji.MVP.Register.RegisterPresenter;
import com.zd.miko.riji.MVP.Register.RegisterService;
import com.zd.miko.riji.MVP.Register.RegisterServiceImpl;
import com.zd.miko.riji.R;
import com.zd.miko.riji.Utils.ActivityUtils;

import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by Miko on 2017/3/21.
 */

public class VerifyPhonePresenter implements VerifyPhoneContract.Presenter {
    public static final int FLAG_REGISTER = 0;
    public static final int FLAG_FIND = 1;
    public static final int FLAG_LOGIN = 2;

    private int flag = -1;

    public void setFlag(int flag) {
        this.flag = flag;
    }

    private VerifyPhoneContract.View activity;
    private RegisterService service;
    private Context context;

    private static final int TIMER = 20;
    private Handler handler;
    private EventHandler eh;

    private PhoneLoginService loginService;

    public VerifyPhonePresenter(Context context, VerifyPhoneContract.View activity) {
        this.activity = activity;
        this.context = context;
        this.service = new RegisterServiceImpl(context);
        activity.setPresenter(this);
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
                            if (flag == FLAG_REGISTER) {
                                startUserRegisterAc();
                            } else if (flag == FLAG_FIND) {
                                startForgetPassAc();
                            } else if (flag == FLAG_LOGIN) {
                                activity.showSnackBar("登录成功");
                                //TODO 登陆成功
                            }
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
                    /**测试的时候使用*/
                    if (flag == FLAG_REGISTER) {
                        startUserRegisterAc();
                    } else if (flag == FLAG_FIND) {
                        startForgetPassAc();
                    } else if (flag == FLAG_LOGIN) {
                        activity.showSnackBar("登录成功");
                        loginService = new PhoneLoginServiceImp(context);
                        loginService.doLogin(activity.getPhone(), new LoginListener() {
                            @Override
                            public void onSuccess(String msg) {
                                LoginExcution excution = new Gson()
                                        .fromJson(msg, LoginExcution.class);
                                activity.closeProgress();
                                activity.showSnackBar(excution.getInfo());
                                if (excution.isSuccess()) {
                                    saveMd5(excution.getMd5());
                                    /**跳转界面*/
                                    context.startActivity(new Intent(context, MainActivity.class));
                                    //TODO 页面跳转 md5 储存
                                }
                            }

                            @Override
                            public void onFailure(String errorMsg) {
                                activity.closeProgress();
                            }
                        });

                        //TODO 登陆成功
                    }
                }
            }
        };
    }

    private void startUserRegisterAc() {

        RegisterFragment fragment = (RegisterFragment)
                ((AppCompatActivity) context)
                        .getSupportFragmentManager()
                        .findFragmentByTag("register_user");
        if (fragment == null) {
            fragment = RegisterFragment.newInstance();
            Bundle bundle = new Bundle();
            bundle.putString("phone", activity.getPhone());
            fragment.setArguments(bundle);
            ActivityUtils.addFragmentToActivity(
                    ((AppCompatActivity) context).getSupportFragmentManager()
                    , fragment, R.id.id_contentFrame, "register_user");
        }
        RegisterContract.Presenter presenter = new
                RegisterPresenter(context, fragment);
    }

    private void startForgetPassAc() {
        FindFragment fragment = (FindFragment)
                ((AppCompatActivity) context)
                        .getSupportFragmentManager()
                        .findFragmentByTag("forget");
        if (fragment == null) {
            fragment = FindFragment.newInstance();
            Bundle bundle = new Bundle();
            bundle.putString("phone", activity.getPhone());
            fragment.setArguments(bundle);
            ActivityUtils.addFragmentToRoot(
                    ((AppCompatActivity) context).getSupportFragmentManager()
                    , fragment, R.id.id_contentFrame, "forget");
        }
        ForgetContract.Presenter presenter = new
                ForgetPresenter(context, fragment);
    }

    void initHandler() {
        handler = new Handler(activity.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (activity.isActive()) {
                    activity.updateHintButton("重新获取(" + msg.getData().getInt("t") + "秒)", false);
                    if (msg.getData().getInt("t") == 0) {
                        activity.updateHintButton("获取验证码", true);
                    }
                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    public void start() {
        if (flag == FLAG_REGISTER) {
            activity.setTitle("注册");
        } else if (flag == FLAG_FIND) {
            activity.setTitle("找回密码");
        } else if (flag == FLAG_LOGIN) {
            activity.setTitle("短信登录");
        }
    }

    private void startCountDown() {
        if (null == handler) {
            initHandler();
        }
        new Thread(() -> {
            int i = TIMER;
            while (i >= 0) {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putInt("t", i);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                    i--;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void registerCallback() {
        if (null == eh) {
            Log.i("xyz", "ssm重建");
            //懒加载
            initEv();
            SMSSDK.registerEventHandler(eh); //注册短信回调
        }
    }

    public void sendCode() {
        SMSSDK.getVerificationCode("86", activity.getPhone());
    }

    @Override
    public void getVerifyCode() {
        String phone = activity.getPhone().replace(" ", "");
        if (phone.length() != 11) {
            activity.showSnackBar("手机号格式不正确");
            return;
        }
        activity.showProgress("正在查询手机号是否被注册,请稍后...");

        checkPhoneRegisted(
                new VerifyPhoneContract
                        .checkRegistedListener() {
                    @Override
                    public void onResult(boolean isRegisted) {

                        activity.closeProgress();
                        /**测试时暂时注释掉*/
                        if (isRegisted) {
                            if (flag == FLAG_REGISTER) {
                                activity.showSnackBar("当前手机号已被注册,请尝试其他号码");
                            } else if (flag == FLAG_FIND || flag == FLAG_LOGIN) {
                                startCountDown();
                                registerCallback();
                                sendCode();
                            }
                        } else {
                            /**手机号尚未注册，发送短信验证码*/
                            if (flag == FLAG_REGISTER) {
                                startCountDown();
                                registerCallback();
                                sendCode();
                            }
                            /**未注册*/
                            if (flag == FLAG_FIND || flag == FLAG_LOGIN) {
                                activity.showSnackBar("当前手机号未曾被注册");
                            }
                        }
                    }

                    @Override
                    public void onError(String error) {
                        activity.showSnackBar("未知网络错误,请检查网络设置");
                    }
                }

        );
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
                            activity.getPassword());
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

    @Override
    public void checkPhoneRegisted(VerifyPhoneContract.checkRegistedListener listener) {
        service.checkPhone(activity.getPhone(), listener);
    }

    @Override
    public void doLoginByPhone() {

    }

    public void saveMd5(String md5) {
        SharedPreferences sp = context
                .getSharedPreferences("md5", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sp.edit();
        editor.putString("md5", md5);
        editor.apply();
    }


}
