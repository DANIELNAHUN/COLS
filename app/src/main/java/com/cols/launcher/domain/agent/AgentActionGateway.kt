package com.cols.launcher.domain.agent

/**
 * The consequential-action boundary (D5 / agent-seam spec). Domain rule:
 * no valid [Confirmation], no execution — enforced structurally, not by
 * convention, so every future agent implementation inherits it.
 */
interface AgentActionGateway {
    suspend fun perform(action: AgentAction, confirmation: Confirmation?): ActionExecutionResult
}

sealed interface ActionExecutionResult {
    /** The flow MUST surface this to the user as a confirmation prompt. */
    data object ConfirmationRequired : ActionExecutionResult

    /** The action executed successfully; [receipt] describes the outcome. */
    data class Executed(val receipt: String) : ActionExecutionResult
}
