package com.zd.miko.riji.MVP.Main;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toolbar;

import com.zd.miko.riji.R;
import com.zd.miko.riji.Utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class MFragment extends Fragment {

    private ImageView header;
    private Toolbar toolbar;

    public MFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_m, container, false);
        header = (ImageView) view.findViewById(R.id.id_header);
        toolbar = (Toolbar) view.findViewById(R.id.id_toolbar);
        ViewGroup.LayoutParams headerParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dpToPx(56)
                + Utils.getStatusBarHeight());
        toolbar.setLayoutParams();
        return view;
    }

}
