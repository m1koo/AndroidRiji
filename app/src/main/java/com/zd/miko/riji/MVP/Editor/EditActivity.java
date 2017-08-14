package com.zd.miko.riji.MVP.Editor;

import android.animation.Animator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.zd.miko.riji.Bean.ContentJson;
import com.zd.miko.riji.Bean.Element;
import com.zd.miko.riji.Bean.RealmBean.RealmDiaryDetailBean;
import com.zd.miko.riji.CustomView.RichEditText.RichTextEditor;
import com.zd.miko.riji.MVP.Editor.EventBusMsg.MessageEvent;
import com.zd.miko.riji.MVP.Reader.ReaderActivity;
import com.zd.miko.riji.MVP.Service.UploadService;
import com.zd.miko.riji.MyApp;
import com.zd.miko.riji.R;
import com.zd.miko.riji.Utils.NetWorkUtils;
import com.zd.miko.riji.Utils.Utils;
import com.zd.miko.riji.CustomView.CircleButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;


/**
 * 1.布局随键盘上移：getWindow().setSoftInputMode(
 * WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
 * 2.禁止EditText自动获取焦点：在父类容器中 android:focusable="true"
 * android:focusableInTouchMode="true"
 * 3.Toolbar标题隐藏：getSupportActionBar().setDisplayShowTitleEnabled(false);
 * 4.递归实现颜色选择器View的状态改变
 * 5.随机值配合递归实现打开界面随机颜色
 * 6.注意的是按返回键退出时static的数据并不会被回收，应在onResume中重置
 */
public class EditActivity extends AppCompatActivity {

    String latitudeStr, longitudeStr;
    String selectLocationStr = "未设置";
    private AMapLocationClient mLocationClient = null;

    private RadioGroup radioGroup;
    private RadioButton radioButton1, radioButton2, radioButton3;
    private Date editTime = null;
    private String currentPosition = "未设置";
    /**
     * 对应LocationDialog中的五个位置选项
     */
    private int currentPositionCode;

    private CircleButton circleButtonAnim;

    private AlertDialog locationDialog, attachDialog;

    private RelativeLayout rlToolbar;

    public int REQUEST_CODE_SELECTOR = 100;

    private ImageView ivLocation;

    private LinearLayout lnLocation;
    private TextView tvLocation;


    private Toolbar toolbar;
    private RichTextEditor richTextEditor;

    private RelativeLayout rlMain;

    private ProgressDialog progressDialog;
//    private AMapLocationClient mlocationClient;


    /**
     * 所选择的日期，初始化为系统日期
     */
    private int yearSelected, monthSelected, daySelected;

    private Calendar calendar = Calendar.getInstance();

    private int currentColor;

    private View mChildOfContent;
    private int usableHeightPrevious;
    private FrameLayout.LayoutParams frameLayoutParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.push(this);
        setContentView(R.layout.activity_edit);

//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        currentColor = getIntent().getIntExtra("editColor", Color.GREEN);

        initView();
        initToolbar();

        initData();
        initEvent();
        initKeyboard();

        initLocationDialog();

        startLocating();

        /**如果不为0，则说明设定了time，在日历中打开*/
        long l = getIntent().getLongExtra("editTime", 0);
        if (l != 0) {
            editTime = new Date(l);
            rlToolbar.setVisibility(View.VISIBLE);
            rlToolbar.setBackgroundColor(currentColor);
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            Transition transition = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                transition = getWindow().getSharedElementEnterTransition();
            }
            if (transition != null) {
                transition.addListener(new Transition.TransitionListener() {
                    @Override
                    public void onTransitionStart(Transition transition) {

                    }

                    @Override
                    public void onTransitionEnd(Transition transition) {
                        animateRevealShow(rlToolbar);
                    }

                    @Override
                    public void onTransitionCancel(Transition transition) {

                    }

                    @Override
                    public void onTransitionPause(Transition transition) {

                    }

                    @Override
                    public void onTransitionResume(Transition transition) {

                    }

                });
            } else {
                rlToolbar.setVisibility(View.VISIBLE);
                rlToolbar.setBackgroundColor(currentColor);
            }
        }

    }

    /**2。0版本取消使用*/
    private void initKeyboard() {
    }

    private void possiblyResizeChildOfContent() {
        int usableHeightNow = computeUsableHeight();
        if (usableHeightNow != usableHeightPrevious) {
            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
            if (heightDifference > (usableHeightSansKeyboard / 4)) {
                // keyboard probably just became visible
                frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
            } else {
                // keyboard probably just became hidden
                frameLayoutParams.height = usableHeightSansKeyboard;
            }
            mChildOfContent.requestLayout();
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        return (r.bottom - r.top);// 全屏模式下： return r.bottom
    }

    private void animateRevealShow(View viewRoot) {
        int cx = (viewRoot.getLeft() + viewRoot.getRight()) / 2;
        int cy = circleButtonAnim.getTop() + circleButtonAnim.getWidth() / 2;
        int finalRadius = Math.max(viewRoot.getWidth(), viewRoot.getHeight());

        Animator anim = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy,
                    circleButtonAnim.getWidth() / 2, finalRadius);
        }
        viewRoot.setVisibility(View.VISIBLE);
        anim.setDuration(500);
        anim.setInterpolator(new AccelerateInterpolator());
        anim.start();
    }

    private void initData() {
        yearSelected = calendar.get(Calendar.YEAR);
        monthSelected = calendar.get(Calendar.MONTH) + 1;
        daySelected = calendar.get(Calendar.DAY_OF_MONTH);
    }

    private void initEvent() {

        lnLocation.setOnClickListener(v -> locationDialog.show());
        richTextEditor.setOnImageClickListener((path, type) -> {
            Intent intent = new Intent(this, ImagePreviewActivity.class);
            intent.putExtra("path", path);
            intent.putExtra("type", type);
            startActivity(intent);
        });

    }

    private void initView() {

        rlToolbar = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.toolbar_layout, null);

        ViewGroup decorViewGroup = (ViewGroup) getWindow().getDecorView();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                Utils.dpToPx(56)+Utils.getStatusBarHeight());

        params.gravity = Gravity.TOP;
        rlToolbar.setLayoutParams(params);
        rlToolbar.setBackgroundColor(currentColor);
        decorViewGroup.addView(rlToolbar);

        circleButtonAnim = (CircleButton) findViewById(R.id.id_circle_anim);

        circleButtonAnim.setBgColor(currentColor);

        progressDialog = new ProgressDialog(this);

        rlMain = (RelativeLayout) findViewById(R.id.activity_main);

        ivLocation = (ImageView) findViewById(R.id.id_iv_location_bottom);
        ivLocation.setAlpha(0.6f);

        tvLocation = (TextView) findViewById(R.id.id_tv_location_bottom);
        tvLocation.setText(currentPosition);
        lnLocation = (LinearLayout) findViewById(R.id.id_ln_location_bottom);

        richTextEditor = (RichTextEditor) findViewById(R.id.id_richedit);

        toolbar = (Toolbar) findViewById(R.id.id_toolbar);

        /**为深色改变EditText颜色*/

        if (!Utils.isLightColor(currentColor)) {
            setLightTheme();
        } else {
            setDarkTheme();
        }

        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                Utils.dpToPx(56));
        params1.setMargins(0, Utils.getStatusBarHeight(), 0, 0);

        toolbar.setLayoutParams(params1);
        rlToolbar.setBackgroundColor(currentColor);
        rlToolbar.setVisibility(View.INVISIBLE);
        circleButtonAnim.setVisibility(View.VISIBLE);
        rlMain.setBackgroundColor(Color.WHITE);
        richTextEditor.setBackgroundColor(Color.WHITE);

        richTextEditor.setHintText("记录下美好");
    }

    private void setLightTheme() {
        ivLocation.setImageResource(R.drawable.ic_location_on_white_48dp);
        tvLocation.setTextColor(getResources().getColor(R.color.defaultTextColorLight));
        richTextEditor.setRichTextColor(getResources().getColor(R.color.defaultTextColorLight));
        richTextEditor.setHintColor(Color.parseColor("#ffCDC5BF"));
    }

    private void setDarkTheme() {
        ivLocation.setImageResource(R.drawable.ic_location_on_black_48dp);
        richTextEditor.setHintColor(Color.parseColor("#ffCDC5BF"));
        tvLocation.setTextColor(getResources().getColor(R.color.defaultTextColorDark));
        richTextEditor.setRichTextColor(getResources().getColor(R.color.defaultTextColorDark));
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.id_toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setNavigationOnClickListener(v -> finishAfterTransition());
        } else {
            toolbar.setNavigationOnClickListener(v -> finish());
        }
    }

    /**
     * 分为几种情况：
     * 1、直接获取到location，刷新tvLocation此时dialog未打开
     * 2、直接获取到location，但此时dialog已经打开
     * 3、获取location失败，重新获取
     * <p>
     * 策略是首先开始location，在开始前进行非空判断如果为空new否则destroy保证只有一个实例
     * 首先初始化dialog，获取到结果后刷新tvLocation，以及dialog，if error 则重新获取
     */
    private void startLocating() {

        AMapLocationListener mLocationListener = amapLocation -> {
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
                    Log.i("xyz", amapLocation.toString());

                    /**获取经纬度*/
                    latitudeStr = String.valueOf(amapLocation.getLatitude());
                    longitudeStr = String.valueOf(amapLocation.getLongitude());

                    String province = amapLocation.getProvince();
                    String city = amapLocation.getCity();
                    String district = amapLocation.getDistrict();
                    String poiName = amapLocation.getPoiName();

                    String tvLocationHint = city + "·" + poiName;

                    /**设置默认的位置为radio1*/
                    selectLocationStr = tvLocationHint;

                    tvLocation.setText(tvLocationHint);

                    radioButton1.setText(tvLocationHint);
                    radioButton2.setText(province + "·" + city);
                    radioButton3.setText(district);

                    radioGroup.check(R.id.id_radio1);
                } else {
                    /**重新定位*/
                    mLocationClient.stopLocation();
                    mLocationClient.onDestroy();
                    startLocating();
                }
            }
        };

        AMapLocationClientOption mLocationOption = null;

        mLocationClient = new AMapLocationClient(getApplicationContext());
        mLocationClient.setLocationListener(mLocationListener);
        //声明AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption
                .AMapLocationMode.Hight_Accuracy);
        mLocationOption.setOnceLocationLatest(true);

        mLocationOption.setNeedAddress(true);

        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.startLocation();
    }


    private void initLocationDialog() {
        LinearLayout dialogContent = (LinearLayout) LayoutInflater.from(this)
                .inflate(R.layout.dialog_location, null);
        TextView title = (TextView) dialogContent.findViewById(R.id.id_dialog_location_titile);
//        title.setTextColor(currentColor);
        locationDialog = new AlertDialog.Builder(this)
                .setView(dialogContent).create();
//
//        TextView tvCustom = dialogContent.findViewById()

        radioButton1 = (RadioButton) dialogContent.findViewById(R.id.id_radio1);
        radioButton2 = (RadioButton) dialogContent.findViewById(R.id.id_radio2);
        radioButton3 = (RadioButton) dialogContent.findViewById(R.id.id_radio3);

        radioGroup = (RadioGroup) dialogContent.findViewById(R.id.id_radio_group);

        radioGroup.check(R.id.id_radio0);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.id_radio0:
                    selectLocationStr = "未设置";
                    tvLocation.setText(selectLocationStr);
                    locationDialog.dismiss();
                    break;
                default:
                    RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                    selectLocationStr = (String) radioButton.getText();
                    tvLocation.setText(selectLocationStr);
                    locationDialog.dismiss();
            }
        });
    }


    private void initAttachDialog() {
        LinearLayout dialogContent = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.dialog_attach, null);

        TextView title = (TextView) dialogContent.findViewById(R.id.id_dialog_attach_titile);
        title.setTextColor(currentColor);
        if (attachDialog == null)
            attachDialog = new AlertDialog.Builder(this)
                    .setView(dialogContent).create();
        LinearLayout lnImage = (LinearLayout) dialogContent.findViewById(R.id.id_rl_open_camera);

        LinearLayout lnVideo = (LinearLayout) dialogContent
                .findViewById(R.id.id_rl_add_video);

        lnImage.setOnClickListener(v -> {
            attachDialog.dismiss();
            openPicSelectorActivity();
        });
        lnVideo.setOnClickListener(v -> {
            attachDialog.dismiss();
            openVideoSelectorActvity();
        });
    }

    private void openVideoSelectorActvity() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofVideo())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
//                .theme()//主题样式(不设置为默认样式) 也可参考demo values/styles下 例如：R.style.picture.white.style
                .maxSelectNum(20)// 最大图片选择数量 int
                .minSelectNum(0)// 最小选择数量 int
                .imageSpanCount(4)// 每行显示个数 int
                .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .previewImage(true)// 是否可预览图片 true or false
                .previewVideo(true)// 是否可预览视频 true or false
                .enablePreviewAudio(true) // 是否可播放音频  true or false
                .compressGrade(Luban.THIRD_GEAR)// luban压缩档次，默认3档 Luban.THIRD_GEAR、Luban.FIRST_GEAR、Luban.CUSTOM_GEAR
                .isCamera(true)// 是否显示拍照按钮 ture or false
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                .setOutputCameraPath("/Chinayie/App")// 自定义拍照保存路径,可不填
                .compress(true)// 是否压缩 true or false
                .compressMode(PictureConfig.LUBAN_COMPRESS_MODE)//系统自带 or 鲁班压缩 PictureConfig.SYSTEM_COMPRESS_MODE or LUBAN_COMPRESS_MODE
//                .glideOverride(100)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度 int
                .isGif(true)// 是否显示gif图片 true or false
                .openClickSound(false)// 是否开启点击声音 true or false
//                .selectionMedia(false)// 是否传入已选图片 List<LocalMedia> list
                .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
//                .compressMaxKB()//压缩最大值kb compressGrade()为Luban.CUSTOM_GEAR有效 int
//                .compressWH() // 压缩宽高比 compressGrade()为Luban.CUSTOM_GEAR有效 int
                .videoQuality(1)// 视频录制质量 0 or 1 int
//                .videoSecond()//显示多少秒以内的视频or音频也可适用 int
//                .recordVideoSecond()//录制视频秒数 默认60s int
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    private void openPicSelectorActivity() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
//                .theme()//主题样式(不设置为默认样式) 也可参考demo values/styles下 例如：R.style.picture.white.style
                .maxSelectNum(20)// 最大图片选择数量 int
                .minSelectNum(0)// 最小选择数量 int
                .imageSpanCount(4)// 每行显示个数 int
                .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .previewImage(true)// 是否可预览图片 true or false
                .previewVideo(true)// 是否可预览视频 true or false
                .enablePreviewAudio(true) // 是否可播放音频  true or false
                .compressGrade(Luban.THIRD_GEAR)// luban压缩档次，默认3档 Luban.THIRD_GEAR、Luban.FIRST_GEAR、Luban.CUSTOM_GEAR
                .isCamera(true)// 是否显示拍照按钮 ture or false
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                .setOutputCameraPath("/Chinayie/App")// 自定义拍照保存路径,可不填
                .compress(true)// 是否压缩 true or false
                .compressMode(PictureConfig.LUBAN_COMPRESS_MODE)//系统自带 or 鲁班压缩 PictureConfig.SYSTEM_COMPRESS_MODE or LUBAN_COMPRESS_MODE
//                .glideOverride(100)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度 int
                .isGif(true)// 是否显示gif图片 true or false
                .openClickSound(false)// 是否开启点击声音 true or false
//                .selectionMedia(false)// 是否传入已选图片 List<LocalMedia> list
                .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
//                .compressMaxKB()//压缩最大值kb compressGrade()为Luban.CUSTOM_GEAR有效 int
//                .compressWH() // 压缩宽高比 compressGrade()为Luban.CUSTOM_GEAR有效 int
                .videoQuality(1)// 视频录制质量 0 or 1 int
//                .videoSecond()//显示多少秒以内的视频or音频也可适用 int
//                .recordVideoSecond()//录制视频秒数 默认60s int
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_attach:
                initAttachDialog();
                attachDialog.show();

                break;
            case R.id.action_done:

                ContentJson contentJson = richTextEditor.getRichContent();

                /**检查是否为空*/

                StringBuilder sb = new StringBuilder();

                int counter = 0;
                for (Element e : contentJson.getElementList()) {
                    if (e.getElementType() == RichTextEditor.TEXT) {
                        sb.append(e.getContent());
                    } else if (e.getElementType() == RichTextEditor.IMAGE) {
                        counter++;
                    } else if (e.getElementType() == RichTextEditor.GIF) {
                        counter++;
                    } else if (e.getElementType() == RichTextEditor.VIDEO) {
                        counter++;
                    }
                }

                if (counter == 0 && sb.toString().replace(" ", "").equals("")) {
                    Toast.makeText(this, "日记内容不可为空", Toast.LENGTH_SHORT).show();
                    return true;
                }

                String content = new Gson().toJson(contentJson);

                Date date = new Date();

                /**设定为日历中的编辑日期*/
                if (editTime != null) {
                    date = editTime;
                }

                long l = date.getTime();
                SharedPreferences sp = getSharedPreferences("account",
                        Context.MODE_PRIVATE);

                String userId = sp.getString("id", "AN12345");

                String articleId = userId + "_" + l;

                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(realm1 -> {
                    RealmDiaryDetailBean diary = realm1
                            .createObject(RealmDiaryDetailBean.class);
                    diary.setEditTime(l);
                    diary.setArticleId(articleId);
                    diary.setUserId(userId);
                    diary.setYear(Utils.getYear(new Date(l)));
                    diary.setMonth(Utils.getMonth(new Date(l)));
                    diary.setDay(Utils.getDay(new Date(l)));
                    diary.setContent(content);
                    diary.setCompleteFlag(true);
                    diary.setLocationStr(selectLocationStr);
                    diary.setOutVisible(false);
                });


                progressDialog.setTitle("转码中请稍后");
                progressDialog.show();
                /**新建线程进行转码，拷贝*/
                new Thread(() -> {
                    String d1 = Environment.getExternalStorageDirectory()
                            + "/meiriji/" + articleId;
                    new File(d1).mkdirs();

                    ArrayList<File> fileList = new ArrayList<>();
                    for (int i = 0; i < contentJson.getElementList().size(); i++) {
                        Element e = contentJson.getElementList().get(i);
                        if (e.getElementType() == RichTextEditor.IMAGE) {
                            fileList.add(new File(e.getContent()));

                        } else if (e.getElementType() == RichTextEditor.VIDEO) {
                            Utils.copyFile(new File(e.getContent()), d1
                                    + "/video_" + e.getIndex() + ".cvv");
                        } else if (e.getElementType() == RichTextEditor.GIF) {
                            Utils.copyFile(new File(e.getContent()), d1
                                    + "/gif_" + e.getIndex() + ".cvv");
                        }
                    }


                    Flowable.fromIterable(fileList)
                            .map(file -> top.zibin.luban.Luban.with(EditActivity.this)
                                    .load(file).get())
                            .toList()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(fileList1 -> {
                                //图片压缩完成得到list
                                for (int i = 0; i < fileList1.size(); i++) {
                                    File f = fileList1.get(i);
                                    Utils.copyFile(f, d1 + "/image_" + i + ".cvv");
                                }
                                EventBus.getDefault().post(new MessageEvent(articleId));
                            });
                }).start();

                break;
        }
        return true;
    }


    @Subscribe
    public void onMessageEvent(MessageEvent event) throws InterruptedException {
        /**设置realm中完整的标志位为true*/
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(realm1 ->
                realm1.where(RealmDiaryDetailBean.class)
                        .findAll().first().setCompleteFlag(true));

        progressDialog.setTitle("完成^=^");
        Thread.sleep(500);
        progressDialog.cancel();
        //TODO 开始上传的 Service 打开 ReadActivity 分享按钮
        Intent intent = new Intent(this, ReaderActivity.class);
        intent.putExtra("articleId", event.message);
        intent.putExtra("currentColor", currentColor);

        startActivity(intent);

        boolean isWifiConnect = NetWorkUtils.isWifiConnected(this);

        if(isWifiConnect){

            UploadService.startService(this, event.message,null);

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor_menu, menu);
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    for (LocalMedia p : selectList) {
                        if (p.getPictureType().contains("gif")) {
                            richTextEditor.insertGif(p.getPath());
                        } else if (p.getPictureType().contains("image")) {
                            richTextEditor.insertImage(p.getPath());
                        } else if (p.getPictureType().contains("video")) {
                            richTextEditor.insertVideo(p.getPath());
                        }
                    }
                    break;
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (mlocationClient != null)
//            mlocationClient.stopLocation();
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
