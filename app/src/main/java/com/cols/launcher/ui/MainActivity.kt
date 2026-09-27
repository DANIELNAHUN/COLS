package com.cols.launcher.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.lifecycleScope
import com.cols.launcher.data.agent.InMemoryAgentActionGateway
import com.cols.launcher.data.agent.InMemoryAgentAdapter
import com.cols.launcher.domain.agent.AgentActionGateway
import com.cols.launcher.domain.agent.AgentOutcome
import com.cols.launcher.domain.agent.AgentPort
import com.cols.launcher.domain.agent.ConfirmationPolicy
import kotlinx.coroutines.launch

/**
 * Composition root (task 2.11, D5): binds the agent-seam interfaces to their
 * in-memory adapters. NO DI framework — manual wiring in one place, so the
 * future `:agent` extraction stays a file move.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val agentPort: AgentPort = InMemoryAgentAdapter(ConfirmationPolicy())
        val actionGateway: AgentActionGateway = InMemoryAgentActionGateway(ConfirmationPolicy())

        setContent {
            MaterialTheme {
                PlaceholderScreen(
                    onPlaceholderTap = {
                        // Smoke path ONLY: proves the seam is reachable from
                        // the UI without executing any consequential action.
                        // (Placeholder scope guard: no launcher/call feature.)
                        lifecycleScope.launch {
                            val outcome: AgentOutcome =
                                agentPort.respondTo(
                                    com.cols.launcher.domain.agent
                                        .AgentRequest(utterance = "call mom"),
                                )
                            if (outcome is AgentOutcome.ActionProposal) {
                                // Bootstrap smoke draft: proposes a call and
                                // requests a stub number — deliberately does
                                // NOT confirm/execute the gateway path.
                            }
                        }
                    },
                )
            }
        }
    }
}
