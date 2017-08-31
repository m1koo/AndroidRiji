package com.zd.miko.riji.MVP.Service.IRetrofit;

import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by Miko on 2017/7/25.
 */

public interface IRetroGetArticleService {
    @Multipart
    @POST("{path}")
    Call<String> getArticle(@Path("path") String path,@Part("userId") String userId,
                        @Part("isRefresh") boolean isRefresh);
}
