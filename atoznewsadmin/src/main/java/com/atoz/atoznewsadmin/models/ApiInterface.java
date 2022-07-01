package com.atoz.atoznewsadmin.models;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {


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
    @POST("upload_quiz_category.php")
    Call<MessageModel> uploadQuizCategory(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("update_news_category.php")
    Call<MessageModel> updateNewsCategory(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("upload_news_sub_category.php")
    Call<MessageModel> uploadNewsSubCategory(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("fetch_categories.php")
    Call<List<CatModel>> fetchCategory(@Field("id") String id);


    @POST("fetch_quiz_categories.php")
    Call<List<CatModel>> fetchQuizCategory();

    @FormUrlEncoded
    @POST("update_quiz_category.php")
    Call<MessageModel> updateQuizCategory(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("fetch_cat_news.php")
    Call<List<NewsModel>> fetchNews(@Field("id") String id);

    @FormUrlEncoded
    @POST("fetch_other_news.php")
    Call<List<NewsModel>> fetchOtherNews(@Field("tableName") String id);

    @FormUrlEncoded
    @POST("delete_category.php")
    Call<MessageModel> deleteCategory(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("delete_quiz_category.php")
    Call<MessageModel> deleteQuizCategory(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("delete_news.php")
    Call<MessageModel> deleteCatNews(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("delete_other_news.php")
    Call<MessageModel> deleteNews(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("update_cat_news.php")
    Call<MessageModel> updateCatNews(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("update_news.php")
    Call<MessageModel> updateNews(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("updateURLOrTABText.php")
    Call<MessageModel> uploadURLOrTABText(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("ads_fetch.php")
    Call<List<AdsModel>> fetchAds(@Field("id") String id);

    @FormUrlEncoded
    @POST("ads_update.php")
    Call<MessageModel> updateAdIds(@FieldMap Map<String, String> map);

    @Multipart
    @POST("upload_own_ads.php")
    Call<ResponseBody> uploadOwnAds(@Part MultipartBody.Part bannerPart, @Part MultipartBody.Part nativePart, @Part MultipartBody.Part interstitialPart, @Part MultipartBody.Part banUrlPart, @Part MultipartBody.Part nativeUrlPart, @Part MultipartBody.Part interstitialUrlPart, @Part MultipartBody.Part appIdPart);

    @FormUrlEncoded
    @POST("fetch_own_ads.php")
    Call<List<OwnAdsModel>> fetchOwnAds(@Field("app_id") String appId);

    @Multipart
    @POST("update_own_ads.php")
    Call<MessageModel> updateOwnAds(@Part MultipartBody.Part bannerPart, @Part MultipartBody.Part bannerPartTemp, @Part MultipartBody.Part nativePart, @Part MultipartBody.Part nativePartTemp, @Part MultipartBody.Part keyPart, @Part MultipartBody.Part adPart);

    @FormUrlEncoded
    @POST("delete_own_ad.php")
    Call<MessageModel> deleteAd(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("upload_ads_state.php")
    Call<MessageModel> uploadAdsState(@Field("adsState") String adsState);

    @POST("fetch_ads_state.php")
    Call<AdsStateModel> fetchAdsState();

    @FormUrlEncoded
    @POST("fetch_url_or_tabtext.php")
    Call<UrlOrTAbTextModel> fetchURLOrTABText(@Field("id") String id);

    @FormUrlEncoded
    @POST("upload_quiz_questions.php")
    Call<MessageModel> uploadQuizQuestions(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("fetch_quiz_questions.php")
    Call<QuizModelList> fetchQuizQuestions(@Field("catId") String id);

    @FormUrlEncoded
    @POST("Delete_quiz_api.php")
    Call<MessageModel> deleteQuizItems(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("update_quiz_api.php")
    Call<MessageModel> updateQuiz(@FieldMap Map<String, String> map);


    @FormUrlEncoded
    @POST("fetch_banners.php")
    Call<BannerModelList> fetchBanner(@FieldMap Map<String, String> map);

    @Multipart
    @POST("update_banner.php")
    Call<MessageModel> updateBanner(@Part MultipartBody.Part idPart,
                                    @Part MultipartBody.Part imgPart,
                                    @Part MultipartBody.Part urlPart,
                                    @Part MultipartBody.Part deleteImgPart,
                                    @Part MultipartBody.Part imgKeyPart);


}
