package com.zd.miko.riji.MVP.Register;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.zd.miko.riji.DtoEntity.LoginExcution;
import com.zd.miko.riji.MVP.VerifyPhone.VerifyPhoneContract;
import com.zd.miko.riji.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Miko on 2017/3/27.
 */

public class RegisterServiceImpl implements RegisterService {
    private Context context;

    public RegisterServiceImpl(Context context) {
        this.context = context;
    }

    @Override
    public void checkPhone(String phone, VerifyPhoneContract.checkRegistedListener listener) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getResources().getString(R.string.host))
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        IRetrofitRegisterService service = retrofit
                .create(IRetrofitRegisterService.class);
        Call<String> call = service.doCheckPhone(phone,
                context.getString(R.string.checkPhone_field));

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                listener.onResult(Boolean.parseBoolean(response.body()));
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.i("xyz", t.getMessage() + t.getCause());
                listener.onError(t.getCause() + t.getMessage());
            }
        });
    }

    @Override
    public void register(String phone, String nickName, String password, RegisterContract.registerListener listener) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getResources().getString(R.string.host))
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        IRetrofitRegisterService service = retrofit
                .create(IRetrofitRegisterService.class);
        Call<String> call = service.doRegister(phone, nickName, password,
                context.getString(R.string.register_field));

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String md5 = "";
                LoginExcution excution = new Gson().fromJson(response.body()
                        , LoginExcution.class);
                listener.onSuccess(excution.getMd5());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.i("xyz", t.getMessage() + t.getCause());
            }
        });
    }
}
