package fernandocs.shopping.data.source.remote

import fernandocs.shopping.data.Article
import fernandocs.shopping.data.source.ArticlesDataSource
import fernandocs.shopping.data.source.remote.api.ArticlesApi
import io.reactivex.Flowable
import java.util.concurrent.TimeUnit

class ArticlesRemoteDataSource
    private constructor(private val articleApi: ArticlesApi) : ArticlesDataSource {

    override fun getArticles(fullText: String?, colors: List<String>?, price: Int?): Flowable<List<Article>> {
        val articles = mutableListOf<Article>()

        if (fullText == null && colors == null && price == null) {

            for (i in 0..100) {
                articles.add(Article("bellybutton",
                        "Long sleeved top - stormy weather",
                        "£11.00",
                        "https://i2.ztat.net/thumb_hd/BE/82/4G/00/2C/11/BE824G002-C11@8.jpg"))
            }

        } else {

            if (fullText != null) {
                articles.add(Article("bellybutton",
                        "Long sleeved top - stormy weather",
                        "£11.00",
                        "https://i2.ztat.net/thumb_hd/BE/82/4G/00/2C/11/BE824G002-C11@8.jpg"))
            }

            colors?.forEachIndexed({ _, color ->
                articles.add(Article("filtered " + color,
                        "Long sleeved top - stormy weather",
                        "£11.00",
                        "https://i2.ztat.net/thumb_hd/BE/82/4G/00/2C/11/BE824G002-C11@8.jpg"))
            })

            if (price != null) {
                articles.add(Article("filtered " + price,
                        "Long sleeved top - stormy weather",
                        "£11.00",
                        "https://i2.ztat.net/thumb_hd/BE/82/4G/00/2C/11/BE824G002-C11@8.jpg"))
            }

        }

        return Flowable
                .fromIterable(articles)
                .delay(1000, TimeUnit.MILLISECONDS)
                .toList()
                .toFlowable()
    }

    companion object {

        fun getInstance(articleApi: ArticlesApi) = ArticlesRemoteDataSource(articleApi)

    }
}