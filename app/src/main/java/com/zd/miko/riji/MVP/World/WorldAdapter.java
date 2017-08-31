package com.zd.miko.riji.MVP.World;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zd.miko.riji.Bean.ArticleWorldBean;
import com.zd.miko.riji.Bean.PreviewObj;
import com.zd.miko.riji.R;
import com.zd.miko.riji.Utils.Utils;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Miko on 2017/8/9.
 */

class WorldAdapter extends RecyclerView.Adapter<WorldAdapter.MyVH> {

    private ArrayList<ArticleWorldBean> mData;

    public WorldAdapter(ArrayList<ArticleWorldBean> mData) {
        this.mData = mData;
    }

    @Override
    public MyVH onCreateViewHolder(ViewGroup parent, int viewType) {

        /**缺省函数，parent不为null为true，null为false*/
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_world,
                parent,false);
        MyVH myVH = new MyVH(view);

        return myVH;
    }

    @Override
    public void onBindViewHolder(MyVH holder, int position) {
        ArticleWorldBean article = mData.get(position);
        holder.tvTitle.setText(article.getTitle());
        holder.tvBrief.setText(article.getContent());
        holder.tvAuthor.setText(article.getUserName());
        holder.tvDate.setText(Utils.getStringDateShort(new Date(article.getEditTime())));

        if (!"".equals(article.getContent())) {
            holder.tvBrief.setVisibility(View.VISIBLE);
            holder.tvBrief.setText(article.getContent());
        } else {
            holder.tvBrief.setVisibility(View.GONE);
        }

        if (article.getImagePaths().size() == 0) {
            holder.lnPreview.setVisibility(View.GONE);
        } else {
            holder.lnPreview.setVisibility(View.VISIBLE);

            holder.lnPreview.getChildAt(0).setVisibility(View.GONE);
            holder.lnPreview.getChildAt(1).setVisibility(View.GONE);
            holder.lnPreview.getChildAt(2).setVisibility(View.GONE);

            int i = 0;
            for (PreviewObj p : article.getImagePaths()) {
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

    public class MyVH extends RecyclerView.ViewHolder {

        TextView tvTitle, tvBrief, tvAuthor, tvDate;
        LinearLayout lnPreview;

        public MyVH(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.id_tv_title);
            tvBrief = (TextView) itemView.findViewById(R.id.id_tv_brief);
            tvAuthor = (TextView) itemView.findViewById(R.id.id_tv_author);
            tvDate = (TextView) itemView.findViewById(R.id.id_tv_date);
            lnPreview = (LinearLayout) itemView.findViewById(R.id.id_ln_pic_preview);
        }
    }
}
