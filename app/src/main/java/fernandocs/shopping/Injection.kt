package fernandocs.shopping

import android.content.Context
import fernandocs.shopping.data.source.ArticlesRepository
import fernandocs.shopping.data.source.remote.ArticlesRemoteDataSource
import fernandocs.shopping.data.source.remote.api.ArticlesApi

object Injection {

    fun provideTasksRepository(context: Context): ArticlesRepository {
        checkNotNull(context)
        return ArticlesRepository
                .getInstance(ArticlesRemoteDataSource
                        .getInstance(ShoppingApp.retrofit.create(ArticlesApi::class.java)))
    }
}