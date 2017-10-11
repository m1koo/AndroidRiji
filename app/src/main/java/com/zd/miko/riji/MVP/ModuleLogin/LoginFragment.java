package com.zd.miko.riji.MVP.ModuleLogin;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.zd.miko.riji.R;
import com.zd.miko.riji.Utils.NativeUtil;


public class LoginFragment extends Fragment implements LoginContract.View {

    private final int TIMER = 60;
    private Handler handler;
    private LoginContract.Presenter presenter;
    private CoordinatorLayout coordinator;
    private EditText edPhone, edModifyCode;
    private Button btVerify, btGetCode;
    private Button btQQLogin, btWeiXinLogin, btWeiboLogin;

    private Context context;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        context = getActivity();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.login_frag, container, false);
        initView(view);
        initEvent();
        return view;
    }

    private void initEvent() {
        btGetCode.setOnClickListener(v -> {
            closeKeyboard();
            presenter.getModifyCode();
        });
        btVerify.setOnClickListener(v -> {
            closeKeyboard();
            presenter.doVerify();
        });
    }

    private void initView(View view) {
        edPhone = (EditText) view.findViewById(R.id.id_ed_phone);
        edModifyCode = (EditText) view.findViewById(R.id.id_ed_modifycode);
        btGetCode = (Button) view.findViewById(R.id.id_bt_get_modify);
        btVerify = (Button) view.findViewById(R.id.id_bt_verify);
        btQQLogin = (Button) view.findViewById(R.id.id_bt_qq);
        btWeiboLogin = (Button) view.findViewById(R.id.id_bt_weibo);
        btWeiXinLogin = (Button) view.findViewById(R.id.id_bt_weixin);
        coordinator = (CoordinatorLayout) view.findViewById(R.id.id_snackContainer);
    }


    @Override
    public void showProgress(String msg) {

    }

    @Override
    public void closeProgress() {

    }

    @Override
    public void showSnackBar(String msg) {
        Snackbar.make(coordinator, msg, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public String getPhone() {
        return edPhone.getText().toString();
    }

    @Override
    public String getModifyCode() {
        return edModifyCode.getText().toString();
    }

    @Override
    public void startCountDown() {
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

    @Override
    public void closeKeyboard() {
        NativeUtil.closeKeyboard(getActivity().getWindow());
    }

    private void initHandler() {
        handler = new Handler(getActivity().getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                updateHintButton("重新获取(" + msg.getData()
                        .getInt("t") + "秒)", false);
                if (msg.getData().getInt("t") == 0) {
                    updateHintButton("获取验证码", true);
                }
            }

        };
    }


    private void updateHintButton(String t, boolean clickable) {
        btGetCode.setText(t);
        if (clickable) {
            btGetCode.setTextColor(Color.BLACK);
        } else {
            btGetCode.setTextColor(Color.GRAY);
        }
        btGetCode.setClickable(clickable);
    }

    @Override
    public void setPresenter(LoginContract.Presenter presenter) {
        this.presenter = presenter;
    }
}
