package com.zd.miko.riji.MVP.VerifyPhone;

import android.content.Context;

import com.zd.miko.riji.MVP.Login.LoginListener;
import com.zd.miko.riji.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Miko on 2017/4/10.
 */

public class PhoneLoginServiceImp implements PhoneLoginService {

    private Context context;

    public PhoneLoginServiceImp(Context context) {
        this.context = context;
    }

    @Override
    public void doLogin(String phone, LoginListener listener) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getResources().getString(R.string.host))
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        IRetrofitPhoneLoginService service = retrofit.create(IRetrofitPhoneLoginService.class);

        Call<String> call = service.doPost(phone, context.getString(R.string.phone_login_field));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                listener.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                listener.onFailure(t.getCause() + t.getMessage());
            }
        });
    }
}
