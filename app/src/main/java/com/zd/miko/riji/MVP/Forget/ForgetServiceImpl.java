package com.zd.miko.riji.MVP.Forget;

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

public class ForgetServiceImpl implements ForgetService {
    private Context context;

    public ForgetServiceImpl(Context context) {
        this.context = context;
    }

    @Override
    public void doModifyPass(String phone, String password, ForgetContract.modifyListener listener) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getResources().getString(R.string.host))
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        IRetrofitModifyPassService service = retrofit
                .create(IRetrofitModifyPassService.class);
        Call<String> call = service.doPost(phone, password,
                context.getString(R.string.modify_pass_field));

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                listener.onResult(true);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.i("xyz", t.getMessage() + t.getCause());
                listener.onError(t.getCause() + t.getMessage());
            }
        });
    }
}
