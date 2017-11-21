package fernandocs.shopping.articles

import fernandocs.shopping.BasePresenter
import fernandocs.shopping.BaseView
import fernandocs.shopping.data.Article

interface ArticlesContract {

    interface View : BaseView<Presenter> {

        fun showLoadingIndicator()

        fun hideLoadingIndicator()

        fun showArticles(articles: List<Article>)

        fun showFilter()

        fun hideFilter()

        fun showFilterOptions()

        fun hideFilterOptions()

        fun showColorFilterDialog()

        fun showMoneyFilterDialog()

        fun showNoArticles()

        fun showLoadingArticlesError()

    }

    interface Presenter : BasePresenter {
        fun loadArticles(filterSearch: String? = null,
                         colors: List<String>? = null,
                         maxPrice: Int? = null)
    }
}