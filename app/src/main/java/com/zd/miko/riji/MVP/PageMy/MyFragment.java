package com.zd.miko.riji.MVP.PageMy;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zd.miko.riji.R;
import com.zd.miko.riji.R2;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyFragment extends Fragment {

    public MyFragment() {
        // Required empty public constructor
    }


    @OnClick(R2.id.id_ln_myfavorite) void favoriteClick() {

    }

    @OnClick(R2.id.id_ln_setting) void settingClick() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ButterKnife.bind(this.getActivity());
        return inflater.inflate(R.layout.fragment_my, container, false);
    }
}
