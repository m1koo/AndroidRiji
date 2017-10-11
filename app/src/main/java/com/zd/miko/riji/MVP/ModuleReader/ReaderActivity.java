package com.zd.miko.riji.MVP.ModuleReader;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zd.miko.riji.Bean.ContentJson;
import com.zd.miko.riji.Bean.Element;
import com.zd.miko.riji.Bean.RealmBean.RealmDiaryDetailBean;
import com.zd.miko.riji.CustomView.RichEditText.RichTextEditor;
import com.zd.miko.riji.MVP.ModuleEditor.ImagePreviewActivity;
import com.zd.miko.riji.MVP.Service.ShareService;
import com.zd.miko.riji.MyApp;
import com.zd.miko.riji.R;
import com.zd.miko.riji.Utils.Utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import io.realm.Realm;

public class ReaderActivity extends AppCompatActivity {

    private Dialog dialog = null;

    private TextView tvLocation;
    private RichTextEditor richTextEditor;
    private Toolbar toolbar;
    private int currentColor;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyApp.push(this);

        setContentView(R.layout.activity_reader);
        initView();

        ViewTreeObserver vto2 = richTextEditor.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                richTextEditor.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                id = getIntent().getStringExtra("articleId");

                Realm realm = Realm.getDefaultInstance();

                RealmDiaryDetailBean detailBean = realm
                        .where(RealmDiaryDetailBean.class)
                        .equalTo("articleId", id)
                        .findFirst();

                tvLocation.setText(detailBean.getLocationStr());

                toolbar.setTitle(detailBean.getYear() + "年"
                        + detailBean.getMonth() + "月" + detailBean.getDay() + "日");

                String unDecodeContentJson = detailBean.getContent();

                String contentJson = null;

                try {
                    contentJson = URLDecoder.decode(unDecodeContentJson, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                ContentJson contentBean = new Gson().fromJson(contentJson,
                        ContentJson.class);

                int imageIndex = 0;
                int videoIndex = 0;
                int gifIndex = 0;

                String rootPath = Environment.getExternalStorageDirectory() + "/meiriji/" + id;

                for (Element e : contentBean.getElementList()) {
                    if (e.getElementType() == RichTextEditor.TEXT) {
                        richTextEditor.insertReadText(e.getContent());
                    } else if (e.getElementType() == RichTextEditor.IMAGE) {
                        richTextEditor.insertReadImage(rootPath + "/image_" + imageIndex + ".cvv");
                        imageIndex++;
                    } else if (e.getElementType() == RichTextEditor.GIF) {
                        richTextEditor.insertReadGif(rootPath + "/gif_" + gifIndex + ".cvv");
                        gifIndex++;
                    } else if (e.getElementType() == RichTextEditor.VIDEO) {
                        richTextEditor.insertReadVideo(rootPath + "/video_" + videoIndex + ".cvv");
                        videoIndex++;
                    }
                }
            }
        });
        initEvent();
    }

    private void initEvent() {
        richTextEditor.setOnImageClickListener((path, type) -> {
            Intent intent = new Intent(this, ImagePreviewActivity.class);
            intent.putExtra("path", path);
            intent.putExtra("type", type);
            startActivity(intent);
        });
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            MyApp.pop();
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_world, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
            View view = LayoutInflater.from(this).inflate(R.layout.dialog_share, null);


            dialog = new AlertDialog.Builder(this)
                    .setView(view).create();
            EditText editText = (EditText) view.findViewById(R.id.id_ed_title);

            view.findViewById(R.id.id_bt_dialog_done_positive).setOnClickListener(v -> {
                String title = editText.getText().toString();
                Log.i("xyz", title);
                ShareService.startService(MyApp.context, id,
                        title);
                dialog.dismiss();
            });
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MyApp.pop();
    }

    private void initView() {
        tvLocation = (TextView) findViewById(R.id.id_tv_location_bottom);
        richTextEditor = (RichTextEditor) findViewById(R.id.id_richedit_nativereader);
        toolbar = (Toolbar) findViewById(R.id.id_toolbar);


        currentColor = getIntent().getIntExtra("currentColor"
                , Color.parseColor("#ba0001"));
        toolbar.setBackgroundColor(currentColor);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setTransitionName("itemShare");
        }
        ViewGroup decorViewGroup = (ViewGroup) getWindow().getDecorView();
        View statusBarView = new View(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                Utils.getStatusBarHeight());

        params.gravity = Gravity.TOP;
        statusBarView.setLayoutParams(params);
        statusBarView.setBackgroundColor(currentColor);
        decorViewGroup.addView(statusBarView);
    }
}
