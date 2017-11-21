package fernandocs.shopping.articles

import fernandocs.shopping.data.Article
import fernandocs.shopping.data.source.ArticlesRepository
import fernandocs.shopping.util.schedulers.BaseSchedulerProvider
import fernandocs.shopping.util.schedulers.ImmediateSchedulerProvider
import io.reactivex.Flowable
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class ArticlesPresenterTest {
    private var MOCK_ITEMS: List<Article>? = null

    @Mock
    private lateinit var mArticlesRepository: ArticlesRepository

    @Mock
    private lateinit var mArticlesView: ArticlesContract.View

    private lateinit var mSchedulerProvider: BaseSchedulerProvider

    private lateinit var mArticlesPresenter: ArticlesPresenter

    @Before
    fun setupTasksPresenter() {
        MockitoAnnotations.initMocks(this)

        mSchedulerProvider = ImmediateSchedulerProvider()

        mArticlesPresenter = ArticlesPresenter(mArticlesRepository, mArticlesView, mSchedulerProvider)

        MOCK_ITEMS = arrayListOf(
                Article("Title1", "Description1", "£11.00", "https://i2.ztat.net/thumb_hd/BE/82/4G/00/2C/11/BE824G002-C11@8.jpg"),
                Article("Title2", "Description2", "£12.00", "https://i2.ztat.net/thumb_hd/BE/82/4G/00/2C/11/BE824G002-C11@8.jpg"),
                Article("Title3", "Description3", "£13.00", "https://i2.ztat.net/thumb_hd/BE/82/4G/00/2C/11/BE824G002-C11@8.jpg"))
    }

    @Test
    fun createPresenterSetsThePresenterToView() {
        mArticlesPresenter = ArticlesPresenter(mArticlesRepository, mArticlesView, mSchedulerProvider)

        verify<ArticlesContract.View>(mArticlesView).setPresenter(mArticlesPresenter)
    }

    @Test
    fun loadAllArticlesFromRepositoryAndLoadIntoView() {
        `when`(mArticlesRepository.getArticles()).thenReturn(Flowable.just<List<Article>>(MOCK_ITEMS))

        mArticlesPresenter.loadArticles()

        verify<ArticlesContract.View>(mArticlesView).showLoadingIndicator()
        verify<ArticlesContract.View>(mArticlesView).hideLoadingIndicator()
    }

    @Test
    fun loadFilteredArticlesFromRepositoryAndLoadIntoView() {
        `when`(mArticlesRepository.getArticles("XXX", listOf("black"), 10)).thenReturn(Flowable.just<List<Article>>(MOCK_ITEMS))

        mArticlesPresenter.loadArticles("XXX", listOf("black"), 10)

        verify<ArticlesContract.View>(mArticlesView).showLoadingIndicator()
        verify<ArticlesContract.View>(mArticlesView).hideLoadingIndicator()
    }

    @Test
    fun errorLoadingArticlesShowsError() {
        `when`(mArticlesRepository.getArticles()).thenReturn(Flowable.error(Exception()))

        mArticlesPresenter.loadArticles()

        verify<ArticlesContract.View>(mArticlesView).showLoadingArticlesError()
    }

    @Test
    fun loadingArticlesShowsEmptyMessage() {
        `when`(mArticlesRepository.getArticles()).thenReturn(Flowable.just(emptyList()))

        mArticlesPresenter.loadArticles()

        verify<ArticlesContract.View>(mArticlesView).showNoArticles()
    }
}
