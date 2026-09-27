package com.cols.launcher.domain.agent

/**
 * The confirmation-before-action domain rule (D5 / agent-seam spec): it lives
 * HERE, in domain code — never in UI — so every future agent adapter inherits
 * it automatically. Each issued token is single-use (the gateway burns it on
 * execution) and bound to the action it was issued for.
 */
interface ConfirmationPolicyContract {
    fun issueFor(action: AgentAction): Confirmation

    fun isValid(
        confirmation: Confirmation?,
        action: AgentAction,
    ): Boolean

    /**
     * Single-use enforcement: burns a previously issued token so the same
     * confirmation can never authorize a second execution.
     */
    fun consume(confirmation: Confirmation)
}

class ConfirmationPolicy : ConfirmationPolicyContract {
    private val issuedByAction = mutableMapOf<String, AgentAction>()

    override fun issueFor(action: AgentAction): Confirmation {
        val token = Confirmation(id = newTokenId())
        issuedByAction[token.id] = action
        return token
    }

    override fun isValid(
        confirmation: Confirmation?,
        action: AgentAction,
    ): Boolean {
        // No confirmation at all is structurally invalid — nothing has granted
        // this action.
        if (confirmation == null) return false
        // A token never issued by this policy grants nothing.
        val boundAction = issuedByAction[confirmation.id] ?: return false
        // A token issued for a DIFFERENT action does not authorize this one.
        return boundAction == action
    }

    override fun consume(confirmation: Confirmation) {
        issuedByAction.remove(confirmation.id)
    }

    private fun newTokenId(): String = "confirm-${tokenCounter++}"

    private companion object {
        var tokenCounter: Int = 0
    }
}
