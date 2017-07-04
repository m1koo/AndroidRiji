package com.zd.miko.riji.MVP.Login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zd.miko.riji.MVP.Forget.ForgetActivity;
import com.zd.miko.riji.MVP.Register.RegisterActivity;
import com.zd.miko.riji.R;
import com.zd.miko.riji.Utils.Utils;

import de.hdodenhof.circleimageview.CircleImageView;


public class LoginFragment extends Fragment implements LoginContract.View {

    private Context context;

    private LoginContract.Presenter presenter;
    private ProgressDialog progressDialog;
    private CoordinatorLayout coordinator;
    private CircleImageView headIcon;
    private Button btLogin;
    private EditText edUserName;
    private EditText edPassword;
    private LinearLayout root;
    private TextView tvForget;
    private TextView tvRegister;

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
        initData();
        initEvent();
        autoScrollView(root, btLogin);
        return view;
    }


    private void initView(View view) {
        tvRegister = (TextView) view.findViewById(R.id.id_tv_register);
        tvForget = (TextView) view.findViewById(R.id.id_tv_forget);
        root = (LinearLayout) view.findViewById(R.id.id_rl_root);
        headIcon = (CircleImageView) view.findViewById(R.id.id_headIcon);
        btLogin = (Button) view.findViewById(R.id.id_bt_login);
        edUserName = (EditText) view.findViewById(R.id.id_ed_username);
        edPassword = (EditText) view.findViewById(R.id.id_ed_password);
        coordinator = (CoordinatorLayout) view.findViewById(R.id.id_snackContainer);
    }



    private void initData() {

    }

    private void initEvent() {


        btLogin.setOnClickListener(v -> {
            Utils.hintKeyboard(context);
            presenter.doLogin();
        });
        tvForget.setOnClickListener(v -> startActivity(new Intent(context, ForgetActivity.class)));
        tvRegister.setOnClickListener(v -> startActivity(
                new Intent(context, RegisterActivity.class)));
    }

    private int scrollToPosition = 0;

    private void autoScrollView(final View root, final View scrollToView) {
        root.getViewTreeObserver().addOnGlobalLayoutListener(
                () -> {

                    Rect rect = new Rect();

                    //获取root在窗体的可视区域
                    root.getWindowVisibleDisplayFrame(rect);

                    //获取root在窗体的不可视区域高度(被遮挡的高度)
                    int rootInvisibleHeight = root.getRootView().getHeight() - rect.bottom;

                    //若不可视区域高度大于150，则键盘显示
                    if (rootInvisibleHeight > 150) {

                        //获取scrollToView在窗体的坐标,location[0]为x坐标，location[1]为y坐标
                        int[] location = new int[2];
                        scrollToView.getLocationInWindow(location);

                        //计算root滚动高度，使scrollToView在可见区域的底部
                        int scrollHeight = (location[1] + scrollToView.getHeight() + 20) - rect.bottom;

                        //注意，scrollHeight是一个相对移动距离，而scrollToPosition是一个绝对移动距离
                        scrollToPosition += scrollHeight;

                    } else {
                        //键盘隐藏
                        scrollToPosition = 0;
                    }
                    root.scrollTo(0, scrollToPosition);

                });
    }

    @Override
    public void showProgress(String msg) {
        if (null == progressDialog)
            progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(msg);
        progressDialog.show();
    }

    @Override
    public void closeProgress() {
        if (null != progressDialog) {
            progressDialog.cancel();
        }
    }

    @Override
    public void showSnackBar(String msg) {
        Snackbar.make(coordinator, msg, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public String getUserName() {
        return edUserName.getText().toString();
    }

    @Override
    public String getPassword() {
        return edPassword.getText().toString();
    }

    @Override
    public void setPresenter(LoginContract.Presenter presenter) {
        this.presenter = presenter;
    }
}
