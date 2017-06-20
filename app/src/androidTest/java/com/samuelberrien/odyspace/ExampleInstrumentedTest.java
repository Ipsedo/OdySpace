package com.samuelberrien.odyspace;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.samuelberrien.odyspace.objects.Icosahedron;
import com.samuelberrien.odyspace.utils.collision.Box;

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
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.samuelberrien.odyspace", appContext.getPackageName());
    }

    @Test
    public void boxTest() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        Icosahedron ico = new Icosahedron(appContext, new float[]{0f, 0f, 510f}, 1f);
        ico.move();

        Box box = new Box(-500f, -500f, -500f, 1000f, 1000f, 1000f);

        assertFalse(ico.isInside(box));
    }
}
