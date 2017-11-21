package fernandocs.shopping

interface BaseView<in T> {

    fun setPresenter(presenter: T)

}