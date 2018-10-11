package com.aimconsulting.gettyexample.activity;

import android.app.Instrumentation;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.aimconsulting.gettyexample.BaseInstrumentationTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ImageSearchActivityTest extends BaseInstrumentationTest {

    private Instrumentation.ActivityMonitor mActivityMonitor;

    @Before
    public void init(){
        mActivityMonitor = getInstrumentation().addMonitor(ImageViewActivity.class.getName(), null, false);
    }

    @Rule
    public ActivityTestRule<ImageSearchActivity> activityTestRule
            = new ActivityTestRule<>(ImageSearchActivity.class, false, false);

    @Test
    public void searchForAnItem_verifyResultsAppearing() throws InterruptedException {
        new ImageSearchActivityRobot()
                .initializeMockResponses(mContext, mockWebServerRule.server, 200)
                .startActivity(activityTestRule)
                .clickOnSearchButton()
                .enterSearchQuery("flowers")
                .performSearch()
                .verifyImageAtPosition(0);
    }

    @Test
    public void clickOnAnImage_verifyImageViewActivityIsLaunched() throws InterruptedException {
        new ImageSearchActivityRobot()
                .initializeMockResponses(mContext, mockWebServerRule.server, 200)
                .startActivity(activityTestRule)
                .clickOnSearchButton()
                .enterSearchQuery("flowers")
                .performSearch()
                .clickOnImage()
                .verifyImageViewActivityLaunched(mActivityMonitor);
    }

    @Test
    public void onScroll_verifyImagesProcessed() throws InterruptedException {
        new ImageSearchActivityRobot()
                .initializeMockResponses(mContext, mockWebServerRule.server, 200)
                .startActivity(activityTestRule)
                .clickOnSearchButton()
                .enterSearchQuery("flowers")
                .performSearch()
                .scrollVetically();
    }

    @Test
    public void clickOnImage_verifyImageInImageViewActivity() throws InterruptedException {
        new ImageSearchActivityRobot()
                .initializeMockResponses(mContext, mockWebServerRule.server, 200)
                .startActivity(activityTestRule)
                .clickOnSearchButton()
                .enterSearchQuery("flowers")
                .performSearch()
                .clickOnImage()
                .verifyImageAndTextDisplayed();
    }
}
