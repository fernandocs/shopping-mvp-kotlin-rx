package fernandocs.shopping.data.source

import fernandocs.shopping.data.source.remote.ArticlesRemoteDataSource

class ArticlesRepository
            private constructor(private val remoteDataSource: ArticlesRemoteDataSource):
        ArticlesDataSource {

    companion object {

        fun getInstance(remoteDataSource: ArticlesRemoteDataSource)
                = ArticlesRepository(remoteDataSource)

    }

    override fun getArticles(fullText: String?, colors: List<String>?, price: Int?)
            = remoteDataSource.getArticles(fullText, colors, price)
}