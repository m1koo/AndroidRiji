package com.zd.miko.riji.MVP.Main.My;

import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zd.miko.riji.Bean.DiaryBriefBean;
import com.zd.miko.riji.R;
import com.zd.miko.riji.Utils.Utils;

import java.util.ArrayList;

/**
 * Created by Miko on 2017/8/4.
 */

public class MyBottomAdapter extends RecyclerView.Adapter<MyBottomAdapter.MyBottomVH> {

    public interface OnItemClick {
        void itemClick(int position);

        void itemLongClick(int position, MyBottomAdapter.MyBottomVH holder);
    }

    private OnItemClick onItemClick;

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    private ArrayList<DiaryBriefBean> mData;

    public int currColor;

    public MyBottomAdapter(ArrayList<DiaryBriefBean> mData) {
        this.mData = mData;
    }

    @Override
    public MyBottomVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyBottomVH(LayoutInflater.from(
                parent.getContext()).inflate(R.layout.item_calendar_bottom, parent,
                false));
    }

    @Override
    public void onBindViewHolder(MyBottomVH holder, int position) {
        holder.tvBrief.setText(mData.get(position).getBriefContent());
        holder.tvLocation.setText(mData.get(position).getLocation());

        holder.tvTime.setText(Utils.getHourMin(mData.get(position).getDate()));
        holder.itemView.setOnClickListener(v -> onItemClick
                .itemClick(holder.getLayoutPosition()));

        holder.itemView.setOnLongClickListener(v -> {
            onItemClick.itemLongClick(holder.getLayoutPosition(), holder);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class MyBottomVH extends RecyclerView.ViewHolder {

        TextView tvBrief, tvLocation, tvTime;

        ImageView ivIndicator;

        public MyBottomVH(View itemView) {
            super(itemView);
            ivIndicator = (ImageView) itemView.findViewById(R.id.id_iv_indicator);

            GradientDrawable gd = (GradientDrawable) ivIndicator.getBackground();
            gd.setColor(currColor);
            ivIndicator.setBackground(gd);
            tvBrief = (TextView) itemView.findViewById(R.id.id_tv_brief);
            tvLocation = (TextView) itemView.findViewById(R.id.id_tv_location);
            tvTime = (TextView) itemView.findViewById(R.id.id_tv_time);
        }
    }
}
