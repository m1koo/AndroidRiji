package com.zd.miko.riji.MVP.Service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.zd.miko.riji.Bean.ArticleSynBean;
import com.zd.miko.riji.Bean.RealmBean.RealmDiaryDetailBean;
import com.zd.miko.riji.R;
import com.zd.miko.riji.Utils.RetrofitParameterBuilder;
import com.zd.miko.riji.Utils.Utils;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

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
                        Log.i("xyz", "response.body()" + " null");
                    } else {
                        Log.i("xyz", response);
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
            downloadArticleInfo(articleId);
        }

    }

    private void downloadArticleInfo(String articleId){

        RetrofitParameterBuilder builder = RetrofitParameterBuilder.newBuilder();

        builder.addParameter("articleId",articleId);

        Utils.normalPost(builder, getString(R.string.get_article_by_id), new Utils.PostCall() {
            @Override
            public void onSuccess(String response) {
                Log.i("xyz",response);

            }

            @Override
            public void onFail(String error) {

            }
        });
    }

//    private void downloadImage(String articleId, String path) {
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl()
//                .addConverterFactory(ScalarsConverterFactory.create())
//                .build();
//        IDownldPicService service = retrofit.create(IDownldPicService.class);
//        Call<ResponseBody> call = service.doDownld(path);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                if (response.body() == null) {
//                    return;
//                }
//                InputStream is = response.body().byteStream();
//                File folder = new File(Environment.getExternalStorageDirectory() + "/beijing/");
//
//                if (!folder.exists())
//                    folder.mkdir();
//                FileOutputStream fos = null;
//                try {
//                    fos = new FileOutputStream(file);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//                BufferedInputStream bis = new BufferedInputStream(is);
//                byte[] buffer = new byte[1024];
//                int len;
//                try {
//                    while ((len = bis.read(buffer)) != -1) {
//                        fos.write(buffer, 0, len);
//                        fos.flush();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    fos.close();
//                    bis.close();
//                    is.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Log.i("xyz", "download icon url failure");
//            }
//        });
//    }
}
