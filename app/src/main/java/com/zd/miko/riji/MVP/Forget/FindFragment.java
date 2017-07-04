package com.zd.miko.riji.MVP.Forget;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.zd.miko.riji.R;
import com.zd.miko.riji.Utils.Utils;


public class FindFragment extends Fragment implements ForgetContract.View {
    private Context context;

    /**
     * 从上一个Fragment中传递来的手机号码
     */
    private String phoneNum;
    private ForgetContract.Presenter presenter;
    private ProgressDialog progressDialog;
    private LinearLayout root;
    private CoordinatorLayout coordinator;
    private EditText edPassword;
    private EditText edRepeatPass;
    private Button btModify;

    public FindFragment() {
        // Required empty public constructor
    }

    public static FindFragment newInstance() {
        return new FindFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        context = getActivity();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (null != bundle) {
            phoneNum = bundle.getString("phone");
        }
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.forget_frag, container, false);
        initView(view);
        initData();
        initEvent();
        return view;
    }

    @Override
    public void onResume() {
        presenter.start();
        super.onResume();
    }

    private void initView(View view) {
        root = (LinearLayout) view.findViewById(R.id.id_rl_root);
        coordinator = (CoordinatorLayout) view.findViewById(R.id.id_snackContainer);
        edPassword = (EditText) view.findViewById(R.id.id_ed_password);
        edRepeatPass = (EditText) view.findViewById(R.id.id_ed_repeat_pass);
        btModify = (Button) view.findViewById(R.id.id_bt_register);
    }




    private void initData() {

    }

    void initEvent() {
        btModify.setOnClickListener(v -> {
            Utils.hintKeyboard(context);
            presenter.doModifyPass();
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


    /**
     * 获取手机号，如果未传递则为null
     */
    @Override
    public String getPhone() {
        return phoneNum;
    }

    @Override
    public String getRepeatPass() {
        return edRepeatPass.getText().toString();
    }

    @Override
    public String getPassword() {
        return edPassword.getText().toString();
    }

    @Override
    public Looper getLooper() {
        return getActivity().getMainLooper();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void setPresenter(ForgetContract.Presenter presenter) {
        this.presenter = presenter;
    }

}
