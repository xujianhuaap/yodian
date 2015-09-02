package maimeng.yodian.app.client.android;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.MediumTest;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<YApplication> {
    public ApplicationTest() {
        super(YApplication.class);
    }

    @MediumTest
    public void testSimpleCreate() {
        createApplication();
    }
}