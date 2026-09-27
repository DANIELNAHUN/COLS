package com.cols.launcher.domain.agent

/**
 * A consequential action proposed by the agent whose execution must be gated
 * behind an explicit confirmation step (agent-seam spec). The name/kind pair is
 * deliberately generic: no provider, model, or transport concept appears here.
 */
data class AgentAction(val kind: String, val target: String)
