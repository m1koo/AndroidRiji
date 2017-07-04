package com.zd.miko.riji.Adapter.SearchAdapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<MyVH>{

    private List<String> temp_list;

    private List<String> origin_list;

    public SearchAdapter(List<String> list) {
        this.origin_list = list;
        this.temp_list = list;
    }


    @Override
    public MyVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyVH(View.inflate(parent.getContext(), android.R.layout.simple_list_item_1
                , null));
    }

    @Override
    public void onBindViewHolder(MyVH holder, int position) {
        holder.textView.setText(temp_list.get(position));
    }


    @Override
    public int getItemCount() {
        return temp_list.size();
    }



}

class MyVH extends RecyclerView.ViewHolder {
    TextView textView;

    public MyVH(View itemView) {
        super(itemView);
        textView = (TextView) itemView.findViewById(android.R.id.text1);
    }
}
