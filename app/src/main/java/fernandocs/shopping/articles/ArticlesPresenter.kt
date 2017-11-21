package fernandocs.shopping.articles

import fernandocs.shopping.data.Article
import fernandocs.shopping.data.source.ArticlesRepository
import fernandocs.shopping.util.EspressoIdlingResource
import fernandocs.shopping.util.schedulers.BaseSchedulerProvider
import io.reactivex.disposables.CompositeDisposable

class ArticlesPresenter(private val articlesRepository: ArticlesRepository,
                        private val articlesView: ArticlesContract.View,
                        private val schedulerProvider: BaseSchedulerProvider) : ArticlesContract.Presenter {

    private var compositeDisposable = CompositeDisposable()

    init {
        articlesView.setPresenter(this)
    }

    override fun subscribe() {
        loadArticles()
    }

    override fun loadArticles(filterSearch: String?, colors: List<String>?, maxPrice: Int?) {

        EspressoIdlingResource.increment()

        compositeDisposable.clear()

        articlesView.showLoadingIndicator()

        compositeDisposable.add(articlesRepository.getArticles(filterSearch, colors, maxPrice)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .doFinally {
                    if (!EspressoIdlingResource.idlingResource.isIdleNow) {
                        EspressoIdlingResource.decrement()
                    }
                }
                .subscribe({
                    articlesView.hideLoadingIndicator()
                    processArticles(it)
                }, {
                    articlesView.hideLoadingIndicator()
                    articlesView.showLoadingArticlesError()
                }))
    }

    private fun processArticles(articles: List<Article>) {
        if (articles.isEmpty()) articlesView.showNoArticles() else articlesView.showArticles(articles)
    }

    override fun unsubscribe() {
        compositeDisposable.clear()
    }

}