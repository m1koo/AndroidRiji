package com.zd.miko.riji.Adapter;

/**
 * Created by Miko on 2017/1/30.
 */
//
//public class DiarysRcAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//
//    private static int HEAD = 0;
//    private static int NORMAL = 1;
//    private ArrayList<DiaryBaseMsg> diaryList;
//
//    public DiarysRcAdapter(ArrayList<DiaryBaseMsg> diaryList) {
//        this.diaryList = diaryList;
//    }
//
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        if(viewType == HEAD){
//            return new FootVH(LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.item_diarys_head,parent,false));
//        }
//        else{
//            return new NormalVH(LayoutInflater.from(parent.getContext())
//            .inflate(R.layout.item_diarys_normal,parent,false));
//        }
//    }
//
//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return diaryList.size();
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        return (position == 0)?HEAD:NORMAL;
//    }
//
//    private class FootVH extends RecyclerView.ViewHolder {
//        public FootVH(View inflate) {
//            super(inflate);
//        }
//    }
//
//    private class NormalVH extends RecyclerView.ViewHolder {
//        public NormalVH(View inflate) {
//            super(inflate);
//        }
//    }
//}
