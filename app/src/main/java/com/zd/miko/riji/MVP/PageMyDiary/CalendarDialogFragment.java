package com.zd.miko.riji.MVP.PageMyDiary;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.gson.Gson;
import com.zd.miko.riji.Bean.ContentJson;
import com.zd.miko.riji.Bean.DiaryBriefBean;
import com.zd.miko.riji.Bean.Element;
import com.zd.miko.riji.Bean.RealmBean.RealmDiaryDetailBean;
import com.zd.miko.riji.CustomView.RichEditText.RichTextEditor;
import com.zd.miko.riji.MVP.ModuleEditor.EditActivity;
import com.zd.miko.riji.MVP.ModuleReader.ReaderActivity;
import com.zd.miko.riji.R;
import com.zd.miko.riji.Utils.Utils;
import com.zd.miko.riji.CustomView.DashedLineView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by Miko on 2017/8/1.
 */

public class CalendarDialogFragment extends DialogFragment {

    private DashedLineView dashedLineView;
    private String[] monthStrArray = new String[]{"January", "February", "March", "April",
            "May",
            "June",
            "July",
            "August",
            "September",
            "October",
            "November",
            "December"
    };
    private ArrayList<DiaryBriefBean> mData = new ArrayList<>();
    private MyDiaryBottomAdapter adapter = new MyDiaryBottomAdapter(mData);
    private int currentMonth = Utils.getToMonth();
    private int currentYear = Utils.getToYear();
    private int currentDay = Utils.getToday();
    private TextView tvMonth, tvYear, tvWeek, tvCurrentDayLong, tvAdd, tvBottomHint;
    private Button leftButton, rightButton;
    private RelativeLayout rlBottomer;
    public int currentColor;
    public float rotiaWidht = 0.87f;

    private RelativeLayout rlBottomHint;
    private RecyclerView recyclerView;
    CompactCalendarView calendarView;

    public interface OnDismissListener {
        void onDismiss();
    }

    private OnDismissListener listener;

    public void setListener(OnDismissListener listener) {
        this.listener = listener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        listener.onDismiss();
    }

    @Override
    public void onStart() {
        super.onStart();
        Window dialogWindow = getDialog().getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (Utils.getScreenWidth() * rotiaWidht);
        lp.height = (int) (lp.width * 1.7);
        dialogWindow.setBackgroundDrawable(null);
        dialogWindow.setAttributes(lp);
    }

    @Override
    public void onResume() {
        super.onResume();

        /**初始化header*/
        refreshHeader(String.valueOf(currentYear), monthStrArray[currentMonth - 1]);

        /**初始化整个月的所有event*/
        initEvent(currentYear, currentMonth);

        /**初始化recyclerView*/
        initRecycData();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(
                R.layout.dialog_bg_calender, null);

        initView(view);

        initClick();
        calendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {

                Realm realm = Realm.getDefaultInstance();
                RealmResults<RealmDiaryDetailBean> diaryBeans = realm
                        .where(RealmDiaryDetailBean.class).equalTo("year",
                                Utils.getYear(dateClicked))
                        .equalTo("month", Utils.getMonth(dateClicked))
                        .equalTo("day", Utils.getDay(dateClicked))
                        .findAll().sort("editTime", Sort.ASCENDING);

                /**如果没有结果，设置recyc不可见，显示提示层*/
                if (diaryBeans.size() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    showBottomDateHint();
                } else {
                    hideBottomHint();
                    recyclerView.setVisibility(View.VISIBLE);
                    mData.clear();
                    for (RealmDiaryDetailBean diary : diaryBeans) {
                        DiaryBriefBean briefBean = getDiaryBrief(diary);
                        mData.add(briefBean);
                    }
                    adapter.notifyDataSetChanged();
                }

                /**设置第一天*/
                tvWeek.setText(Utils.getWeek(dateClicked));
                tvCurrentDayLong.setText(Utils.getStringDateShortSlash(dateClicked));

                tvAdd.setVisibility(View.VISIBLE);

                try {
                    tvAdd.setTag(getSbDate(dateClicked));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {

                int scrollMonth = Utils.getMonth(firstDayOfNewMonth);
                int scrollYear = Utils.getYear(firstDayOfNewMonth);
                refreshHeader(String.valueOf(scrollYear),
                        monthStrArray[scrollMonth - 1]);

                initEvent(scrollYear, scrollMonth);
                Realm realm = Realm.getDefaultInstance();

                RealmQuery<RealmDiaryDetailBean> diaryQuerys = realm
                        .where(RealmDiaryDetailBean.class).equalTo("year",
                                scrollYear)
                        .equalTo("month", scrollMonth);

                RealmResults<RealmDiaryDetailBean> diaryMonthBeens = diaryQuerys
                        .findAll().sort("editTime", Sort.ASCENDING);


                /**如果没有结果，不设置第一天，设置recyc 当前月为空*/
                if (diaryMonthBeens.size() == 0) {
                    recyclerView.setVisibility(View.GONE);

                    /**设置第一天*/
                    tvWeek.setText(Utils.getWeek(firstDayOfNewMonth));
                    tvCurrentDayLong.setText(Utils.getStringDateShortSlash(firstDayOfNewMonth));

                    tvAdd.setVisibility(View.GONE);
                    showBottomMonthHint();
                }
                /**否则设置选择最早的日期
                 *  刷新recycler
                 */
                else {
                    hideBottomHint();
                    RealmDiaryDetailBean first = diaryMonthBeens.first();
                    calendarView.setCurrentDate(new Date(first.getEditTime()));
                    recyclerView.setVisibility(View.VISIBLE);

                    mData.clear();

                    /**根据first 找到第一天的day，在Realm中查找到第一天的所有日记添加到mData中*/
                    RealmResults<RealmDiaryDetailBean> todayAllDiarys = diaryQuerys
                            .equalTo("day", first.getDay()).findAll()
                            .sort("editTime", Sort.ASCENDING);

                    for (RealmDiaryDetailBean diary : todayAllDiarys) {
                        DiaryBriefBean diaryBriefBean = getDiaryBrief(diary);
                        mData.add(diaryBriefBean);
                    }
                    adapter.notifyDataSetChanged();

                    tvAdd.setVisibility(View.VISIBLE);

                    try {
                        tvAdd.setTag(getSbDate(firstDayOfNewMonth));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    /**设置最早的一篇日记*/
                    tvWeek.setText(Utils.getWeek(new Date(first.getEditTime())));
                    tvCurrentDayLong.setText(Utils.getStringDateShortSlash(new Date(first.getEditTime())));
                }
                realm.close();
            }
        });

        return new AlertDialog.Builder(getActivity()).setView(view)
                .create();
    }

    private Date getSbDate(Date d) throws ParseException {
        String firstDateStrPre = Utils.getStringDateLong(d)
                .split(" ")[0];

        String currentDateStrSuf = Utils.getStringDateLong(new Date())
                .split(" ")[1];

        String sb = firstDateStrPre + " " + currentDateStrSuf;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date dre = null;
        dre = sdf.parse(sb);

        return dre;

    }

    private void initClick() {
        tvAdd.setOnClickListener(v -> {
            Intent i = new Intent(this.getContext(), EditActivity.class);
            i.putExtra("editColor", currentColor);
            i.putExtra("editTime", ((Date) tvAdd.getTag()).getTime());
            this.getActivity().startActivity(i);
        });
        leftButton.setOnClickListener(v -> {
            calendarView.showPreviousMonth();
        });
        rightButton.setOnClickListener(v -> {
            calendarView.showNextMonth();
        });
        adapter.setOnItemClick(new MyDiaryBottomAdapter.OnItemClick() {
            @Override
            public void itemClick(int position) {
                Intent intent = new Intent(CalendarDialogFragment.this.getContext(),
                        ReaderActivity.class);
                intent.putExtra("articleId", mData.get(position).getArticleId());
                intent.putExtra("currentColor", currentColor);
                startActivity(intent);
            }

            @Override
            public void itemLongClick(int position, MyDiaryBottomAdapter.MyBottomVH holder) {

                PopupMenu popup = new PopupMenu(getActivity(),
                        holder.itemView, Gravity.RIGHT);
                popup.getMenuInflater()
                        .inflate(R.menu.menu_pop_delete, popup.getMenu());

                /**删除逻辑*/
                popup.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.delete) {
                        String articleId = mData.get(position).getArticleId();
                        RealmDiaryDetailBean bean = Realm.getDefaultInstance()
                                .where(RealmDiaryDetailBean.class)
                                .equalTo("articleId", articleId).findFirst();
                        Realm.getDefaultInstance()
                                .executeTransaction(realm -> bean.deleteFromRealm());
                        mData.remove(position);
                        /**如果mData中没有数据则重新指向第一个数据*/
                        if (mData.size() == 0) {
                            Date firstDayOfMonth = calendarView.getFirstDayOfCurrentMonth();
                            calendarView.setCurrentDate(firstDayOfMonth);
                            tvWeek.setText(Utils.getWeek(firstDayOfMonth));
                            tvCurrentDayLong.setText(Utils.getStringDateShortSlash(firstDayOfMonth));
                            tvAdd.setVisibility(View.VISIBLE);

                            try {
                                tvAdd.setTag(getSbDate(firstDayOfMonth));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            initEvent(Utils.getYear(firstDayOfMonth), Utils.getMonth(firstDayOfMonth));
                            Realm realm = Realm.getDefaultInstance();
                            RealmResults<RealmDiaryDetailBean> diarys = realm
                                    .where(RealmDiaryDetailBean.class)
                                    .equalTo("year", Utils.getYear(firstDayOfMonth))
                                    .equalTo("month", Utils.getMonth(firstDayOfMonth))
                                    .equalTo("day", Utils.getDay(firstDayOfMonth)).findAll();
                            for (RealmDiaryDetailBean d : diarys) {
                                mData.add(getDiaryBrief(d));
                            }
                            if (mData.size() == 0) {
                                showBottomDateHint();
                            } else {
                                hideBottomHint();
                                adapter.notifyDataSetChanged();
                            }
                        }

                        adapter.notifyDataSetChanged();
                    }
                    return true;
                });
                popup.show();
            }
        });
    }

    private void showBottomMonthHint() {
        rlBottomHint.setVisibility(View.VISIBLE);
        tvBottomHint.setText("您本月还没有写过日记哦");
    }

    private void hideBottomHint() {
        rlBottomHint.setVisibility(View.GONE);
    }

    private void showBottomDateHint() {
        rlBottomHint.setVisibility(View.VISIBLE);
        tvBottomHint.setText("您当日还没有写过日记哦");
    }

    /**
     * init中与scroll中的不同在于：
     * init 初始化的日期是当前的日期，如果当前的月中没有日记则不设置currentData，
     * 有则设置当前日期再进行判断
     * scroll：如果当前的月中没有日记则不设置currentData，有则设置第一个
     */
    private void initRecycData() {

        adapter.currColor = currentColor;

        Realm realm = Realm.getDefaultInstance();
        RealmQuery<RealmDiaryDetailBean> diaryQuerys = realm
                .where(RealmDiaryDetailBean.class).equalTo("year",
                        currentYear)
                .equalTo("month", currentMonth);

        RealmResults<RealmDiaryDetailBean> diaryMonthBeens = diaryQuerys
                .findAll().sort("editTime", Sort.ASCENDING);


        /**如果没有结果，不设置第一天，设置recyc 当前月为空
         * 不应存在这种情况，因为初始化会添加一条*/
        if (diaryMonthBeens.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            showBottomMonthHint();
            tvAdd.setVisibility(View.GONE);
        }
        /**否则设置选择最早的日期
         *  刷新recycler
         */
        else {
            /**有结果则设置当前日期*/
            calendarView.setCurrentDate(new Date());
            tvAdd.setVisibility(View.VISIBLE);
            tvAdd.setTag(new Date());

            hideBottomHint();
            recyclerView.setVisibility(View.VISIBLE);
            RealmResults<RealmDiaryDetailBean> currentDayDiarys = diaryQuerys
                    .equalTo("day", currentDay).findAll()
                    .sort("editTime", Sort.ASCENDING);

            if (currentDayDiarys.size() == 0) {
                recyclerView.setVisibility(View.GONE);
                showBottomDateHint();
            } else {

                hideBottomHint();
                recyclerView.setVisibility(View.VISIBLE);
                mData.clear();

                for (RealmDiaryDetailBean diary : currentDayDiarys) {
                    DiaryBriefBean diaryBriefBean = getDiaryBrief(diary);
                    mData.add(diaryBriefBean);
                }
                adapter.notifyDataSetChanged();
            }
        }
        realm.close();
        /**都显示今天*/
        tvWeek.setText(Utils.getWeek(new Date()));
        tvCurrentDayLong.setText(Utils.getStringDateShortSlash(new Date()));
    }

    private void refreshHeader(String year, String month) {
        tvMonth.setText(month);
        tvYear.setText(year);
    }

    private void initEvent(int cuYear, int cuMonth) {
        calendarView.removeAllEvents();
        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmDiaryDetailBean> diaryBeans = realm
                .where(RealmDiaryDetailBean.class).equalTo("year", cuYear)
                .equalTo("month", cuMonth).findAll();
        int i = 0;
        for (RealmDiaryDetailBean diary : diaryBeans) {
            long editTime = diary.getEditTime();
            int color = 0;
            if (i == 0) {
                color = Color.argb(255, 169, 68, 65);
            } else {
                color = Color.argb(255, 100, 68, 65);
            }
            Event ev1 = new Event(color, editTime, "?");
            calendarView.addEvent(ev1, true);
            i++;
        }
        realm.close();
    }

    private DiaryBriefBean getDiaryBrief(RealmDiaryDetailBean bean) {
        DiaryBriefBean briefBean = new DiaryBriefBean();
        String diaryContent = bean.getContent();

        ContentJson contentBean = new Gson().fromJson(diaryContent,
                ContentJson.class);

        StringBuilder sb = new StringBuilder();

        for (Element e : contentBean.getElementList()) {
            if (e.getElementType() == RichTextEditor.TEXT) {
                sb.append(e.getContent());
            } else if (e.getElementType() == RichTextEditor.IMAGE) {
                if (sb.toString().equals("")) {
                    sb.append("图片");
                }

            } else if (e.getElementType() == RichTextEditor.GIF) {
                if (sb.toString().equals("")) {
                    sb.append("动图");
                }

            } else if (e.getElementType() == RichTextEditor.VIDEO) {
                if (sb.toString().equals("")) {
                    sb.append("视频");
                }
            }
        }
        briefBean.setArticleId(bean.getArticleId());
        briefBean.setBriefContent(String.valueOf(sb));
        briefBean.setDate(new Date(bean.getEditTime()));
        briefBean.setLocation(bean.getLocationStr());
        briefBean.setDay(String.valueOf(bean.getDay()));
        Date d = new Date();
        d.setTime(bean.getEditTime());
        briefBean.setWeek(Utils.getWeek(d));
        return briefBean;
    }

    public void initView(View view) {

        ImageView ivIndicator = (ImageView) view.findViewById(R.id.id_iv_indicator);

        GradientDrawable gd = (GradientDrawable) ivIndicator.getBackground();
        gd.setColor(currentColor);
        ivIndicator.setBackground(gd);

        rlBottomHint = (RelativeLayout) view.findViewById(R.id.id_rl_bottom_hint);
        dashedLineView = (DashedLineView) view.findViewById(R.id.id_dashline);
        tvWeek = (TextView) view.findViewById(R.id.id_tv_bottom_week);
        tvCurrentDayLong = (TextView) view.findViewById(R.id.id_tv_bottom_currentday_long);
        tvAdd = (TextView) view.findViewById(R.id.id_tv_add);

        tvBottomHint = (TextView) view.findViewById(R.id.id_tv_bottom_hint);

        leftButton = (Button) view.findViewById(R.id.id_bt_left);
        rightButton = (Button) view.findViewById(R.id.id_bt_right);

        recyclerView = (RecyclerView) view.findViewById(R.id.id_recyc);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setAdapter(adapter);
        rlBottomer = (RelativeLayout) view.findViewById(R.id.id_rl_bottom);
        rlBottomer.setBackgroundColor(currentColor);

        calendarView = (CompactCalendarView) view.findViewById(R.id.compactcalendar_view);

        calendarView.setLocale(TimeZone.getDefault(), Locale.CHINESE);
        calendarView.setUseThreeLetterAbbreviation(true);

        calendarView.shouldSelectFirstDayOfMonthOnScroll(false);

        calendarView.setTargetHeight((int) (Utils.getScreenWidth() * rotiaWidht * 1.7 * 0.55));

        calendarView.shouldDrawIndicatorsBelowSelectedDays(true);
        tvMonth = (TextView) view.findViewById(R.id.id_tv_month);
        tvYear = (TextView) view.findViewById(R.id.id_tv_year);
        leftButton = (Button) view.findViewById(R.id.id_bt_left);
        rightButton = (Button) view.findViewById(R.id.id_bt_right);
    }

}
