package com.zd.miko.riji.MVP.Service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.zd.miko.riji.Bean.ArticleBean;
import com.zd.miko.riji.Bean.ContentJson;
import com.zd.miko.riji.Bean.Element;
import com.zd.miko.riji.Bean.RealmBean.RealmDiaryDetailBean;
import com.zd.miko.riji.CustomView.RichEditText.RichTextEditor;
import com.zd.miko.riji.MVP.Service.IRetrofit.IRetroNormalService;
import com.zd.miko.riji.R;
import com.zd.miko.riji.Utils.RetrofitParameterBuilder;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import io.realm.Realm;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class UploadService extends IntentService {
    public UploadService() {
        super("UploadService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startService(Context context, String articleId, String title) {
        Intent intent = new Intent(context, UploadService.class);
        intent.putExtra("articleId", articleId);
        intent.putExtra("title", title);
        context.startService(intent);
    }

    private void sendNotification() {
        //获取NotificationManager实例
        NotificationManager notifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //实例化NotificationCompat.Builde并设置相关属性
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                //设置小图标
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.drawable.ic_cloud_circle_black_48dp))
                .setSmallIcon(R.mipmap.ic_launcher)
                //设置通知标题
                .setContentTitle("Mei日记")
                //设置通知内容
                .setContentText("Wifi状态下自动同步日记");
        //设置通知时间，默认为系统发出通知的时间，通常不用设置
        //.setWhen(System.currentTimeMillis());
        //通过builder.build()方法生成Notification对象,并发送通知,id=1
        notifyManager.notify(1, builder.build());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {


            String articleId = intent.getStringExtra("articleId");

            String title = intent.getStringExtra("title");
            Realm realm = Realm.getDefaultInstance();
            /**serach by id*/
            RealmDiaryDetailBean realmBean = realm
                    .where(RealmDiaryDetailBean.class)
                    .equalTo("articleId", articleId)
                    .findFirst();

            ArticleBean diaryBean = new ArticleBean();
            diaryBean.setCompleteFlag(realmBean.isCompleteFlag());
            diaryBean.setYear(realmBean.getYear());
            diaryBean.setDay(realmBean.getDay());
            diaryBean.setMonth(realmBean.getMonth());
            String content = realmBean.getContent();
            String contentEncode = null;
            try {
                contentEncode = URLEncoder.encode(content, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                contentEncode = "内容丢失";
            }
            diaryBean.setContent(contentEncode);
            diaryBean.setLocation(realmBean.getLocationStr());
            diaryBean.setEditTime(realmBean.getEditTime());
            diaryBean.setArticleId(realmBean.getArticleId());
            diaryBean.setUserId(realmBean.getUserId());
            diaryBean.setOutVisible(realmBean.isOutVisible());
            /**get the contentJson then use gson transform it to obj
             * to traverse and upload*/
            String contentJson = realmBean.getContent();

            ContentJson contentBean = new Gson().fromJson(contentJson,
                    ContentJson.class);
            /**create a builder to content string and file*/
            RetrofitParameterBuilder builder = RetrofitParameterBuilder.newBuilder();

            String diaryJson = new Gson().toJson(diaryBean);

            Log.i("xyz", diaryJson);
            /**不为null说明是share*/
            if (title != null) {
                builder.addParameter("title", title);
                builder.addParameter("isShare", true);
            } else {
                sendNotification();
            }

            builder.addParameter("diaryJson", diaryJson);

            String rootPath = Environment.getExternalStorageDirectory() + "/meiriji/"
                    + articleId;

            int imageIndex = 0;
            int videoIndex = 0;
            int gifIndex = 0;

            for (Element e : contentBean.getElementList()) {
                if (e.getElementType() == RichTextEditor.IMAGE) {
                    File f = new File(rootPath + "/image_" + imageIndex + ".cvv");
                    if (!f.exists()) {
                        continue;
                    }
                    builder.addParameter("file", f);
                    imageIndex++;
                } else if (e.getElementType() == RichTextEditor.GIF) {
                    File f = new File(rootPath + "/gif_" + gifIndex + ".cvv");
                    if (!f.exists()) {
                        continue;
                    }
                    builder.addParameter("file", f);

                    gifIndex++;
                } else if (e.getElementType() == RichTextEditor.VIDEO) {
                    File f = new File(rootPath + "/video_" + videoIndex + ".cvv");
                    if (!f.exists()) {
                        continue;
                    }
                    builder.addParameter("file", f);
                    videoIndex++;
                }
            }
            Map<String, RequestBody> params = builder.bulider();
            Retrofit retrofit = new Retrofit
                    .Builder()
                    .baseUrl(getString(R.string.host))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
            IRetroNormalService service = retrofit.create(IRetroNormalService.class);
            service.upload(getString(R.string.upload), params).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Log.i("xyz", response.body());
                    NotificationManager notifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notifyManager.cancel(1);
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.i("xyz", "失败");
                }
            });
        }
    }
}
