package fernandocs.shopping.data.source

import fernandocs.shopping.data.Article
import io.reactivex.Flowable

interface ArticlesDataSource {

    fun getArticles(fullText: String? = null,
                    colors: List<String>? = null,
                    price: Int? = null) : Flowable<List<Article>>

}