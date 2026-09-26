package com.cols.launcher.data.agent

import com.cols.launcher.domain.agent.AgentAction
import com.cols.launcher.domain.agent.AgentActionGateway
import com.cols.launcher.domain.agent.ActionExecutionResult
import com.cols.launcher.domain.agent.Confirmation
import com.cols.launcher.domain.agent.ConfirmationPolicyContract

/**
 * In-memory adapter for the consequential-action boundary (D5 / agent-seam
 * spec). Structurally refuses execution without a valid confirmation: the
 * flow returns [ActionExecutionResult.ConfirmationRequired] and the action is
 * NOT executed. A valid token is single-use — it is burned on execution, so a
 * reused token is refused.
 */
class InMemoryAgentActionGateway(
    private val confirmationPolicy: ConfirmationPolicyContract,
) : AgentActionGateway {

    private val executedActions = mutableListOf<AgentAction>()

    /** Inspectable record of what actually executed (test/diagnostic affordance). */
    val executed: List<AgentAction> get() = executedActions.toList()

    override suspend fun perform(action: AgentAction, confirmation: Confirmation?): ActionExecutionResult {
        if (!confirmationPolicy.isValid(confirmation, action)) {
            // Domain rule: no valid confirmation, no execution.
            return ActionExecutionResult.ConfirmationRequired
        }
        // Single-use: burn the token right before the action executes.
        confirmationPolicy.consume(confirmation!!)
        executedActions.add(action)
        return ActionExecutionResult.Executed(receipt = deterministicReceipt(action))
    }

    private fun deterministicReceipt(action: AgentAction): String =
        "executed ${action.kind} on ${action.target}"
}
