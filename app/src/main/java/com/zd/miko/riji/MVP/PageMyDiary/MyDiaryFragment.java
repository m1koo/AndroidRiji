package com.zd.miko.riji.MVP.PageMyDiary;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zd.miko.riji.Bean.ContentJson;
import com.zd.miko.riji.Bean.DiaryBriefBean;
import com.zd.miko.riji.Bean.Element;
import com.zd.miko.riji.Bean.PreviewObj;
import com.zd.miko.riji.Bean.RealmBean.RealmDiaryDetailBean;
import com.zd.miko.riji.CustomView.CircleButton;
import com.zd.miko.riji.CustomView.RichEditText.RichTextEditor;
import com.zd.miko.riji.CustomView.RippleView;
import com.zd.miko.riji.EventBusBean.ActivityEvent;
import com.zd.miko.riji.MVP.ModuleEditor.EditActivity;
import com.zd.miko.riji.MVP.ModuleReader.ReaderActivity;
import com.zd.miko.riji.R;
import com.zd.miko.riji.Utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyDiaryFragment extends Fragment implements MyDiaryContract.View {
    private int currMon = 0;
    private ArrayList<CircleButton> circleButtonList;
    private ArrayList<MonthBean> ovData;
    private ArrayList<DiaryBriefBean> mData;
    private RecyclerView recyclerView;
    private CircleButton ovCurrent;
    private CircleButton ovS1, ovS2, ovS3;
    private CircleButton ovCalender;
    private CircleButton fbtAnim;
    private TextView tvHint;
    private RelativeLayout rlHint;
    private RelativeLayout rlApha;

    private TextView tvCurrMonth;
    private Toolbar toolbar;
    private MyDiaryContract.Presenter presenter;
    private MyAdapter adapter;
    private RippleView rippleContainer;
    private ArrayList<Integer> colorList = new ArrayList<>();
    private FloatingActionButton floatButton;

    private ImageView ivCloud1, ivCloud2, ivCloud3, ivLeaf1, ivLeaf2;

    public MyDiaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        floatButton.setVisibility(View.VISIBLE);

        initRecycData();
        initEvent();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mydiary, container, false);
        initView(view);
        initRippleListener();
        initOvData();
        initOval();
        initCloudAnim();
        mData = null;
        return view;
    }

    private void initCloudAnim() {

        ivCloud1.post(() -> {
            startCloudAnim(ivCloud1, 50000, false);
            startCloudAnim(ivCloud2, 30000, true);
            startCloudAnim(ivCloud3, 60000, true);

            startLeafAnim(ivLeaf1, 3500, -140, -240);
            startLeafAnim(ivLeaf2, 4500, -220, 220);

        });


    }

    private void startLeafAnim(ImageView ivLeaf, int duration, int offsetY, int offsetR) {

        int len = ivLeaf.getRight();
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(ivLeaf, "translationX", 0, -len);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(ivLeaf, "translationY", 0, offsetY);

        ObjectAnimator anim3 = ObjectAnimator.ofFloat(ivLeaf, "rotation", 0, offsetR);

        anim1.setRepeatMode(ValueAnimator.RESTART);
        anim1.setRepeatCount(ValueAnimator.INFINITE);
        anim1.setInterpolator(new LinearInterpolator());

        anim2.setRepeatMode(ValueAnimator.RESTART);
        anim2.setRepeatCount(ValueAnimator.INFINITE);
        anim2.setInterpolator(new AnticipateOvershootInterpolator());


        anim3.setRepeatMode(ValueAnimator.RESTART);
        anim3.setRepeatCount(ValueAnimator.INFINITE);

        AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(anim1, anim2, anim3);

        animSet.setDuration(duration);
        animSet.start();
    }

    private void startCloudAnim(ImageView ivCloud, int duration, boolean toLeft) {

        ObjectAnimator anim;
        if (toLeft) {
            int len = ivCloud.getRight();
            anim = ObjectAnimator.ofFloat(ivCloud, "translationX",
                    0, -len);
        } else {
            int len = Utils.getScreenWidth() - ivCloud.getLeft();
            anim = ObjectAnimator.ofFloat(ivCloud, "translationX",
                    0, len);
        }

        anim.setDuration(duration);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatMode(ValueAnimator.RESTART);
        anim.setRepeatCount(ValueAnimator.INFINITE);
        anim.start();
    }


    private void initOvData() {

        /**生成6个不同的颜色*/
        while (colorList.size() < 6) {
            int c = randomColorFactory();
            if (!colorList.contains(c)) {
                colorList.add(c);
            }
        }
        /**对颜色排序*/
        Collections.sort(colorList);

        /**获取当前的月*/
        int currentMonth = Utils.getToMonth();

        currMon = currentMonth;
        int preFirst = currentMonth + 11 > 12 ? currentMonth - 1 : currentMonth + 11;
        int preSecond = currentMonth + 10 > 12 ? currentMonth - 2 : currentMonth + 10;
        int preThird = currentMonth + 9 > 12 ? currentMonth - 3 : currentMonth + 9;

        ovData = new ArrayList<>();
        ovData.add(new MonthBean(currentMonth, colorList.get(0), 0));
        ovData.add(new MonthBean(preFirst, colorList.get(1), 1));
        ovData.add(new MonthBean(preSecond, colorList.get(2), 2));
        ovData.add(new MonthBean(preThird, colorList.get(3), 3));
        ovData.add(new MonthBean(4, colorList.get(4), 4));
    }

    /**
     * 当mData不为空的时候，说明是返回则只需要添加第一个数据即可
     */
    private void initRecycData() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmDiaryDetailBean> diaryRealms = realm
                .where(RealmDiaryDetailBean.class).equalTo("month", currMon)
                .findAll().sort("editTime", Sort.DESCENDING);

        /**第一次进入界面*/
        if (mData == null) {
            mData = new ArrayList<>();

            for (RealmDiaryDetailBean bean : diaryRealms) {
                DiaryBriefBean briefBean = getDiaryBrief(bean);
                mData.add(briefBean);
            }
            adapter = new MyAdapter(mData);
            adapter.setTopColor(ovData.get(0).getOvColor());
            recyclerView.setAdapter(adapter);
            initAdapterEvent();
        } else if (mData.size() == 0) {
            /**月份切换*/
            for (RealmDiaryDetailBean bean : diaryRealms) {
                DiaryBriefBean briefBean = getDiaryBrief(bean);
                mData.add(briefBean);
            }
            adapter.setTopColor(ovData.get(0).getOvColor());
            adapter.notifyDataSetChanged();
        }
        if (mData.size() == 0) {
            rlHint.setVisibility(View.VISIBLE);
            tvHint.setText("在" + ovData.get(0).getMonth() + "月份没有留下您的足迹");
        } else {
            rlHint.setVisibility(View.GONE);
        }
        realm.close();
    }

    private DiaryBriefBean getDiaryBrief(RealmDiaryDetailBean bean) {
        DiaryBriefBean briefBean = new DiaryBriefBean();
        String diaryContent = bean.getContent();

        ContentJson contentBean = new Gson().fromJson(diaryContent,
                ContentJson.class);

        StringBuilder sb = new StringBuilder();

        ArrayList<PreviewObj> previewPaths = new ArrayList<>();
        String dir = Environment.getExternalStorageDirectory()
                + "/meiriji/" + bean.getArticleId();
        for (Element e : contentBean.getElementList()) {
            if (e.getElementType() == RichTextEditor.TEXT) {
                sb.append(e.getContent());
            } else if (e.getElementType() == RichTextEditor.IMAGE) {
                previewPaths.add(new PreviewObj(0, dir
                        + "/image_" + e.getIndex() + ".cvv"));
            } else if (e.getElementType() == RichTextEditor.GIF) {
                previewPaths.add(new PreviewObj(0, dir
                        + "/gif_" + e.getIndex() + ".cvv"));
            } else if (e.getElementType() == RichTextEditor.VIDEO) {
                previewPaths.add(new PreviewObj(1, dir
                        + "/video_" + e.getIndex() + ".cvv"));
            }
        }
        briefBean.setArticleId(bean.getArticleId());
        briefBean.setBriefContent(String.valueOf(sb));
        briefBean.setDate(new Date(bean.getEditTime()));
        briefBean.setLocation(bean.getLocationStr());
        briefBean.setDay(String.valueOf(bean.getDay()));
        briefBean.setPreviewPaths(previewPaths);
        Date d = new Date();
        d.setTime(bean.getEditTime());
        briefBean.setWeek(Utils.getWeek(d));
        return briefBean;
    }


    private void initView(View view) {

        /**初始化Toolbar避免去掉状态栏后文字堆积*/

        tvHint = (TextView) view.findViewById(R.id.id_hint1);
        rlHint = (RelativeLayout) view.findViewById(R.id.id_rl_hint);
        ivCloud1 = (ImageView) view.findViewById(R.id.id_cloud1);
        ivCloud2 = (ImageView) view.findViewById(R.id.id_cloud2);
        ivCloud3 = (ImageView) view.findViewById(R.id.id_cloud3);

        ivLeaf1 = (ImageView) view.findViewById(R.id.id_leaf1);
        ivLeaf2 = (ImageView) view.findViewById(R.id.id_leaf2);

        rlApha = (RelativeLayout) view.findViewById(R.id.id_rl_recyc_alpha);

        floatButton = (FloatingActionButton) view.findViewById(R.id.fab);

        fbtAnim = (CircleButton) view.findViewById(R.id.fab_anim);

        rippleContainer = (RippleView) view.findViewById(R.id.ripple_container);

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        toolbar.setTitle("");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

        DrawerLayout drawer = (DrawerLayout) view.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this.getActivity(),
                drawer, toolbar, 0, 0);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        tvCurrMonth = (TextView) view.findViewById(R.id.id_tv_currmonth);

        ovCurrent = (CircleButton) view.findViewById(R.id.id_ov_current);

        ovCalender = (CircleButton) view.findViewById(R.id.id_ov_calender);
        ovS1 = (CircleButton) view.findViewById(R.id.id_ov_s1);

        ovS2 = (CircleButton) view.findViewById(R.id.id_ov_s2);
        ovS3 = (CircleButton) view.findViewById(R.id.id_ov_s3);
        ovS1.tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 27);
        ovS2.tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        ovS3.tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

        ovS1.setIndex(1);
        ovS2.setIndex(2);
        ovS3.setIndex(3);

        circleButtonList = new ArrayList<>();
        circleButtonList.add(ovS1);
        circleButtonList.add(ovS2);
        circleButtonList.add(ovS3);

        recyclerView = (RecyclerView) view.findViewById(R.id.id_recyc);

        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

    }

    private void initRippleListener() {

        rippleContainer.setRipplelistener(new RippleView.onRippleListener() {
            @Override
            public void onEnd(int index) {
                CircleButton cb = circleButtonList.get(index - 1);

                ObjectAnimator anim = ObjectAnimator.ofFloat(cb, "alpha", 0f, 1f);

                AnimatorSet set = new AnimatorSet();
                set.playTogether(anim);

                set.setDuration(1000);
                set.start();
            }

            @Override
            public void onStart(int index) {

            }

            @Override
            public void onOver(int index) {

                /**设置被点击的Circle透明度为0，随后使用动画恢复*/
                CircleButton cbClicked = circleButtonList.get(index - 1);
                cbClicked.setAlpha(0);

                MonthBean tempC = ovData.get(index);

                ovData.set(index, ovData.get(0));
                ovData.set(0, tempC);
                currMon = tempC.getMonth();
                initOval();
                /**刷新数据*/
                mData = null;
                initRecycData();
            }

            @Override
            public void onStartOverCurrent(int index) {

            }
        });

    }

    private void initEvent() {
        floatButton.setOnClickListener(v -> {

            floatButton.setVisibility(View.INVISIBLE);
            ActivityOptions options = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                options = ActivityOptions
                        .makeSceneTransitionAnimation(this.getActivity(),
                                fbtAnim, "shareCircle");
            }
            Intent intent = new Intent(this.getContext(), EditActivity.class);
            intent.putExtra("editColor", colorList.get(5));
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                assert options != null;
                getActivity().startActivity(intent, options.toBundle());
            } else {
                getActivity().startActivity(intent);
            }
            /**返回界面后显示当前月份*/
            mData = null;
            currMon = Utils.getToMonth();
            initOvData();
            initOval();
        });
        ovCalender.setOnClickListener(v -> {
            onCalendarClick();
        });

    }

    public void initAdapterEvent() {
        adapter.setDiaryClick(new MyAdapter.OnDiaryClick() {
            @Override
            public void onShortClick(int position, MyVH holder) {
                Intent intent = new Intent(MyDiaryFragment.this.getContext(),
                        ReaderActivity.class);
                intent.putExtra("articleId", mData.get(position).getArticleId());
                if (position == 0) {
                    intent.putExtra("currentColor", holder.cardView.getCardBackgroundColor().getDefaultColor());
                } else {
                    intent.putExtra("currentColor", holder.cardView.getCardBackgroundColor().getDefaultColor());
                }
                ActivityOptions options = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    holder.itemView.setTransitionName("itemShare");
                    options = ActivityOptions
                            .makeSceneTransitionAnimation(MyDiaryFragment.this.getActivity(),
                                    holder.itemView, "itemShare");
                }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    assert options != null;
                    getActivity().startActivity(intent, options.toBundle());
                } else {
                    getActivity().startActivity(intent);
                }
            }

            @Override
            public void onLongClick(int position, MyVH holder) {

                PopupMenu popup = new PopupMenu(getActivity(),
                        holder.cardView, Gravity.RIGHT);
                popup.setOnDismissListener(menu -> rlApha.setVisibility(View.GONE));
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.menu_pop_delete, popup.getMenu());

                /**删除逻辑*/
                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.delete) {
                        String articleId = mData.get(position).getArticleId();
                        Realm realm = Realm.getDefaultInstance();
                        RealmDiaryDetailBean bean = realm
                                .where(RealmDiaryDetailBean.class)
                                .equalTo("articleId", articleId).findFirst();
                        realm.executeTransaction(realm1 -> bean.deleteFromRealm());
                        mData.remove(position);
                        adapter.notifyDataSetChanged();

                        /**发送广播，activity响应 snackbar*/
                        ActivityEvent activityEvent = new ActivityEvent();
                        activityEvent.event = ActivityEvent.SHOW_SNACKBAR_MSG;
                        activityEvent.message = "日记已删除";
                        EventBus.getDefault().post(activityEvent);
                        realm.close();
                    }
                    return true;
                });

                popup.show(); //showing popup men
                rlApha.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public void setPresenter(MyDiaryContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void initOval() {


//        animOvCurrent.setBgColor(ovData.get(0).getOvColor());
        ovCurrent.setBgColor(ovData.get(0).getOvColor());

        ovS1.tv.setText(ovData.get(1).getMonth() + "月");

        ovS2.tv.setText(ovData.get(2).getMonth() + "月");
        ovS3.tv.setText(ovData.get(3).getMonth() + "月");

        ovS1.setBgColor(ovData.get(1).getOvColor());
        ovS2.setBgColor(ovData.get(2).getOvColor());
        ovS3.setBgColor(ovData.get(3).getOvColor());

        ovCalender.setBgColor(ovData.get(4).getOvColor());

        floatButton.setBackgroundTintList(ColorStateList.valueOf(colorList.get(5)));
        fbtAnim.setBgColor(colorList.get(5));

        tvCurrMonth.setText(ovData.get(0).getMonth() + "月");
        setOvText(ovS1.tv);
        setOvText(ovS2.tv);
        setOvText(ovS3.tv);
        setOvText(tvCurrMonth);
    }

    public void setOvText(TextView tv) {

        String t = tv.getText().toString();

        SpannableStringBuilder builder1 = new SpannableStringBuilder
                (t);
        //改变第0-3个字体颜色为蓝色
        //改变第0-3个字体大小
        builder1.setSpan(new AbsoluteSizeSpan((int) (tv.getTextSize() / 2.5)),
                t.length() - 1, t.length(),
                Spanned.
                        SPAN_EXCLUSIVE_INCLUSIVE);

        tv.setText(builder1);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();//这句话没用啊，谁知道
        inflater.inflate(R.menu.menu_my, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_calender) {

//
//            /**发送广播，activity响应 隐藏导航栏*/
//            ActivityEvent activityEvent = new ActivityEvent();
//            activityEvent.event = ActivityEvent.HIDE_BOTTOM;
//            EventBus.getDefault().post(activityEvent);


            onCalendarClick();
        }
        return super.onOptionsItemSelected(item);
    }


    public void onCalendarClick() {
        FragmentTransaction mFragTransaction = getFragmentManager().beginTransaction();
        CalendarDialogFragment dialogFragment = new CalendarDialogFragment();
        dialogFragment.currentColor = ovCalender.getBgColor();
        dialogFragment.setListener(() -> {
            initOvData();
            initOval();
            mData = null;

            initRecycData();
            initEvent();
        });
        dialogFragment.show(mFragTransaction, "dialog");

    }

    @Override
    public void startEnterAnim() {

    }

    @Override
    public void startChangeAnim(int changedIndex) {
    }

    @Override
    public void refreshList() {

    }

    @Override
    public void openCalendarDialog() {

    }

    public int randomColorFactory() {

        String s = "#ff5177" +
                " #03a9f4" +
                " #8bc34a #9575cd #00bcd4 #ff8a80 #66CC99 #FA8072" +
                " #ffab40 #ff6e40";
        String[] sArray = s.split(" ");
        String sb = "";
        for (int i = 0; i < sArray.length; i++) {
            if (i % 2 == 1) {
                sb = sb + sArray[i] + " ";
            }
        }

        int len = sArray.length;
        int r = new Random().nextInt(len - 1);
        String rC = sArray[r];
        return Color.parseColor(rC);
    }

    public class MonthBean {
        int month;
        int ovColor;
        int index;

        @Override
        public String toString() {
            return "MonthBean{" +
                    "month='" + month + '\'' +
                    ", ovColor=" + ovColor +
                    ", index=" + index +
                    '}';
        }

        public MonthBean(int month, int ovColor, int index) {
            this.month = month;
            this.ovColor = ovColor;
            this.index = index;
        }

        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public int getOvColor() {
            return ovColor;
        }

        public void setOvColor(int ovColor) {
            this.ovColor = ovColor;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }
}
