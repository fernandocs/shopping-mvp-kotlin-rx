package fernandocs.shopping.data.source.remote.api

import fernandocs.shopping.data.Result
import io.reactivex.Flowable
import retrofit2.http.GET
import retrofit2.http.Query

interface ArticlesApi {

    @GET("article")
    fun getArticles(@Query("fullText") fullText: String?,
                    @Query("color") colors: List<String>?,
                    @Query("price") price: String?) : Flowable<Result>
}