package com.cols.launcher.domain.agent

/**
 * Provider-agnostic agent port (D5 / agent-seam spec): the ONLY seam between
 * the app and any future AI capability. No vendor, model, API-key, or
 * transport concept may appear in its name, signatures, or surrounding code.
 */
interface AgentPort {
    suspend fun respondTo(request: AgentRequest): AgentOutcome
}

data class AgentRequest(
    val utterance: String,
)

sealed interface AgentOutcome {
    /** A pure-informative agent response with no consequential effect. */
    data class Answer(
        val text: String,
    ) : AgentOutcome

    /** An agent-proposed consequential action: execution requires confirmation. */
    data class ActionProposal(
        val action: AgentAction,
        val confirmation: Confirmation,
    ) : AgentOutcome
}

/** Opaque, single-use confirmation token issued by the ConfirmationPolicy. */
data class Confirmation(
    val id: String,
)
