package com.aimconsulting.gettyexample.activity;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.GridView;

import com.aimconsulting.gettyexample.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.io.InputStream;
import java.util.Scanner;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.swipeUp;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;


/**
 * Robot pattern helps instrumentation tests in separating the ACTS and ASSERTIONS from ARRANGEMENTS.
 * This way if there is any change in the view id's or assertions ..
 * we can change it in robot class instead of changing it in every test.
 */
public class ImageSearchActivityRobot {

    //<editor-fold desc= "ACTS">

    public ImageSearchActivityRobot startActivity(ActivityTestRule<ImageSearchActivity> activityTestRule) {
        activityTestRule.launchActivity(null);
        return this;
    }

    public ImageSearchActivityRobot initializeMockResponses(final Context context, MockWebServer server, final int responseCode) {
        Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
                if (request.getPath().contains("search/images?phrase=")) {
                    String imagesResponse = getJsonResponse(context, "test_images_reposne");
                    MockResponse mockResponse = new MockResponse().setResponseCode(responseCode);
                    if (responseCode == 200){
                        mockResponse.setBody(imagesResponse);
                    }
                    return mockResponse;
                }
                return null;
            }
        };

        server.setDispatcher(dispatcher);
        return this;
    }

    public String getJsonResponse(Context context, String fileName) {
        int jsonResId = context.getResources().getIdentifier(fileName, "raw", context.getPackageName());
        InputStream ins = context.getResources().openRawResource(jsonResId);
        Scanner sc = new Scanner(ins).useDelimiter("\\A");
        return sc.hasNext() ? sc.next() : "";
    }

    public ImageSearchActivityRobot clickOnImage() {
        onData(anything()).inAdapterView(withId(R.id.gridView)).atPosition(0)
                .onChildView(withId(R.id.imageView)).perform(click());
        return this;
    }

    public ImageSearchActivityRobot clickOnSearchButton() {
        ViewInteraction appCompatImageView = onView(
                allOf(withClassName(is("android.support.v7.widget.AppCompatImageView")), withContentDescription("Search"),
                        childAtPosition(
                                allOf(withClassName(is("android.widget.LinearLayout")),
                                        childAtPosition(
                                                withId(R.id.searchView),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatImageView.perform(click());
        return this;
    }

    public ImageSearchActivityRobot enterSearchQuery(String queryName) {
        ViewInteraction searchAutoComplete = onView(
                allOf(withClassName(is("android.widget.SearchView$SearchAutoComplete")),
                        childAtPosition(
                                allOf(withClassName(is("android.widget.LinearLayout")),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                1)),
                                0),
                        isDisplayed()));
        searchAutoComplete.perform(replaceText(queryName), closeSoftKeyboard());
        return this;
    }

    public ImageSearchActivityRobot performSearch() throws InterruptedException {
        ViewInteraction searchAutoComplete2 = onView(
                allOf(withClassName(is("android.widget.SearchView$SearchAutoComplete")),
                        childAtPosition(
                                allOf(withClassName(is("android.widget.LinearLayout")),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                1)),
                                0),
                        isDisplayed()));
        searchAutoComplete2.perform(pressImeActionButton());
        Thread.sleep(500);
        return this;
    }

    public ImageSearchActivityRobot searchClose(){
        onView(withId(R.id.search_close_btn)).perform(click());
        return this;
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    public ImageSearchActivityRobot scrollVetically() {
        onView(withId(R.id.gridView)).perform(swipeUp());
        return this;
    }

    //</editor-fold>

    //<editor-fold desc= "ASSERTIONS">

    public ImageSearchActivityRobot verifyImageAtPosition(int i) {
        onData(anything()).inAdapterView(withId(R.id.gridView)).atPosition(i)
                .onChildView(withId(R.id.imageView)).check(matches(isDisplayed()));
        return this;
    }

    public ImageSearchActivityRobot verifyNoImageResults() {
        onView(withId(R.id.gridView)).check(matches(isEmpty()));
        return this;
    }

    public ImageSearchActivityRobot verifyImageViewActivityLaunched(Instrumentation.ActivityMonitor activityMonitor) {
        Activity activity = activityMonitor.waitForActivityWithTimeout(100);
        assertNotNull(activity);
        activity.finish();
        return this;
    }

    private Matcher<? super View> isEmpty() {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View item) {
                if (item instanceof GridView){
                    int count = ((GridView) item).getAdapter().getCount();
                    return count == 0;
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {

            }
        };
    }

    public void verifyImageAndTextDisplayed() {
        onView(withId(R.id.largeImageView)).check(matches(isDisplayed()));
        onView(withId(R.id.largeTextView)).check(matches(isDisplayed()));
    }

    //</editor-fold>
}
