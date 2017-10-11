package com.zd.miko.riji.MVP.ModuleEditor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.zd.miko.riji.CustomView.RichEditText.RichTextEditor;
import com.zd.miko.riji.R;

import java.io.IOException;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Miko on 2017/7/18.
 */

public class ImagePreviewActivity extends AppCompatActivity {

    GifImageView gifView;
    ImageView jpegView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_image_preview);

        jpegView = (ImageView) findViewById(R.id.id_center_image);
        gifView = (GifImageView) findViewById(R.id.id_gif);

        findViewById(R.id.id_rl_back).setOnClickListener(v -> finish());

        String path = getIntent().getStringExtra("path");
        int imageType = getIntent().getIntExtra("type", 0);

        if (imageType == RichTextEditor.IMAGE) {
            if (path != null) {
                Bitmap bm = BitmapFactory.decodeFile(path);
                jpegView.setImageBitmap(bm);
            }
        }else if(imageType == RichTextEditor.GIF){
            jpegView.setVisibility(View.GONE);
            gifView.setVisibility(View.VISIBLE);
            try {
                GifDrawable gifFromPath = new GifDrawable(path);
                gifView.setImageDrawable(gifFromPath);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else if(imageType == RichTextEditor.VIDEO){
            jpegView.setVisibility(View.GONE);
            gifView.setVisibility(View.GONE);
            JCVideoPlayerStandard jcVideoPlayerStandard =
                    (JCVideoPlayerStandard) findViewById(R.id.videoplayer);
            jcVideoPlayerStandard.backButton.setVisibility(View.GONE);
            jcVideoPlayerStandard.setVisibility(View.VISIBLE);
            jcVideoPlayerStandard.setUp(path
                    , JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL);
            jcVideoPlayerStandard.thumbImageView.setImageBitmap(getVideoThumbnail(path));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }

    public static Bitmap getVideoThumbnail(String videoPath) {
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(videoPath);
        Bitmap bitmap = media.getFrameAtTime();
        return bitmap;
    }
}
