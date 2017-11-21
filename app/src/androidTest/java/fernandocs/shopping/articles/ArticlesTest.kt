package fernandocs.shopping.articles

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.IdlingRegistry
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.view.View
import android.view.ViewGroup
import fernandocs.shopping.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class ArticlesTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(ArticlesActivity::class.java)

    @Before
    @Throws(Exception::class)
    fun setUp() {
        IdlingRegistry.getInstance().register(
                mActivityTestRule.activity.getCountingIdlingResource())
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(mActivityTestRule.activity.getCountingIdlingResource())
    }

    @Test
    fun articlesDefaultFields() {

        val actionMenuItemView = onView(
                allOf<View>(withId(R.id.action_search), withContentDescription("Search"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.toolbar),
                                        1),
                                0),
                        isDisplayed()))

        actionMenuItemView.perform(click())

        val searchAutoComplete = onView(
                allOf(withId(R.id.search_src_text),
                        childAtPosition(
                                allOf(withId(R.id.search_plate),
                                        childAtPosition(
                                                withId(R.id.search_edit_frame), 1)),
                                0),
                        isDisplayed()))
        searchAutoComplete.perform(replaceText("teste search"), closeSoftKeyboard())
        searchAutoComplete.perform(pressImeActionButton())

        onView(withId(R.id.fabFilter)).check(matches(isDisplayed()))

        onView(withId(R.id.fabFilter)).perform(click())

        onView(withId(R.id.fabFilterColor)).check(matches(isDisplayed()))
        onView(withId(R.id.fabFilterMoney)).check(matches(isDisplayed()))

        onView(withId(R.id.fabFilter)).perform(click())

        onView(withId(R.id.fabFilterColor)).check(matches(not(isDisplayed())))
        onView(withId(R.id.fabFilterMoney)).check(matches(not(isDisplayed())))

        onView(withId(R.id.fabFilter)).perform(click())

        onView(withId(R.id.fabFilterColor)).perform(click())
    }

    private fun childAtPosition(
            parentMatcher: Matcher<View>, position: Int): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return (parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position))
            }
        }
    }
}