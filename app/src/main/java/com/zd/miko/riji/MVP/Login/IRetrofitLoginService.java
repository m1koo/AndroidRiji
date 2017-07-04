package com.zd.miko.riji.MVP.Login;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Miko on 2017/3/21.
 */

public interface IRetrofitLoginService {
    @FormUrlEncoded
    @POST("{path}")
    Call<String> doPost(@Field("userName") String userName
            , @Field("password") String password, @Path("path") String path);
}
