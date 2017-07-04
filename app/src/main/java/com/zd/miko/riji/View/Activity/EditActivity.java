//package com.zd.miko.riji.View.Activity;
//
//import android.Manifest;
//import android.content.Intent;
//import android.graphics.Color;
//import android.os.Build;
//import android.os.Bundle;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.CardView;
//import android.support.v7.widget.Toolbar;
//import android.util.Log;
//import android.util.TypedValue;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.WindowManager;
//import android.widget.Button;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.PopupWindow;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.bumptech.glide.Glide;
//import com.yuyh.library.imgsel.ImageLoader;
//import com.yuyh.library.imgsel.ImgSelActivity;
//import com.yuyh.library.imgsel.ImgSelConfig;
//import com.zd.miko.riji.CustomView.CircleIvWithCheck;
//import com.zd.miko.riji.CustomView.RichEditText.RichTextEditor;
//import com.zd.miko.riji.R;
//import com.zd.miko.riji.Utils.Utils;
//
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//import java.util.Random;
//
//import kr.co.namee.permissiongen.PermissionFail;
//import kr.co.namee.permissiongen.PermissionGen;
//import kr.co.namee.permissiongen.PermissionSuccess;
//
//
///**
// * 1.布局随键盘上移：getWindow().setSoftInputMode(
// * WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
// * 2.禁止EditText自动获取焦点：在父类容器中 android:focusable="true"
// * android:focusableInTouchMode="true"
// * 3.Toolbar标题隐藏：getSupportActionBar().setDisplayShowTitleEnabled(false);
// * 4.递归实现颜色选择器View的状态改变
// * 5.随机值配合递归实现打开界面随机颜色
// * 6.注意的是按返回键退出时static的数据并不会被回收，应在onResume中重置
// */
//public class EditActivity extends AppCompatActivity implements View.OnClickListener {
//
//    private String currentPosition = "未设置";
//    /**
//     * 对应LocationDialog中的五个位置选项
//     */
//    private int currentPositionCode;
//
//    private AlertDialog locationDialog, attachDialog;
//
//    public int REQUEST_CODE_SELECTOR = 100;
//
//    private ImageView ivLocation;
//
//    private LinearLayout lnLocation;
//    private TextView tvLocation;
//    /**
//     * Toolbar颜色选择器
//     */
//    private PopupWindow popColorSelector;
//
//    /**
//     * 改变颜色，沉浸状态栏
//     */
//    private RelativeLayout linearActivity;
//
//    /**
//     * colorSelector content
//     */
//    private CardView colorSelectorContent;
//
//    private Toolbar toolbar;
//    private RichTextEditor richTextEditor;
//
//    private AMapLocationClient mlocationClient;
//
//    private TextView tvDatePicker;
//
//    /**
//     * 所选择的日期，初始化为系统日期
//     */
//    private int yearSelected, monthSelected, daySelected;
//
//    private Calendar calendar = Calendar.getInstance();
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_edit);
//
//        /**布局随键盘上移*/
//        getWindow().setSoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
//        );
//        initStatusBar();
//        initView();
//        initToolbar();
//
//        initData();
//        initView();
//        initEvent();
//    }
//
//
//    private void initData() {
//        yearSelected = calendar.get(Calendar.YEAR);
//        monthSelected = calendar.get(Calendar.MONTH) + 1;
//        daySelected = calendar.get(Calendar.DAY_OF_MONTH);
//    }
//
//    private void initEvent() {
//
//        lnLocation.setOnClickListener(v -> PermissionGen.needPermission(this, 200,
//                new String[]{
//                        Manifest.permission.ACCESS_COARSE_LOCATION,
//                        Manifest.permission.ACCESS_FINE_LOCATION
//                }
//        ));
//    }
//
//    private void initView() {
//
//        ivLocation = (ImageView) findViewById(R.id.id_iv_location_bottom);
//        ivLocation.setAlpha(0.6f);
//
//        tvLocation = (TextView) findViewById(R.id.id_tv_location_bottom);
//        tvLocation.setText(currentPosition);
//        lnLocation = (LinearLayout) findViewById(R.id.id_ln_location_bottom);
//
//        linearActivity = (RelativeLayout) findViewById(R.id.activity_main);
//
//        colorSelectorContent = (CardView) LayoutInflater.from(this)
//                .inflate(R.layout.popup_toolbar_colorpen, null);
//
//        richTextEditor = (RichTextEditor) findViewById(R.id.id_richedit);
//
//        CircleIvWithCheck color1 = (CircleIvWithCheck) colorSelectorContent.findViewById(R.id.id_color_selector1);
//        CircleIvWithCheck color2 = (CircleIvWithCheck) colorSelectorContent.findViewById(R.id.id_color_selector2);
//        CircleIvWithCheck color3 = (CircleIvWithCheck) colorSelectorContent.findViewById(R.id.id_color_selector3);
//        CircleIvWithCheck color4 = (CircleIvWithCheck) colorSelectorContent.findViewById(R.id.id_color_selector4);
//        CircleIvWithCheck color5 = (CircleIvWithCheck) colorSelectorContent.findViewById(R.id.id_color_selector5);
//        CircleIvWithCheck color6 = (CircleIvWithCheck) colorSelectorContent.findViewById(R.id.id_color_selector6);
//        CircleIvWithCheck color7 = (CircleIvWithCheck) colorSelectorContent.findViewById(R.id.id_color_selector7);
//        CircleIvWithCheck color8 = (CircleIvWithCheck) colorSelectorContent.findViewById(R.id.id_color_selector8);
//
//        color1.setOnClickListener(this);
//        color2.setOnClickListener(this);
//        color3.setOnClickListener(this);
//        color4.setOnClickListener(this);
//        color5.setOnClickListener(this);
//        color6.setOnClickListener(this);
//        color7.setOnClickListener(this);
//        color8.setOnClickListener(this);
//
//        int random = new Random().nextInt(8);
//        setCheckRandomColor(colorSelectorContent, random);
//
//        /**为深色改变EditText颜色*/
//
//        if (!Utils.isLightColor(currentColor)) {
//            setLightTheme();
//        } else {
//            setDarkTheme();
//        }
//        /**随机值选取出颜色，改变了currentColor*/
//        linearActivity.setBackgroundColor(currentColor);
//        richTextEditor.setBackgroundColor(currentColor);
//
//        richTextEditor.setHintText("记录下美好");
//    }
//
//    private void setLightTheme() {
//        ivLocation.setImageResource(R.drawable.ic_location_on_white_48dp);
//        tvLocation.setTextColor(getResources().getColor(R.color.defaultTextColorLight));
//        richTextEditor.setRichTextColor(getResources().getColor(R.color.defaultTextColorLight));
//        richTextEditor.setHintColor(Color.parseColor("#ffCDC5BF"));
//    }
//
//    private void setDarkTheme() {
//        ivLocation.setImageResource(R.drawable.ic_location_on_black_48dp);
//        richTextEditor.setHintColor(Color.parseColor("#ffCDC5BF"));
//        tvLocation.setTextColor(getResources().getColor(R.color.defaultTextColorDark));
//        richTextEditor.setRichTextColor(getResources().getColor(R.color.defaultTextColorDark));
//    }
//
//    private void initToolbar() {
//        toolbar = (Toolbar) findViewById(R.id.id_toolbar);
//
//        setSupportActionBar(toolbar);
//
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//    }
//
//    private void initStatusBar() {
//        if (Build.VERSION.SDK_INT >= 21) {
//            View decorView = getWindow().getDecorView();
//            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
//            decorView.setSystemUiVisibility(option);
//            getWindow().setStatusBarColor(Color.parseColor("#55000000"));
//        }
//    }
//
//    private void startLocationService() {
//        //声明mLocationOption对象
//        mlocationClient = new AMapLocationClient(this);
//        AMapLocationClientOption mLocationOption = null;
//        mLocationOption = new AMapLocationClientOption();
//        mlocationClient.setLocationListener(amapLocation -> {
//            if (amapLocation != null) {
//                if (amapLocation.getErrorCode() == 0) {
//
//                    ((TextView) locationDialog
//                            .findViewById(R.id.id_dialog_tv_city))
//                            .setText(amapLocation.getCity());
//                    ((TextView) locationDialog
//                            .findViewById(R.id.id_dialog_tv_aoi))
//                            .setText(amapLocation.getAoiName());
//                    ((TextView) locationDialog
//                            .findViewById(R.id.id_dialog_tv_district))
//                            .setText(amapLocation.getDistrict());
//
//                    ((TextView) locationDialog
//                            .findViewById(R.id.id_dialog_tv_poi))
//                            .setText(amapLocation.getPoiName());
//                    ((TextView) locationDialog
//                            .findViewById(R.id.id_dialog_tv_street))
//                            .setText(amapLocation.getStreet());
//
//                    ViewGroup dialogContent = (ViewGroup) locationDialog.getWindow().getDecorView();
//                    if (amapLocation.getCity().replace(" ", "").equals("")) {
//                        Utils.getViewsByTag(dialogContent, "1").get(0).setVisibility(View.GONE);
//                    }
//                    if (amapLocation.getDistrict().replace(" ", "").equals("")) {
//                        Utils.getViewsByTag(dialogContent, "2").get(0).setVisibility(View.GONE);
//                    }
//
//                    if (amapLocation.getStreet().replace(" ", "").equals("")) {
//                        Utils.getViewsByTag(dialogContent, "3").get(0).setVisibility(View.GONE);
//                    }
//
//                    if (amapLocation.getAoiName().replace(" ", "").equals("")) {
//                        Utils.getViewsByTag(dialogContent, "4").get(0).setVisibility(View.GONE);
//                    }
//
//                    if (amapLocation.getPoiName().replace(" ", "").equals("")) {
//                        Utils.getViewsByTag(dialogContent, "5").get(0).setVisibility(View.GONE);
//                    }
//
//                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                    Date date = new Date(amapLocation.getTime());
//                    df.format(date);//定位时间
//                } else {
//                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
//                    Log.e("AmapError", "location Error, ErrCode:"
//                            + amapLocation.getErrorCode() + ", errInfo:"
//                            + amapLocation.getErrorInfo());
//                }
//            }
//        });
//        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//        mLocationOption.setInterval(2000);
//        mlocationClient.setLocationOption(mLocationOption);
//        mlocationClient.startLocation();
//    }
//
//
//    private void initLocationDialog() {
//        LinearLayout dialogContent = (LinearLayout) LayoutInflater.from(this)
//                .inflate(R.layout.dialog_location, null);
//        TextView title = (TextView) dialogContent.findViewById(R.id.id_dialog_location_titile);
//        title.setTextColor(currentColor);
//        locationDialog = new AlertDialog.Builder(this)
//                .setView(dialogContent).create();
//
//        dialogContent.findViewById(R.id.id_dialog_rl_unsetting).setOnClickListener(this);
//        dialogContent.findViewById(R.id.id_dialog_rl_city).setOnClickListener(this);
//        dialogContent.findViewById(R.id.id_dialog_rl_street).setOnClickListener(this);
//        dialogContent.findViewById(R.id.id_dialog_rl_aoi).setOnClickListener(this);
//        dialogContent.findViewById(R.id.id_dialog_rl_poi).setOnClickListener(this);
//        dialogContent.findViewById(R.id.id_dialog_rl_district).setOnClickListener(this);
//        /**设置radio*/
//        for (int i = 0; i < 6; i++) {
//            Utils.getViewsByTag(dialogContent, "radio_" + i).get(0).setVisibility(View.INVISIBLE);
//        }
//        Utils.getViewsByTag(dialogContent, "radio_" + currentPositionCode)
//                .get(0).setVisibility(View.VISIBLE);
//    }
//
//
//    private void initAttachDialog() {
//        LinearLayout dialogContent = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.dialog_attach, null);
//
//        TextView title = (TextView) dialogContent.findViewById(R.id.id_dialog_attach_titile);
//        title.setTextColor(currentColor);
//        if (attachDialog == null)
//            attachDialog = new AlertDialog.Builder(this)
//                    .setView(dialogContent).create();
//        LinearLayout lnImage = (LinearLayout) dialogContent.findViewById(R.id.id_rl_open_camera);
//        LinearLayout lnMic = (LinearLayout) dialogContent
//                .findViewById(R.id.id_rl_open_mic);
//        LinearLayout lnVideo = (LinearLayout) dialogContent
//                .findViewById(R.id.id_rl_add_video);
//
//        lnMic.setOnClickListener(v -> {
//            attachDialog.dismiss();
//        });
//        lnImage.setOnClickListener(v -> {
//            attachDialog.dismiss();
//            openPicSelectorActivity();
//        });
//        lnVideo.setOnClickListener(v -> {
//            attachDialog.dismiss();
//        });
//    }
//
//    private void openPicSelectorActivity() {
//        ImageLoader loader = (ImageLoader) (context, path, imageView) -> {
//            // TODO 在这边可以自定义图片加载库来加载ImageView，例如Glide、Picasso、ImageLoader等
//            Glide.with(context).load(path).into(imageView);
//        };
//        ImgSelConfig config = new ImgSelConfig.Builder(this, loader)
//                // 是否多选, 默认true
//                .multiSelect(true)
//                // 是否记住上次选中记录, 仅当multiSelect为true的时候配置，默认为true
//                .rememberSelected(false)
//                // “确定”按钮背景色
//                .btnBgColor(getResources().getColor(R.color.editorColor1))
//                // “确定”按钮文字颜色
//                .btnTextColor(Color.WHITE)
//                // 使用沉浸式状态栏
//                .statusBarColor(currentColor)
//                // 返回图标ResId
//                .backResId(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material)
//                // 标题
//                .title("选择图片")
//                // 标题文字颜色
//                .titleColor(Color.WHITE)
//                // TitleBar背景色
//                .titleBgColor(currentColor)
//                // 裁剪大小。needCrop为true的时候配置
//                .needCrop(false)
//                // 第一个是否显示相机，默认true
//                .needCamera(true)
//                // 最大选择图片数量，默认9
//                .maxNum(5)
//                .build();
//
//        ImgSelActivity.startActivity(this, config, REQUEST_CODE_SELECTOR);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_attach:
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                    PermissionGen.with(this)
//                            .addRequestCode(100)
//                            .permissions(
//                                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                                    Manifest.permission.CAMERA)
//                            .request();
//                }
//                break;
//            case R.id.action_changeColor:
//                if (popColorSelector == null) {
//                    popColorSelector = new PopupWindow(colorSelectorContent, Utils.dpToPx(130, this),
//                            FrameLayout.LayoutParams.WRAP_CONTENT);
//                    popColorSelector.setOutsideTouchable(true);
//                }
//                if (popColorSelector.isShowing()) {
//                    popColorSelector.dismiss();
//                } else
//                    popColorSelector.showAsDropDown(toolbar,
//                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP
//                                    , 180, getResources().getDisplayMetrics()), 0);
//                break;
//            case R.id.action_done:
//                LinearLayout dialogContent = (LinearLayout) LayoutInflater.from(this)
//                        .inflate(R.layout.dialog_done_confirm, null);
//                AlertDialog confirmDialog = new AlertDialog.Builder(this)
//                        .setView(dialogContent)
//                        .create();
//                Button btPositive = (Button) dialogContent
//                        .findViewById(R.id.id_bt_dialog_done_positive);
//
//                Button btNegative = (Button) dialogContent
//                        .findViewById(R.id.id_bt_dialog_done_negative);
//
//                btPositive.setOnClickListener(v -> {
//                    confirmDialog.dismiss();
//
//                });
//
//                btNegative.setOnClickListener(v -> {
//                    confirmDialog.dismiss();
//
//
//                });
//                confirmDialog.show();
//                break;
//        }
//        return true;
//    }
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.editor_menu, menu);
//        return true;
//    }
//
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           String[] permissions,
//                                           int[] grantResults) {
//        PermissionGen.onRequestPermissionsResult(this,
//                requestCode, permissions, grantResults);
//    }
//
//    @PermissionSuccess(requestCode = 100)
//    public void doSomething() {
//        initAttachDialog();
//        attachDialog.show();
//    }
//
//    @PermissionFail(requestCode = 100)
//    public void doFailSomething() {
//        Toast.makeText(this, "获取权限失败，无法添加图片", Toast.LENGTH_SHORT).show();
//    }
//
//    @PermissionSuccess(requestCode = 200)
//    public void doLocationSuc() {
//        initLocationDialog();
//        locationDialog.show();
//        startLocationService();
//    }
//
//    @PermissionFail(requestCode = 200)
//    public void doFailLocation() {
//        Toast.makeText(this, "获取权限失败，无法获取位置", Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_CODE_SELECTOR && resultCode == RESULT_OK && data != null) {
//            List<String> pathList = data.getStringArrayListExtra
//                    (ImgSelActivity.INTENT_RESULT);
//            for (String path : pathList) {
//                richTextEditor.insertImage(path);
//            }
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (mlocationClient != null)
//            mlocationClient.stopLocation();
//
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        circleSelectorCounter = 0;
//    }
//
//    @Override
//    public void onClick(View v) {
//        if (v instanceof RelativeLayout && !(v instanceof CircleIvWithCheck)) {
//            currentPositionCode = Integer.parseInt(v.getTag().toString());
//            if (currentPositionCode == 0) {
//                currentPosition = "未设置";
//            } else {
//                TextView tv = (TextView) Utils.getViewsByTag(
//                        (ViewGroup) locationDialog.getWindow().getDecorView()
//                        , "text_" + currentPositionCode).get(0);
//                currentPosition = tv.getText().toString();
//                if (currentPosition.equals("获取中")) {
//                    currentPosition = "未设置";
//                }
//            }
//            if (mlocationClient != null)
//                mlocationClient.stopLocation();
//            locationDialog.dismiss();
//            tvLocation.setText(currentPosition);
//            return;
//        }
//
//        setCheckInvisible(colorSelectorContent);
//        ((CircleIvWithCheck) colorSelectorContent.findViewById(v.getId())).setCheckVisible(View.VISIBLE);
//        /**获取CircleIv的颜色并应用于整个界面*/
//        currentColor = ((CircleIvWithCheck)
//                colorSelectorContent.findViewById(v.getId())).getColor();
//        popColorSelector.dismiss();
//
//        if (!Utils.isLightColor(currentColor)) {
//            setLightTheme();
//        } else {
//            setDarkTheme();
//        }
//        linearActivity.setBackgroundColor(currentColor);
//        richTextEditor.setBackgroundColor(currentColor);
//    }
//
//    /**
//     * 随机颜色计数器
//     */
//    private static int circleSelectorCounter = 0;
//    private int currentColor;
//
//    private void setCheckRandomColor(View view, int randomCount) {
//        if (view instanceof ViewGroup && !(view instanceof CircleIvWithCheck)) {
//            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
//                setCheckRandomColor(((ViewGroup) view).getChildAt(i), randomCount);
//            }
//        } else if (view instanceof CircleIvWithCheck) {
//            if (circleSelectorCounter == randomCount) {
//                ((CircleIvWithCheck) view).setCheckVisible(View.VISIBLE);
//                currentColor = ((CircleIvWithCheck) view).getColor();
//            } else {
//                ((CircleIvWithCheck) view).setCheckVisible(View.INVISIBLE);
//            }
//            circleSelectorCounter++;
//        }
//    }
//
//
//    /**
//     * 递归算法将所有选择器取消勾选
//     */
//    private void setCheckInvisible(View view) {
//        if (view instanceof ViewGroup && !(view instanceof CircleIvWithCheck)) {
//            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
//                setCheckInvisible(((ViewGroup) view).getChildAt(i));
//            }
//        } else if (view instanceof CircleIvWithCheck) {
//            ((CircleIvWithCheck) view).setCheckVisible(View.INVISIBLE);
//        }
//    }
//}
