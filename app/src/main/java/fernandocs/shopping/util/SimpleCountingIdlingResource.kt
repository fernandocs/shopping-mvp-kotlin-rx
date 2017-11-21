package fernandocs.shopping.util

import android.support.test.espresso.IdlingResource
import java.util.concurrent.atomic.AtomicInteger

class SimpleCountingIdlingResource(resourceName: String) : IdlingResource {

    private val mResourceName = checkNotNull(resourceName)

    private val counter = AtomicInteger(0)

    @Volatile private var resourceCallback: IdlingResource.ResourceCallback? = null

    override fun getName() = mResourceName

    override fun isIdleNow() = (counter.get() == 0)

    override fun registerIdleTransitionCallback(resourceCallback: IdlingResource.ResourceCallback) {
        this.resourceCallback = resourceCallback
    }

    fun increment() {
        counter.getAndIncrement()
    }

    fun decrement() {
        val counterVal = counter.decrementAndGet()

        if (counterVal == 0) {
            if (null != resourceCallback) {
                resourceCallback!!.onTransitionToIdle()
            }
        }

        if (counterVal < 0) {
            throw IllegalArgumentException("Counter has been corrupted!")
        }
    }
}