package com.zd.miko.riji.MVP.ModuleReader;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zd.miko.riji.Bean.DtoCommentBean;
import com.zd.miko.riji.R;
import com.zd.miko.riji.Utils.Utils;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Miko on 2017/8/9.
 */

class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyVH> {

    public interface OnItemClickListener {
        void onClickListener(String articleId);
    }

    private OnItemClickListener listener;

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    private ArrayList<DtoCommentBean> mData;

    public CommentAdapter(ArrayList<DtoCommentBean> mData) {
        this.mData = mData;
    }

    @Override
    public MyVH onCreateViewHolder(ViewGroup parent, int viewType) {

        /**缺省函数，parent不为null为true，null为false*/
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment,
                parent, false);
        MyVH myVH = new MyVH(view);

        return myVH;
    }

    @Override
    public void onBindViewHolder(MyVH holder, int position) {

        DtoCommentBean dtoCommentBean = mData.get(position);
        holder.tvUserName.setText(dtoCommentBean.getUserName());
        holder.tvCommentStr.setText(dtoCommentBean.getCommentStr());
        holder.tvDate.setText(Utils.getCommentStringDateLong(
                new Date(dtoCommentBean.getCommentTime())));
        // TODO: 2017/9/15  glide 头像
        holder.itemView.setOnClickListener(v -> {
            if(listener!=null){
                listener.onClickListener(dtoCommentBean.getArticleId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyVH extends RecyclerView.ViewHolder {

        CircleImageView circleImageView;
        TextView tvUserName, tvCommentStr, tvDate;

        public MyVH(View itemView) {
            super(itemView);
            tvUserName = (TextView) itemView.findViewById(R.id.id_tv_userName);
            tvCommentStr = (TextView) itemView.findViewById(R.id.id_tv_comment_str);
            tvDate = (TextView) itemView.findViewById(R.id.id_tv_date);
            circleImageView = (CircleImageView) itemView.findViewById(R.id.id_head_icon);
        }
    }
}
