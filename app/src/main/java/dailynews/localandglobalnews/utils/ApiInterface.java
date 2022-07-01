package dailynews.localandglobalnews.utils;

import java.util.List;
import java.util.Map;

import dailynews.localandglobalnews.models.AToZNewsAdsModel;
import dailynews.localandglobalnews.models.BannerModelList;
import dailynews.localandglobalnews.models.BreakingNews.NewsModel;
import dailynews.localandglobalnews.models.OwnAdsModel;
import dailynews.localandglobalnews.models.QuizModelList;
import dailynews.localandglobalnews.models.category.CatModel;
import dailynews.localandglobalnews.models.tabTextAndUrls.UrlOrTAbTextModel;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {


//    @FormUrlEncoded
//    @POST("ads_fetch.php")
//    Call<List<AdsModel>> fetchAds(@Field("id") String id);

//    @FormUrlEncoded
//    @POST("ads_update.php")
//    Call<MessageModel> updateAdIds(@FieldMap Map<String, String> map);
//
//    @FormUrlEncoded
//    @POST("fetch_categories.php")
//    Call<List<CatModel>> fetchCategory(@Field("id") String id);

//    @FormUrlEncoded
//    @POST("fetch_cat_news.php")
//    Call<List<NewsModel>> fetchNews(@Field("id") String id);

    @FormUrlEncoded
    @POST("fetch_other_news.php")
    Call<List<NewsModel>> fetchOtherNews(@Field("tableName") String id);

    @FormUrlEncoded
    @POST("fetch_categories.php")
    Call<List<CatModel>> fetchCategory(@Field("id") String id);

    @POST("fetch_quiz_categories.php")
    Call<List<CatModel>> fetchQuizCategory();

    @FormUrlEncoded
    @POST("fetch_cat_news.php")
    Call<List<NewsModel>> fetchCatNewsItem(@Field("id") String id);

    @FormUrlEncoded
    @POST("fetch_url_or_tabtext.php")
    Call<UrlOrTAbTextModel> fetchURLOrTABText(@Field("id") String id);

    @FormUrlEncoded
    @POST("ads_fetch.php")
    Call<List<AToZNewsAdsModel>> aTozNewsAdsFetch(@Field("id") String id);

    @FormUrlEncoded
    @POST("fetch_own_ads.php")
    Call<List<OwnAdsModel>> fetchOwnAds(@Field("app_id") String appId);

    @FormUrlEncoded
    @POST("fetch_banners.php")
    Call<BannerModelList> fetchBanner(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("fetch_quiz_questions.php")
    Call<QuizModelList> fetchQuizQuestions(@Field("catId") String id);

}
