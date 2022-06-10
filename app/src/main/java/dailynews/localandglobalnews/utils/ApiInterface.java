package dailynews.localandglobalnews.utils;

import java.util.List;

import dailynews.localandglobalnews.models.BreakingNews.NewsModel;
import dailynews.localandglobalnews.models.category.CatModel;
import dailynews.localandglobalnews.models.tabTextAndUrls.UrlOrTAbTextModel;
import retrofit2.Call;
import retrofit2.http.Field;
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

    @FormUrlEncoded
    @POST("fetch_cat_news.php")
    Call<List<NewsModel>> fetchCatNewsItem(@Field("id") String id);

    @FormUrlEncoded
    @POST("fetch_url_or_tabtext.php")
    Call<UrlOrTAbTextModel> fetchURLOrTABText(@Field("id") String id);
}
