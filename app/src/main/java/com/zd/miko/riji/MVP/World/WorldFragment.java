package com.zd.miko.riji.MVP.World;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.lapism.searchview.SearchView;
import com.zd.miko.riji.Adapter.SearchAdapter.SearchAdapter;
import com.zd.miko.riji.R;

import java.util.ArrayList;
import java.util.List;

/**
 * create by miko
 * 2017年1月24日
 */
public class WorldFragment extends Fragment {

    private List<String> searchHisteryList;

    private List<String> temp_list;

    private SearchAdapter searchAdapter;


    private SearchView searchView;
    private RecyclerView mRecycDiary;
    private LinearLayout linearLayout;
    private RecyclerView recyclerView;
    private ListView listView;
    private CardView cardView;

    public WorldFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        initData();
        initView(view);

        return view;
    }

    private void initData() {
        //TODO sp中获取数据
        searchHisteryList = new ArrayList<>();
        searchHisteryList.add("aaaadf");
        searchHisteryList.add("aaaadsfaadf");
        searchHisteryList.add("aafgrtweraadf");
        searchHisteryList.add("aaaqqqadf");
        searchHisteryList.add("aaaayyydf");
        searchHisteryList.add("aaaarrdf");
        searchHisteryList.add("aayuuuaadf");

        temp_list = searchHisteryList;
        searchAdapter = new SearchAdapter(searchHisteryList);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void initView(View view) {
        mRecycDiary = (RecyclerView) view.findViewById(R.id.id_rcyc_diarys);
        searchView = (SearchView) view.findViewById(R.id.searchView);
        searchView.setAdapter(searchAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //TODO 搜索 添加搜索记录
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchAdapter.notifyDataSetChanged();
                return false;
            }
        });
    }

    public void initEvent() {

    }

    public void initSearchList() {

    }


    class MyFilter extends Filter {

        List<String> filteredList = new ArrayList<>();

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null && constraint.length() != 0) {
                for (String s : searchHisteryList) {
                    if (s.contains(constraint)) {
                        filteredList.add(s);
                    }
                }
            } else {
                filteredList.addAll(searchHisteryList);
            }

            FilterResults results = new FilterResults();
            results.count = filteredList.size();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.count == 0) {
                temp_list = new ArrayList<>();
                temp_list.add("没有符合条件的历史记录");
                searchAdapter.notifyDataSetChanged();
            } else {
                Log.i("xyz", String.valueOf(results.count));
                searchAdapter.notifyDataSetChanged();
            }
        }
    }
}
