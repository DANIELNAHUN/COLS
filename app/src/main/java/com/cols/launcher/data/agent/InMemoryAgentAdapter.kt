package com.cols.launcher.data.agent

import com.cols.launcher.domain.agent.AgentAction
import com.cols.launcher.domain.agent.AgentOutcome
import com.cols.launcher.domain.agent.AgentPort
import com.cols.launcher.domain.agent.AgentRequest
import com.cols.launcher.domain.agent.Confirmation
import com.cols.launcher.domain.agent.ConfirmationPolicyContract

/**
 * Deterministic in-memory [AgentPort] implementation (D5 / agent-seam spec):
 * keeps the app buildable and testable WITHOUT any AI dependency. Per the
 * agent-seam spec this stub MUST NOT contain network calls, model
 * invocations, or credential handling — it returns fixed outcomes based only
 * on the request text.
 */
class InMemoryAgentAdapter(
    private val confirmationPolicy: ConfirmationPolicyContract,
) : AgentPort {

    override suspend fun respondTo(request: AgentRequest): AgentOutcome {
        // The bootstrap stub proposes one canonical consequential action for
        // any request that mentions acting on someone; everything else gets a
        // pure answer. The proposal arrives PRE-CONFIRMED: the confirmation
        // token is issued by the domain policy inside the flow, but execution
        // is still gated by AgentActionGateway.perform (confirmation must be
        // explicitly carried into the gateway).
        val action = actionFor(request.utterance)
        return if (action == null) {
            AgentOutcome.Answer(text = DETERMINISTIC_ANSWER)
        } else {
            AgentOutcome.ActionProposal(
                action = action,
                confirmation = confirmationPolicy.issueFor(action),
            )
        }
    }

    private fun actionFor(utterance: String): AgentAction? =
        if (CALL_KEYWORDS.any { utterance.lowercase().contains(it) }) {
            AgentAction(kind = ACTION_KIND, target = ACTION_TARGET)
        } else {
            null
        }

    private companion object {
        const val DETERMINISTIC_ANSWER = "stub: nothing to act on"
        const val ACTION_KIND = "stub_action"
        const val ACTION_TARGET = "stub_target"
        val CALL_KEYWORDS = listOf("call", "dial")
    }
}
