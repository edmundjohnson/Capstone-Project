package uk.jumpingmouse.moviecompanion;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        String packageNameBase = "uk.jumpingmouse.moviecompanion";
        // The actual package name may be "uk.jumpingmouse.moviecompanion.dev.admin", etc.
        assertEquals(packageNameBase,
                appContext.getPackageName().substring(0, packageNameBase.length()));
    }
}
