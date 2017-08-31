package com.zd.miko.riji.MVP.World;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.baoyz.widget.PullRefreshLayout;
import com.google.gson.Gson;
import com.lapism.searchview.SearchView;
import com.zd.miko.riji.Adapter.SearchAdapter.SearchAdapter;
import com.zd.miko.riji.Bean.ArticleWorldBean;
import com.zd.miko.riji.Bean.ArticleWorldBrief;
import com.zd.miko.riji.Bean.ArticleWorldBriefs;
import com.zd.miko.riji.Bean.PreviewObj;
import com.zd.miko.riji.Bean.RealmBean.RealmArticleWorld;
import com.zd.miko.riji.MVP.Service.IRetrofit.IRetroGetArticleService;
import com.zd.miko.riji.R;
import com.zd.miko.riji.Utils.Utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * create by miko
 * 2017年1月24日
 */
public class WorldFragment extends Fragment {

    private int currentColor = randomColorFactory();

    private List<String> searchHisteryList;

    private List<String> temp_list;

    private SearchAdapter searchAdapter;

    private ArrayList<ArticleWorldBean> mData = new ArrayList<>();

    private SearchView searchView;

    private RecyclerView mRecycDiary;

    private WorldAdapter adapter = new WorldAdapter(mData);

    private LinearLayout linearLayout;

    private RecyclerView recyclerView;
    private ListView listView;
    private CardView cardView;
    private PullRefreshLayout refreshLayout;

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
        View view = inflater.inflate(R.layout.fragment_world, container, false);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmArticleWorld> realmArticleWorlds = realm
                .where(RealmArticleWorld.class).findAll();
        for(RealmArticleWorld r : realmArticleWorlds){
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    r.deleteFromRealm();
                }
            });
        }
        realm.close();


        initData();
        initView(view);
        initRecycData();

        initStatusBar();
        initEvent();
        view.setBackgroundColor(currentColor);

        return view;
    }

    private void initRecycData() {
        mRecycDiary.setAdapter(adapter);
        mRecycDiary.setLayoutManager(new LinearLayoutManager(this.getContext()));

        /**从数据库中初始化数据*/
        Realm realm = Realm.getDefaultInstance();

        RealmResults<RealmArticleWorld> realmArticlesPushed = realm
                .where(RealmArticleWorld.class)
                .equalTo("userId", Utils.getUserAccount())
                .findAll();

        for (RealmArticleWorld realmBean : realmArticlesPushed) {

            ArticleWorldBean articleWorldBean = new ArticleWorldBean();

            articleWorldBean.setUserName(realmBean.getUserName());
            articleWorldBean.setUserId(realmBean.getUserId());
            articleWorldBean.setEditTime(realmBean.getShareTime());
            articleWorldBean.setContent(realmBean.getContent());
            articleWorldBean.setTitle(realmBean.getTitle());

            ArrayList<PreviewObj> previewObjs = new ArrayList<>();
            String[] pathArray = realmBean.getImagePaths().split(" ");
            for (String p : pathArray) {
                String[] pInfo = p.split("_");
                String url = getString(R.string.host) + "img/"
                        + realmBean.getArticleId() + "/" + p + ".cvv";
                PreviewObj previewObj;
                if (pInfo[0].contains("image")) {
                    previewObj = new PreviewObj(0, url);
                } else if (pInfo[0].contains("gif")) {
                    previewObj = new PreviewObj(0, url);
                } else {
                    previewObj = new PreviewObj(1, url);
                }
                previewObjs.add(previewObj);
            }
            articleWorldBean.setImagePaths(previewObjs);
            articleWorldBean.setArticleId(realmBean.getArticleId());

            mData.add(0,articleWorldBean);
        }
        adapter.notifyDataSetChanged();

        realm.close();

        // start refresh
        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(getString(R.string.host))
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        IRetroGetArticleService service = retrofit
                .create(IRetroGetArticleService.class);
        service.getArticle(getString(R.string.getArticle), Utils.getUserAccount(), false).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                String res = response.body();

                pressRefreshInfo(res);

                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

//                Log.i("xyz", String.valueOf(t));
                refreshLayout.setRefreshing(false);

            }
        });

    }


    private void pressRefreshInfo(String res){
        try {
            String resDecode = URLDecoder.decode(res, "UTF-8");
            Log.i("xyz", resDecode);

            ArticleWorldBriefs articleWorldBriefs = new Gson()
                    .fromJson(resDecode, ArticleWorldBriefs.class);
            for (ArticleWorldBrief articleNet :
                    articleWorldBriefs.getArticleWorldBriefs()) {
                ArticleWorldBean articleBean = new ArticleWorldBean();
                articleBean.setArticleId(articleNet.getArticleId());
                articleBean.setTitle(articleNet.getTitle());
                articleBean.setContent(articleNet.getContent());
                articleBean.setEditTime(articleNet.getShareTime());
                articleBean.setUserId(articleNet.getUserId());
                articleBean.setUserName(articleNet.getUserName());
                ArrayList<PreviewObj> previewObjs = new ArrayList<>();
                String[] pathArray = articleNet.getPreviewPaths().split(" ");
                for (String p : pathArray) {
                    String[] pInfo = p.split("_");
                    String url = getString(R.string.host) + "img/"
                            + articleNet.getArticleId() + "/" + p + ".cvv";
                    PreviewObj previewObj;
                    if (pInfo[0].contains("image")) {
                        previewObj = new PreviewObj(0, url);
                    } else if (pInfo[0].contains("gif")) {
                        previewObj = new PreviewObj(0, url);
                    } else {
                        previewObj = new PreviewObj(1, url);
                    }
                    previewObjs.add(previewObj);
                }
                articleBean.setImagePaths(previewObjs);
                mData.add(0,articleBean);
            }
            adapter.notifyDataSetChanged();

            /**存储到Realm*/
            Realm r = Realm.getDefaultInstance();
            r.executeTransaction(realm1 -> {
                for (ArticleWorldBrief articleNet : articleWorldBriefs.getArticleWorldBriefs()) {
                    RealmArticleWorld realmArticle = realm1
                            .createObject(RealmArticleWorld.class);
                    realmArticle.setUserName(articleNet.getUserName());
                    realmArticle.setArticleId(articleNet.getArticleId());
                    realmArticle.setUserId(articleNet.getUserId());
                    realmArticle.setContent(articleNet.getContent());
                    realmArticle.setImagePaths(articleNet.getPreviewPaths());
                    realmArticle.setShareTime(articleNet.getShareTime());
                    realmArticle.setReadUserId(Utils.getUserAccount());
                    realmArticle.setTitle(articleNet.getTitle());
                    realmArticle.setHadRead(false);
                }
            });
            r.close();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    private void initStatusBar() {
        RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) searchView.getLayoutParams();
        param.setMargins(0, Utils.getStatusBarHeight(), 0, 0);
        searchView.setLayoutParams(param);
    }

    private void initData() {
        //TODO sp中获取数据
        searchHisteryList = new ArrayList<>();
        searchHisteryList.add("aaaadf");
        searchHisteryList.add("aaaadsfaadf");

        temp_list = searchHisteryList;
        searchAdapter = new SearchAdapter(searchHisteryList);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void initView(View view) {
        refreshLayout = (PullRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
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
        refreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {


                adapter.notifyDataSetChanged();

                // start refresh
                Retrofit retrofit = new Retrofit
                        .Builder()
                        .baseUrl(getString(R.string.host))
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .build();

                IRetroGetArticleService service = retrofit
                        .create(IRetroGetArticleService.class);
                service.getArticle(getString(R.string.getArticle), Utils.getUserAccount(), true).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        String res = response.body();
                        pressRefreshInfo(res);

                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.i("xyz", t.getMessage());
                        refreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }

    public void initSearchList() {

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
