package io.github.example.pickcolor

import androidx.test.runner.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
//        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
//        assertEquals("io.github.example.pickcolor", appContext.packageName)


        val str = String.format(Locale.getDefault(), "%08s", "fff")
        println(str)
    }
}
