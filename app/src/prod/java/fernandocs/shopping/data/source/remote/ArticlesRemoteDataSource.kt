package fernandocs.shopping.data.source.remote

import fernandocs.shopping.data.Article
import fernandocs.shopping.data.source.ArticlesDataSource
import fernandocs.shopping.data.source.remote.api.ArticlesApi
import io.reactivex.Flowable

class ArticlesRemoteDataSource
    private constructor(private val articleApi: ArticlesApi) : ArticlesDataSource {

    override fun getArticles(fullText: String?, colors: List<String>?, price: Int?) =
            articleApi.getArticles(fullText, colors, if (price != null) "0-" + price else null)
                    .flatMap { Flowable.fromIterable(it.content) }
                    .map { Article(it.brand.name,
                            it.name,
                            if (it.units.isNotEmpty()) {
                                it.units.first().price.formatted
                            } else { "no price" },
                            if (it.media.images.isNotEmpty()) {
                                it.media.images.first().mediumHdUrl
                            } else { "" }) }
                    .toList()
                    .toFlowable()!!

    companion object {

        fun getInstance(articleApi: ArticlesApi) = ArticlesRemoteDataSource(articleApi)

    }
}