package com.zd.miko.riji.MVP.VerifyPhone;

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
import android.widget.TextView;

import com.zd.miko.riji.R;
import com.zd.miko.riji.Utils.Utils;


public class VerifyPhoneFragment extends Fragment implements VerifyPhoneContract.View {
    private Context context;

    private VerifyPhoneContract.Presenter presenter;
    private ProgressDialog progressDialog;
    private LinearLayout root;
    private CoordinatorLayout coordinator;
    private EditText edPhone;
    private EditText edPassword;
    private TextView tvRightHint;
    private Button btVerify;

    public VerifyPhoneFragment() {
        // Required empty public constructor
    }

    public static VerifyPhoneFragment newInstance() {
        return new VerifyPhoneFragment();
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
        View view = inflater.inflate(R.layout.verify_phone_frag, container, false);
        initView(view);
        initData();
        initEvent();
        return view;
    }


    private void initView(View view) {
        root = (LinearLayout) view.findViewById(R.id.id_rl_root);
        coordinator = (CoordinatorLayout) view.findViewById(R.id.id_snackContainer);
        edPhone = (EditText) view.findViewById(R.id.id_ed_phone);
        edPassword = (EditText) view.findViewById(R.id.id_ed_password);
        tvRightHint = (TextView) view.findViewById(R.id.id_tv_right_hint);
        btVerify = (Button) view.findViewById(R.id.id_bt_virify);
    }


    private void initData() {

    }

    void initEvent() {
        btVerify.setOnClickListener(v -> {
            Utils.hintKeyboard(context);
            presenter.doVerify();
        });


        tvRightHint.setOnClickListener(v -> {
            Utils.hintKeyboard(context);
            presenter.getVerifyCode();
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
    public void updateHintButton(String text, boolean isClickable) {
        tvRightHint.setText(text);
        tvRightHint.setClickable(isClickable);
        if (isClickable) {
            tvRightHint.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else {
            tvRightHint.setTextColor(getResources().getColor(R.color.buttonGray));
        }
    }

    @Override
    public String getPhone() {
        return edPhone.getText().toString();
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
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void setTitle(String title) {
//        toolbar.setTitle(title);
    }

    @Override
    public void setEdPhoneHint(String hint) {
        edPhone.setHint(hint);
    }

    @Override
    public void onDestroyView() {
        presenter.destroySSM();
        super.onDestroyView();
    }

    @Override
    public void setPresenter(VerifyPhoneContract.Presenter presenter) {
        this.presenter = presenter;
    }
}
