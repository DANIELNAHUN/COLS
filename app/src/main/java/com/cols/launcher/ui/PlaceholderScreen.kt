package com.cols.launcher.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/**
 * Material 3 smoke placeholder screen (task 2.10, D7): title text + ONE
 * interactive element meeting the codified RQ3 floor — touch target ≥ 48dp
 * and a semantics label. No launcher, call, contacts, or caregiver features:
 * this screen exists only to prove the Compose render path.
 *
 * `onSampleAction` is the smoke hook for the agent seam composition root
 * (MainActivity); the placeholder itself stays agent-agnostic.
 */
@Composable
fun PlaceholderScreen(
    onPlaceholderTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "COLS",
                style = MaterialTheme.typography.headlineLarge,
            )
            Button(
                onClick = onPlaceholderTap,
                // 48dp meets the Material/RQ3 minimum touch-target floor; a
                // senior-specific 64dp floor is a future product decision.
                modifier =
                    Modifier
                        .padding(top = 8.dp)
                        .size(width = 120.dp, height = 48.dp)
                        .semantics { contentDescription = "Placeholder action button" },
            ) {
                Text(text = "Placeholder")
            }
        }
    }
}
