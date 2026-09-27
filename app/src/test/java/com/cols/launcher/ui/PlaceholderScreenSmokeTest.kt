package com.cols.launcher.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

/**
 * Compose component test (task 3.2 / app-testing spec "Compose component
 * test without a device"): renders the placeholder screen through the Compose
 * test runtime on Robolectric and asserts the codified RQ3 accessibility
 * floor - the title renders, and the interactive element reports BOTH a
 * >=48dp touch target (via `SemanticsNode.touchBoundsInRoot`) and a
 * semantics label (contentDescription).
 *
 * The rule launches the REAL [MainActivity] (declared with the standard
 * MAIN/LAUNCHER intent filter): Robolectric's PR 4736 refuses to resolve
 * the synthetic `androidx.activity.ComponentActivity` provided by
 * ui-test-manifest because it carries no intent filter. The activity hosts
 * [PlaceholderScreen] via its composition root, so no extra setContent call
 * is needed.
 *
 * `w400dp-h800dp` guarantees a window large enough for the smoke screen on
 * the JVM's default device; the 48dp comparison uses the same density that
 * laid the UI out, so it is density-consistent by construction.
 */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [35], qualifiers = "w400dp-h800dp")
class PlaceholderScreenSmokeTest {
    @get:Rule
    val composeRule = createAndroidComposeRule(MainActivity::class.java)

    @Test
    fun `placeholder screen renders the title text`() {
        composeRule.onNodeWithText("COLS").assertIsDisplayed()
    }

    @Test
    fun `placeholder interactive element exposes a semantics label`() {
        composeRule
            .onNodeWithContentDescription("Placeholder action button")
            .assertExists()
    }

    @Test
    fun `placeholder interactive element meets the 48dp touch target floor`() {
        val node =
            composeRule
                .onNodeWithContentDescription("Placeholder action button")
                .fetchSemanticsNode()
        val minTargetPx = with(composeRule.density) { 48.dp.toPx() }
        val bounds = node.touchBoundsInRoot

        assertTrue(
            "touch target ${bounds.width}x${bounds.height}px must be >= $minTargetPx px (48dp)",
            bounds.width >= minTargetPx - 0.5f && bounds.height >= minTargetPx - 0.5f,
        )
    }
}
