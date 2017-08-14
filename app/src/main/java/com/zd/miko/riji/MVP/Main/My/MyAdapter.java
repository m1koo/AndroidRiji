package com.zd.miko.riji.MVP.Main.My;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zd.miko.riji.Bean.DiaryBriefBean;
import com.zd.miko.riji.Bean.PreviewObj;
import com.zd.miko.riji.R;
import com.zd.miko.riji.Utils.Utils;

import java.util.ArrayList;

/**
 * Created by Miko on 2017/7/7.
 */

public class MyAdapter extends RecyclerView.Adapter<MyVH> {

    ObjectAnimator objectAnimator;
    public View currentShakeView = null;

    public int topColor;

    public interface OnDiaryClick {
        void onShortClick(int position, MyVH holder);

        void onLongClick(int position, MyVH holder);
    }


    private final int HEADER = 0;
    private final int OTHER = 1;
    private OnDiaryClick diaryClick;

    public int getTopColor() {
        return topColor;
    }

    public void setTopColor(int topColor) {
        this.topColor = topColor;
    }

    public void setDiaryClick(OnDiaryClick diaryClick) {
        this.diaryClick = diaryClick;
    }

    private ArrayList<DiaryBriefBean> mData;

    public MyAdapter(ArrayList<DiaryBriefBean> mData) {
        this.mData = mData;
    }

    @Override
    public MyVH onCreateViewHolder(ViewGroup parent, int viewType) {
        MyVH vh = new MyVH(LayoutInflater.from(
                parent.getContext()).inflate(R.layout.home_recyc_item, parent,
                false));
        if (viewType == OTHER) {
            vh.rlIndex.setVisibility(View.INVISIBLE);
            vh.cardView.setCardBackgroundColor(Color.parseColor("#718579"));
        } else {
            vh.rlIndex.setVisibility(View.VISIBLE);
            vh.cardView.setCardBackgroundColor(topColor);
        }

        return vh;
    }


    @Override
    public void onBindViewHolder(MyVH holder, int position) {

        /**设置触摸响应*/
        holder.itemView.setOnClickListener(v -> {
            diaryClick.onShortClick(holder.getLayoutPosition(), holder);
        });

        holder.itemView.setOnLongClickListener(v -> {

            diaryClick.onLongClick(holder.getLayoutPosition(), holder);
            return true;
        });

        DiaryBriefBean diary = mData.get(position);
        holder.tvLocation.setText(diary.getLocation());
        holder.tvDay.setText(diary.getDay());
        holder.tvWeek.setText(diary.getWeek());
        holder.tvDate.setText(Utils.getStringDateLong(diary.getDate()));
        if (!"".equals(diary.getBriefContent())) {
            holder.tvBrief.setVisibility(View.VISIBLE);
            holder.tvBrief.setText(diary.getBriefContent());
        } else {
            holder.tvBrief.setVisibility(View.GONE);
        }

        /**如果么没有图片设置容器为空*/

        if (diary.getPreviewPaths().size() == 0) {
            holder.lnPreview.setVisibility(View.GONE);
        } else {
            holder.lnPreview.setVisibility(View.VISIBLE);

            holder.lnPreview.getChildAt(0).setVisibility(View.GONE);
            holder.lnPreview.getChildAt(1).setVisibility(View.GONE);
            holder.lnPreview.getChildAt(2).setVisibility(View.GONE);

            int i = 0;
            for (PreviewObj p : diary.getPreviewPaths()) {
                if (i == 3) {
                    break;
                } else {
                    ImageView im = (ImageView) holder.lnPreview.getChildAt(i);
                    im.setVisibility(View.VISIBLE);
                    Glide.with(holder.itemView.getContext())
                            .load(p.path).into(im);
                    i++;
                }
            }

        }
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? HEADER : OTHER;
    }
}

class MyVH extends RecyclerView.ViewHolder {

    TextView tvDate, tvWeek, tvDay, tvBrief, tvLocation;

    RelativeLayout rlIndex;

    CardView cardView;

    LinearLayout lnPreview;

    public MyVH(View itemView) {
        super(itemView);
        cardView = (CardView) itemView.findViewById(R.id.item_card);
        tvLocation = (TextView) itemView.findViewById(R.id.id_tv_location);
        tvBrief = (TextView) itemView.findViewById(R.id.id_tv_brief);
        tvDate = (TextView) itemView.findViewById(R.id.id_tv_date);
        tvDay = (TextView) itemView.findViewById(R.id.id_tv_day);
        tvWeek = (TextView) itemView.findViewById(R.id.id_tv_week);
        rlIndex = (RelativeLayout) itemView.findViewById(R.id.id_rl_red_index);
        lnPreview = (LinearLayout) itemView.findViewById(R.id.id_ln_pic_preview);
    }
}
