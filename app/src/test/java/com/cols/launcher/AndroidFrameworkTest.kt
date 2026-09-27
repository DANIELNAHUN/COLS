package com.cols.launcher

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

/**
 * Robolectric framework test (task 3.1 / app-testing spec "Android-dependent
 * unit test without a device"): exercises REAL Android-framework behavior -
 * application context, manifest metadata, and resource resolution - on the
 * JVM with no emulator or device attached.
 *
 * SDK 35 is the highest verified level on the pinned JDK 17 toolchain:
 * Robolectric 4.16.x supports up to API 36 ("Baklava"), but its SDK-36
 * runtime requires Java 21 while this project's verified JVM pin (catalog
 * `jvm = "17"`, D2) is Java 17 - confirmed empirically on the first run
 * ("Android SDK 36 requires Java 21 (have Java 17)"). compileSdk 37 only
 * bounds compilation, not the test runtime. This test runs as part of
 * `./gradlew :app:testDebugUnitTest`.
 */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [35])
class AndroidFrameworkTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun `robolectric provides a real application context on the jvm`() {
        // A full Application instance is created by the framework from the
        // merged manifest - proving the manifest was processed for the JVM.
        assertTrue(
            "expected a framework Application, got ${context.javaClass}",
            context.applicationContext is android.app.Application,
        )
        assertEquals("com.cols.launcher", context.packageName)
    }

    @Test
    fun `framework resources resolve without an emulator`() {
        assertEquals("COLS", context.getString(R.string.app_name))
    }
}
