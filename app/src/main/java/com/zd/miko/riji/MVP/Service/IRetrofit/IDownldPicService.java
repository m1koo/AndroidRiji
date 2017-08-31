package com.zd.miko.riji.MVP.Service.IRetrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface IDownldPicService {
    @GET("{path}")
    Call<ResponseBody> doDownld(@Path("path")String path);
}
