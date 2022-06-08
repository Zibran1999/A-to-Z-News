package com.atoz.atoznewsadmin.models;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {


    @FormUrlEncoded
    @POST("ads_fetch.php")
    Call<List<AdsModel>> fetchAds(@Field("id") String id);

    @FormUrlEncoded
    @POST("ads_update.php")
    Call<MessageModel> updateAdIds(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("upload_news.php")
    Call<MessageModel> uploadNews(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("upload_cat_news.php")
    Call<MessageModel> uploadCatNews(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("upload_news_category.php")
    Call<MessageModel> uploadNewsCategory(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("upload_news_sub_category.php")
    Call<MessageModel> uploadNewsSubCategory(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("fetch_categories.php")
    Call<List<CatModel>> fetchCategory(@Field("id") String id);
    @FormUrlEncoded
    @POST("fetch_cat_news.php")
    Call<List<NewsModel>> fetchNews(@Field("id") String id);
}
