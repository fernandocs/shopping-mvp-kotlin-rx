package fernandocs.shopping.util.schedulers

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SchedulerProvider private constructor() : BaseSchedulerProvider {

    override fun computation() = Schedulers.computation()

    override fun io() = Schedulers.io()

    override fun ui(): Scheduler = AndroidSchedulers.mainThread()

    companion object {

        private var INSTANCE: SchedulerProvider? = null

        val instance: SchedulerProvider
            @Synchronized get() {
                if (INSTANCE == null) {
                    INSTANCE = SchedulerProvider()
                }
                return INSTANCE as SchedulerProvider
            }
    }
}