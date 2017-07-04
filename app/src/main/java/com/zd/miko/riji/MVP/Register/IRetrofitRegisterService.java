package com.zd.miko.riji.MVP.Register;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Miko on 2017/3/21.
 */

public interface IRetrofitRegisterService {
    @FormUrlEncoded
    @POST("{path}")
    Call<String> doCheckPhone(@Field("phone") String phone, @Path("path") String path);



    @FormUrlEncoded
    @POST("{path}")
    Call<String> doRegister(@Field("phone") String phone,
                            @Field("nick_name") String nickName,
                            @Field("password") String password,
                            @Path("path") String path);
}
