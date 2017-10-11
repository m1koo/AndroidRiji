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
import com.zd.miko.riji.Bean.ArticleSynBean;
import com.zd.miko.riji.Bean.ContentJson;
import com.zd.miko.riji.Bean.Element;
import com.zd.miko.riji.Bean.RealmBean.RealmDiaryDetailBean;
import com.zd.miko.riji.CustomView.RichEditText.RichTextEditor;
import com.zd.miko.riji.MVP.ModuleEditor.EventBusMsg.MessageEvent;
import com.zd.miko.riji.MVP.Service.IRetrofit.IDownldPicService;
import com.zd.miko.riji.R;
import com.zd.miko.riji.Utils.RetrofitParameterBuilder;
import com.zd.miko.riji.Utils.Utils;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
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
public class SynService extends IntentService {
    public SynService() {
        super("SynService");
    }

    private static final String TAG = "SynService";
    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startService(Context context) {
        Intent intent = new Intent(context, SynService.class);
        context.startService(intent);
    }

    private HashMap<String, Integer> taskMap = new HashMap<>();

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

            Realm realm = Realm.getDefaultInstance();
            /**serach by id*/
            RealmResults<RealmDiaryDetailBean> realmBeans = realm
                    .where(RealmDiaryDetailBean.class).findAll();

            StringBuilder sb = new StringBuilder();
            for (RealmDiaryDetailBean diaryDetailBean : realmBeans) {
                sb.append(diaryDetailBean.getArticleId() + " ");
            }
            realm.close();

            RetrofitParameterBuilder builder = RetrofitParameterBuilder.newBuilder();

            builder.addParameter("articles", sb.toString());

            builder.addParameter("userId", Utils.getUserAccount());

            Utils.normalPost(builder, getString(R.string.syn), new Utils.PostCall() {
                @Override
                public void onSuccess(String response) {
                    if (response == null) {
                        Log.i(TAG, "response.body()" + " null");
                    } else {
                        ArticleSynBean articleSynBean = new Gson()
                                .fromJson(response, ArticleSynBean.class);
                        parseSysBean(articleSynBean);
                    }
                }

                @Override
                public void onFail(String error) {

                }
            });

        }
    }

    private void parseSysBean(ArticleSynBean articleSynBean) {

        ArrayList<String> prepareDownloadArticles = (ArrayList<String>) articleSynBean
                .getPrepareDownloadArticleIds();
        ArrayList<String> prepareUploadArticles = (ArrayList<String>) articleSynBean
                .getPrepareUploadArticleIds();

        for (String articleId : prepareDownloadArticles) {
            /**articleId对应的任务数初始化为0*/
            taskMap.put(articleId, 0);
            downloadArticleInfo(articleId);

        }

        for (String articleId : prepareUploadArticles) {
            taskMap.put(articleId, 1);
            UploadService.startService(this, articleId, null);
        }
    }


    @Subscribe
    public void onMessageEvent(MessageEvent event) throws InterruptedException {
        if (event.type.equals("UploadListener")) {
            taskMap.remove(event.message);

            if (taskMap.isEmpty()) {
                Log.i(TAG, "syn end");
            }
        }
    }


    private void downloadArticleInfo(String articleId) {
        RetrofitParameterBuilder builder = RetrofitParameterBuilder.newBuilder();

        builder.addParameter("articleId", articleId);

        Utils.normalPost(builder, getString(R.string.get_article_by_id), new Utils.PostCall() {
            @Override
            public void onSuccess(String response) {

                ArticleBean articleBean = new Gson().fromJson(response, ArticleBean.class);

                if(articleBean == null){
                    return;
                }

                String content = articleBean.getContent();

                ContentJson contentJson = new Gson().fromJson(content,
                        ContentJson.class);

                for (Element e : contentJson.getElementList()) {

                    int type = e.getElementType();
                    if (type != RichTextEditor.TEXT) {
                        String typeStr = null;
                        if (type == RichTextEditor.GIF) {
                            typeStr = "gif";
                        } else if (type == RichTextEditor.IMAGE) {
                            typeStr = "image";
                        } else if (type == RichTextEditor.VIDEO) {
                            typeStr = "video";
                        }

                        int oldValue = taskMap.get(articleId);
                        /**更新任务数*/
                        taskMap.replace(articleId, ++oldValue);

                        String path = getString(R.string.host) + "img/"
                                + articleId + "/"
                                + typeStr + "_" + e.getIndex() + ".cvv";

                        downloadImage(articleId, path);
                    }
                }

                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(realm1 -> {
                    RealmDiaryDetailBean diaryDetailBean = realm1
                            .createObject(RealmDiaryDetailBean.class);
                    diaryDetailBean.setLocationStr(articleBean.getLocation());
                    diaryDetailBean.setYear(articleBean.getYear());
                    diaryDetailBean.setEditTime(articleBean.getEditTime());
                    diaryDetailBean.setMonth(articleBean.getMonth());
                    diaryDetailBean.setDay(articleBean.getDay());
                    diaryDetailBean.setUserId(articleBean.getUserId());
                    diaryDetailBean.setContent(articleBean.getContent());
                    diaryDetailBean.setArticleId(articleBean.getArticleId());
                });
            }

            @Override
            public void onFail(String error) {

            }
        });
    }

    private void downloadImage(String articleId, String picUrl) {

        String host = picUrl.substring(0, picUrl.lastIndexOf("/") + 1);
        String path = picUrl.substring(picUrl.lastIndexOf("/") + 1, picUrl.length());

        String d1 = Environment.getExternalStorageDirectory()
                + "/meiriji/" + articleId;

        String srcPath = d1 + "/" + path;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(host)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        IDownldPicService service = retrofit.create(IDownldPicService.class);
        Call<ResponseBody> call = service.doDownld(path);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() == null) {
                    return;
                }

                InputStream is = response.body().byteStream();
                File folder = new File(d1);

                if (!folder.exists())
                    folder.mkdirs();

                Utils.copyFile(is, srcPath);
                /**任务数减一*/
                int oldValue = taskMap.get(articleId);
                taskMap.replace(articleId, --oldValue);
                
                //TODO
                if (oldValue == 0) {
                    taskMap.remove(articleId);
                }

                if (taskMap.isEmpty()) {
                    Log.i(TAG, "syn end");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i(TAG, "download icon url failure");
            }
        });
    }
}
