package com.zd.miko.riji.MVP.Forget;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Miko on 2017/3/21.
 */

public interface IRetrofitModifyPassService {
    @FormUrlEncoded
    @POST("{path}")
    Call<String> doPost(@Field("phone") String phone
            , @Field("password") String password, @Path("path") String path);
}
