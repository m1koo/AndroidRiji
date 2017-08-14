package com.zd.miko.riji.MVP.Service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zd.miko.riji.MVP.Service.IRetrofit.IRetroShareService;
import com.zd.miko.riji.MyApp;
import com.zd.miko.riji.R;

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
public class ShareService extends IntentService {
    public ShareService() {
        super("ShareService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startService(Context context, String articleId, String title) {
        Intent intent = new Intent(context, ShareService.class);
        intent.putExtra("articleId", articleId);
        intent.putExtra("title", title);

        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String articleId = intent.getStringExtra("articleId");
            String title = intent.getStringExtra("title");

            Retrofit retrofit = new Retrofit
                    .Builder()
                    .baseUrl(getString(R.string.host))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
            IRetroShareService service = retrofit.create(IRetroShareService.class);
            service.share(getString(R.string.share), title, articleId).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Log.i("xyz", response.body());
                    if (response.body().equals("NoArticle")) {
                        UploadService.startService(MyApp.context, articleId, title);
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.i("xyz", "失败");
                }
            });
        }
    }
}
