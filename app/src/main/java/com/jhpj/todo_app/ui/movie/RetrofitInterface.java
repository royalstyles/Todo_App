package com.jhpj.todo_app.ui.movie;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitInterface {
    @GET("http://kobis.or.kr/kobisopenapi/webservice/rest/boxoffice/searchDailyBoxOfficeList.json")
    Call<Result> getBoxOffice(@Query("key") String key, @Query("targetDt") String targetDt);

}
