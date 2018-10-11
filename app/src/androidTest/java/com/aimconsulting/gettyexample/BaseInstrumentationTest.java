package com.aimconsulting.gettyexample;

import android.content.Context;
import android.support.test.runner.AndroidJUnit4;

import com.aimconsulting.gettyexample.http.getty.TestGettyImageProvider;

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;

@RunWith(AndroidJUnit4.class)
public class BaseInstrumentationTest {

    public Context mContext;

    @Before
    public void initialSetup(){
        mContext = getInstrumentation().getTargetContext();

        TestGettyImageProvider.setBaseUrl(mockWebServerRule.server.url("/").toString());
    }

    @Rule
    public MockWebServerRule mockWebServerRule = new MockWebServerRule();
}
