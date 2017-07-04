//package com.zd.miko.riji.MVP.Home;
//
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.os.Bundle;
//import android.os.Looper;
//import android.support.design.widget.CoordinatorLayout;
//import android.support.design.widget.Snackbar;
//import android.support.v4.app.Fragment;
//import android.support.v4.view.ViewPager;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//
//import com.zd.miko.riji.Adapter.MyVPAdapter;
//import com.zd.miko.riji.CustomView.ChangableView;
//import com.zd.miko.riji.MVP.Forget.ForgetContract;
//import com.zd.miko.riji.R;
//import com.zd.miko.riji.Utils.Utils;
//
//
//public class HomeFragment extends Fragment implements ForgetContract.View {
//    private Context context;
//
//    private ForgetContract.Presenter presenter;
//    private ProgressDialog progressDialog;
//    private CoordinatorLayout coordinator;
//
//
//    private ViewPager mViewPager;
//    private MyVPAdapter mAdapter;
//    private ChangableView btmIndicator_first;
//    private ChangableView btmIndicator_second;
//    private ChangableView btmIndicator_third;
//    private ChangableView btmIndicator_forth;
//
//    public HomeFragment() {
//        // Required empty public constructor
//    }
//
//    public static HomeFragment newInstance() {
//        return new HomeFragment();
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        context = getActivity();
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.forget_frag, container, false);
//        initView(view);
//        initToolbar(view);
//        initData();
//        initEvent();
//        return view;
//    }
//
//    @Override
//    public void onResume() {
//        presenter.start();
//        super.onResume();
//    }
//
//    private void initView(View view) {
//        coordinator = (CoordinatorLayout) view.findViewById(R.id.id_snackContainer);
//
//    }
//
//
//    private void initToolbar(View view) {
//        toolbar = (Toolbar) view.findViewById(R.id.id_toolbar);
//        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//    }
//
//
//    private void initData() {
//
//    }
//
//    void initEvent() {
//        toolbar.setNavigationOnClickListener(v -> {
//            getActivity().finish();
//        });
//        btModify.setOnClickListener(v -> {
//            Utils.hintKeyboard(context);
//            presenter.doModifyPass();
//        });
//    }
//
//
//    @Override
//    public void showProgress(String msg) {
//        if (null == progressDialog)
//            progressDialog = new ProgressDialog(context);
//        progressDialog.setTitle(msg);
//        progressDialog.show();
//    }
//
//    @Override
//    public void closeProgress() {
//        if (null != progressDialog) {
//            progressDialog.cancel();
//        }
//    }
//
//    @Override
//    public void showSnackBar(String msg) {
//        Snackbar.make(coordinator, msg, Snackbar.LENGTH_SHORT).show();
//    }
//
//
//    /**
//     * 获取手机号，如果未传递则为null
//     */
//    @Override
//    public String getPhone() {
//        return phoneNum;
//    }
//
//    @Override
//    public String getRepeatPass() {
//        return edRepeatPass.getText().toString();
//    }
//
//    @Override
//    public String getPassword() {
//        return edPassword.getText().toString();
//    }
//
//    @Override
//    public Looper getLooper() {
//        return getActivity().getMainLooper();
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//    }
//
//    @Override
//    public void setPresenter(ForgetContract.Presenter presenter) {
//        this.presenter = presenter;
//    }
//
//}
