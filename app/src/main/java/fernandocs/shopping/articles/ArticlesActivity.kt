package fernandocs.shopping.articles

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.support.annotation.VisibleForTesting
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.NumberPicker
import com.squareup.picasso.Picasso
import fernandocs.shopping.Injection
import fernandocs.shopping.R
import fernandocs.shopping.data.Article
import fernandocs.shopping.util.EspressoIdlingResource
import fernandocs.shopping.util.schedulers.SchedulerProvider
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.article_item.view.*
import kotlinx.android.synthetic.main.content_main.*
import java.lang.reflect.InvocationTargetException
import java.text.NumberFormat
import java.util.*

class ArticlesActivity : AppCompatActivity(),
        SearchView.OnQueryTextListener, ArticlesContract.View {

    private var presenter: ArticlesContract.Presenter? = null

    override fun setPresenter(presenter: ArticlesContract.Presenter) {
        this.presenter = presenter
    }

    override fun showLoadingIndicator() {
        textViewMessage.visibility = View.GONE
        progressBarLoadArticles.visibility = View.VISIBLE
    }

    override fun hideLoadingIndicator() {
        progressBarLoadArticles.visibility = View.GONE
    }

    override fun showArticles(articles: List<Article>) {
        this.articles.clear()
        this.articles.addAll(articles)
        recyclerViewArticles.adapter.notifyDataSetChanged()
    }

    override fun showFilter() {
        fabFilter.show()
    }

    override fun hideFilter() {
        hideFilterOptions()
        fabFilter.hide()
    }

    override fun showFilterOptions() {
        fabFilterColor.show()
        fabFilterMoney.show()
    }

    override fun hideFilterOptions() {
        fabFilterColor.hide()
        fabFilterMoney.hide()
    }

    override fun showColorFilterDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle(R.string.picker_color_title)

        dialogBuilder.setMultiChoiceItems(colors.toList().toTypedArray(), checkedColors,
                { _, index, checked ->
                    checkedColors[index] = checked
                }
        )
        dialogBuilder.setOnDismissListener {
            loadArticles()
        }

        dialogBuilder.setPositiveButton(R.string.apply, { _, _ ->
            Log.d(TAG, "filter")
        })
        dialogBuilder.setNegativeButton(R.string.reset, { _, _ ->
            Log.d(TAG, "reset")
            colors.forEachIndexed { i, _ -> checkedColors[i] = false }
        })

        dialogBuilder.create().show()
    }

    override fun showMoneyFilterDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle(R.string.picker_money_title)
        dialogBuilder.setMessage(R.string.picker_money_message)
        val numberPicker = NumberPicker(this)
        numberPicker.maxValue = 9999
        numberPicker.minValue = 0
        numberPicker.value = valueToFilter ?: 0
        numberPicker.setFormatter { i ->
            val format = NumberFormat.getCurrencyInstance()
            format.currency = Currency.getInstance(Locale.UK)
            format.format(i)
        }
        numberPicker.wrapSelectorWheel = false
        numberPicker.isClickable = false
        dialogBuilder.setView(numberPicker)

        dialogBuilder.setOnDismissListener { loadArticles() }

        dialogBuilder.setPositiveButton(R.string.apply, { _, _ ->
            Log.d(TAG, "filter")
            valueToFilter = numberPicker.value
        })

        dialogBuilder.setNegativeButton(R.string.reset, { _, _ ->
            Log.d(TAG, "reset")
            valueToFilter = null
        })
        dialogBuilder.create().show()

        // Fix for bug in Android Picker where the first element is not shown
        try {
            val method = numberPicker.javaClass.getDeclaredMethod("changeValueByOne",
                    Boolean::class.javaPrimitiveType)
            method.isAccessible = true
            method.invoke(numberPicker, true)
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }

    }

    override fun showNoArticles() {
        textViewMessage.visibility = View.VISIBLE
        textViewMessage.setText(R.string.no_articles)
    }

    override fun showLoadingArticlesError() {
        textViewMessage.visibility = View.VISIBLE
        textViewMessage.setText(R.string.error_get_articles)
    }

    companion object {
        val TAG = ArticlesActivity::class.java.simpleName!!
    }

    private val articles = mutableListOf<Article>()
    private var colors = mutableListOf<String>()
    private val checkedColors = booleanArrayOf(false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false, false, false, false)
    private var valueToFilter: Int? = null
    private var textToFilter: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        colors.addAll(resources.getStringArray(R.array.articles_colors))

        recyclerViewArticles.adapter = ArticleAdapter(articles, { _, _ -> })

        hideFilter()

        fabFilter.setOnClickListener {
            if (fabFilterColor.isShown) fabFilterColor.hide() else fabFilterColor.show()
            if (fabFilterMoney.isShown) fabFilterMoney.hide() else fabFilterMoney.show()
        }

        fabFilterColor.setOnClickListener { showColorFilterDialog() }

        fabFilterMoney.setOnClickListener {
            showMoneyFilterDialog()
        }

        setPresenter(ArticlesPresenter(Injection.provideTasksRepository(this),
                this, SchedulerProvider.instance))
    }

    override fun onResume() {
        super.onResume()
        presenter!!.subscribe()
    }

    override fun onPause() {
        super.onPause()
        presenter!!.unsubscribe()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)

        // Associate searchable configuration with the SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val menuItem = menu.findItem(R.id.action_search)
        val searchView = menuItem.actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.imeOptions = EditorInfo.IME_ACTION_SEARCH
        searchView.setOnQueryTextListener(this)

        menuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                Log.d(TAG, "onMenuItemActionExpand")
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                Log.d(TAG, "onMenuItemActionCollapse")
                textToFilter = null
                valueToFilter = null
                checkedColors.forEachIndexed({i, _ -> checkedColors[i] = false})
                hideFilter()
                presenter!!.loadArticles()
                return true
            }

        })

        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        inputManager.hideSoftInputFromWindow(this.currentFocus!!.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS)

        textToFilter = query

        loadArticles()

        showFilter()

        return true
    }

    private fun loadArticles() {
        val colorsSelected = mutableListOf<String>()

        checkedColors.forEachIndexed({i, checked -> if (checked) colorsSelected.add(colors[i])})

        presenter!!.loadArticles(textToFilter, colorsSelected , valueToFilter)
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        Log.d(TAG, newText)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_search -> true
        else -> super.onOptionsItemSelected(item)
    }

    @VisibleForTesting
    fun getCountingIdlingResource() = EspressoIdlingResource.idlingResource

    class ArticleAdapter(private val list: List<Article>,
                         private val itemClick: (Article, Int) -> Unit)
        : RecyclerView.Adapter<ArticleAdapter.ViewHolder>() {
        override fun getItemCount() = list.size

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent?.context).inflate(R.layout.article_item, parent, false)
            return ViewHolder(view, itemClick)
        }

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            holder?.bind(list[position], position)
        }

        class ViewHolder(view: View, private val itemClick: (Article, Int) -> Unit) : RecyclerView.ViewHolder(view) {
            fun bind(item: Article, position: Int) {
                with(item) {
                    Picasso.with(itemView.context).load(thumbnailUrl).into(itemView.imageViewArticleThumbnail)
                    itemView.textViewName.text = brand
                    itemView.textViewDescription.text = description
                    itemView.textViewPrice.textViewPrice.text = price
                    itemView.setOnClickListener { itemClick(this, position) }
                }
            }
        }
    }
}
