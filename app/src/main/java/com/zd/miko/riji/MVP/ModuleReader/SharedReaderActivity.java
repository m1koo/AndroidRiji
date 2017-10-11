package com.zd.miko.riji.MVP.ModuleReader;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.google.gson.Gson;
import com.zd.miko.riji.Bean.ArticleBean;
import com.zd.miko.riji.Bean.ContentJson;
import com.zd.miko.riji.Bean.DtoCommentBean;
import com.zd.miko.riji.Bean.DtoCommentList;
import com.zd.miko.riji.Bean.Element;
import com.zd.miko.riji.CustomView.RichEditText.RichTextEditor;
import com.zd.miko.riji.MVP.ModuleEditor.ImagePreviewActivity;
import com.zd.miko.riji.MyApp;
import com.zd.miko.riji.R;
import com.zd.miko.riji.Utils.RetrofitParameterBuilder;
import com.zd.miko.riji.Utils.Utils;

import java.util.ArrayList;

public class SharedReaderActivity extends AppCompatActivity {

    private final static String TAG = "SharedReaderActivity";
    private Dialog dialog = null;

    //    private TextView tvLocation;
    private MaterialFavoriteButton favoriteButton;
    private RichTextEditor richTextEditor;
    private Toolbar toolbar;
    private int currentColor;
    private String articleId;
    private Button btCommentComplete;
    /**
     * 正在编辑的标志
     */
    private boolean isEditing = false;
    private EditText edComment;
    private RelativeLayout rlScrollContainer;
    private RelativeLayout rlBottomEditing;
    private RelativeLayout rlCommentUnEditable;
    private RelativeLayout rlBottom;
    private ArrayList<DtoCommentBean> mCommentData = new ArrayList<>();
    private CommentAdapter commentAdapter = new CommentAdapter(mCommentData);
    private RecyclerView recycComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyApp.push(this);

        setContentView(R.layout.activity_share_reader);

        articleId = getIntent().getStringExtra("articleId");

        currentColor = getIntent().getIntExtra("currentColor", Color.BLUE);

        if (articleId == null || articleId.equals("")) {
            return;
        }
        initView();

        initArticleData(articleId);

        getFavoriteState();

        refreshComment();

        initEvent();
    }


    private void initArticleData(String articleId) {
        RetrofitParameterBuilder builder = RetrofitParameterBuilder.newBuilder();

        builder.addParameter("articleId", articleId);

        Utils.normalPost(builder, getString(R.string.get_article_by_id), new Utils.PostCall() {
            @Override
            public void onSuccess(String response) {
                if (response == null || response.equals("")) {
                    Log.i(TAG, "response.body()" + " null");
                } else {
                    ArticleBean articleBean = new Gson().fromJson(response,
                            ArticleBean.class);
                    if (articleBean == null) {
                        Log.i(TAG, "article gson bean is null");
                        return;
                    }
                    parseArticle(articleBean);

                    setRecyclerViewPosition();

                }
            }

            @Override
            public void onFail(String error) {

            }
        });
    }

    private void setRecyclerViewPosition() {
        float richViewHeight = richTextEditor.getHeight();

        float rlScrollContainerHeight = rlScrollContainer.getHeight();
        float scale = (float) (3.0 / 4.0);

        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        /**如果内容较少，设置marginTop*/
        if (richViewHeight / rlScrollContainerHeight < scale) {
            Log.i(TAG, String.valueOf(richViewHeight / rlScrollContainerHeight));
            param.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            param.setMargins(0, (int) (rlScrollContainerHeight * scale), 0, 0);
            recycComment.setLayoutParams(param);
        }
    }

    private void parseArticle(ArticleBean article) {
        String contentJson = article.getContent();

        ContentJson contentBean = new Gson().fromJson(contentJson,
                ContentJson.class);

        int imageIndex = 0;
        int videoIndex = 0;
        int gifIndex = 0;

        String rootPath = getString(R.string.host) + "img/" + article.getArticleId();

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

    private void getFavoriteState() {
        RetrofitParameterBuilder builder = RetrofitParameterBuilder.newBuilder();

        builder.addParameter("articleId", articleId);
        builder.addParameter("userId", Utils.getUserAccount());

        Utils.normalPost(builder, getString(R.string.string_get_favorite), new Utils.PostCall() {
            @Override
            public void onSuccess(String response) {
                if (response == null || response.equals("")) {
                    Log.i(TAG, "getFavoriteState response null ");
                    return;
                }

                boolean state = Boolean.valueOf(response);
                favoriteButton.setFavorite(state);

            }

            @Override
            public void onFail(String error) {

            }
        });

    }

    private void changeFavoriteState(boolean isFavorite) {
        RetrofitParameterBuilder builder = RetrofitParameterBuilder.newBuilder();

        builder.addParameter("articleId", articleId);
        builder.addParameter("isFavorite", String.valueOf(isFavorite));
        builder.addParameter("userId", Utils.getUserAccount());

        Utils.normalPost(builder, getString(R.string.string_change_favorite), new Utils.PostCall() {
            @Override
            public void onSuccess(String response) {
                if (response == null || response.equals("")) {
                    Log.i(TAG, "change favoriteState response null ");
                    return;
                }
            }

            @Override
            public void onFail(String error) {

            }
        });
    }

    private void initEvent() {
        favoriteButton.setOnFavoriteChangeListener((buttonView, favorite) -> {
            changeFavoriteState(favorite);
        });
        btCommentComplete.setOnClickListener(v -> {
            String commentStr = edComment.getText().toString();
            if (commentStr == null) {
                return;
            }
            /**清除空格*/
            String parseCommentStr = commentStr.replace(" ", "");
            if (parseCommentStr.equals("")) {
                Toast.makeText(this, "您的评论内容为空", Toast.LENGTH_SHORT).show();
                return;
            }

            RetrofitParameterBuilder builder = RetrofitParameterBuilder.newBuilder();
            builder.addParameter("articleId", articleId);
            builder.addParameter("userId", Utils.getUserAccount());
            builder.addParameter("commentStr", parseCommentStr);

            Utils.normalPost(builder, getString(R.string.add_comment), new Utils.PostCall() {
                @Override
                public void onSuccess(String response) {
                    if (response == null || response.equals("")) {
                        Log.i(TAG, "post comment response null ");
                        return;
                    }
                    Log.i(TAG, "post comment response " + response);

                    refreshComment();
                }

                @Override
                public void onFail(String error) {
                    Toast.makeText(SharedReaderActivity.this, "网络错误",
                            Toast.LENGTH_SHORT).show();
                }
            });
        });

        rlCommentUnEditable.setOnClickListener(v -> {
            rlBottom.setVisibility(View.INVISIBLE);
            rlBottomEditing.setVisibility(View.VISIBLE);
            edComment.setFocusable(true);
            edComment.setFocusableInTouchMode(true);
            edComment.requestFocus();//
            InputMethodManager inputManager = (InputMethodManager) this
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(edComment, 0);

            isEditing = true;
        });

        richTextEditor.setOnClickListener(v -> {
            if (isEditing) {
                isEditing = false;
                rlBottom.setVisibility(View.VISIBLE);
                rlBottomEditing.setVisibility(View.INVISIBLE);
                edComment.setText("");
                edComment.clearFocus();
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edComment.getWindowToken(), 0);
            }
        });

        commentAdapter.setListener(articleId1 -> {
            if (isEditing) {
                isEditing = false;
                rlBottom.setVisibility(View.VISIBLE);
                rlBottomEditing.setVisibility(View.INVISIBLE);
                edComment.setText("");
                edComment.clearFocus();
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edComment.getWindowToken(), 0);
            }
        });


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

    private void refreshComment() {

        // TODO: 2017/9/15 开启pregress等待

        RetrofitParameterBuilder builder = RetrofitParameterBuilder.newBuilder();
        builder.addParameter("articleId", articleId);
        Utils.normalPost(builder, getString(R.string.get_article_comments), new Utils.PostCall() {
            @Override
            public void onSuccess(String response) {
                if (response == null || response.equals("")) {
                    Log.i(TAG, "response.body()" + " null");
                } else {
                    Log.i(TAG, response);

                    DtoCommentList dtoCommentList = new Gson().fromJson(response,
                            DtoCommentList.class);

                    if (dtoCommentList == null) {
                        Log.i(TAG, "dtoCommentList Gson error");
                        return;
                    }

                    /**清除所有评论*/
                    mCommentData.clear();
                    mCommentData.addAll(dtoCommentList.getCommentList());

                    commentAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFail(String error) {

            }
        });
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
//        getMenuInflater().inflate(R.menu.menu_world, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.action_share) {
//            View view = LayoutInflater.from(this).inflate(R.layout.dialog_share, null);
//
//
//            dialog = new AlertDialog.Builder(this)
//                    .setView(view).create();
//            EditText editText = (EditText) view.findViewById(R.id.id_ed_title);
//
//            view.findViewById(R.id.id_bt_dialog_done_positive).setOnClickListener(v -> {
//                String title = editText.getText().toString();
//                Log.i(TAG, title);
//                ShareService.startService(MyApp.context, id,
//                        title);
//                dialog.dismiss();
//            });
//            dialog.show();
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MyApp.pop();
    }

    private void initView() {
//        tvLocation = (TextView) findViewById(R.id.id_tv_location_bottom);
        richTextEditor = (RichTextEditor) findViewById(R.id.id_richedit_nativereader);
        toolbar = (Toolbar) findViewById(R.id.id_toolbar);

        favoriteButton = (MaterialFavoriteButton) findViewById(R.id.id_bt_favorite);
        btCommentComplete = (Button) findViewById(R.id.id_bt_comment_complete);

        rlBottom = (RelativeLayout) findViewById(R.id.id_rl_bottom);
        rlBottomEditing = (RelativeLayout) findViewById(R.id.id_rl_bottom_editing);
        rlCommentUnEditable = (RelativeLayout) findViewById(R.id.id_rl_uneditable);

        rlScrollContainer = (RelativeLayout) findViewById(R.id.id_rl_scroll_container);

        edComment = (EditText) findViewById(R.id.id_ed_comment);

        recycComment = (RecyclerView) findViewById(R.id.id_recyc_comment);

        recycComment.setAdapter(commentAdapter);
        recycComment.setLayoutManager(new LinearLayoutManager(this));

        recycComment.setNestedScrollingEnabled(false);

        commentAdapter.notifyDataSetChanged();

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
