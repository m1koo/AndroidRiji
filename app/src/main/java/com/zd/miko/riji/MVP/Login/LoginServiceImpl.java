package com.zd.miko.riji.MVP.Login;

import android.content.Context;
import android.util.Log;

import com.zd.miko.riji.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Miko on 2017/3/21.
 */

public class LoginServiceImpl implements LoginService {

    /**context提出来方便单元测试*/
    private Context context;

    public LoginServiceImpl(Context context) {
        this.context = context;
    }

    @Override
    public void getUserId(String type, String account,LoginListener listener) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.host))
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();


        IRetrofitLoginService service = retrofit.create(IRetrofitLoginService.class);
        Call<String> call = service.doPost(account,type,"login");

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                listener.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.i("xyz", t.getMessage() + t.getCause());
                listener.onFailure(t.getCause() + t.getMessage());
            }
        });
    }
}
