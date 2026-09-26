package com.cols.launcher.data.agent

// Stub-contract test (task 2.9 / agent-seam spec): the in-memory adapter must
// return deterministic outcomes through AgentPort and perform NO network,
// file, or credential access. Determinism is proven by repeated identical
// runs; the no-effects property is structural — the adapter's only dependency
// is the ConfirmationPolicy (no network/file/credential type appears in its
// constructor or body).

import com.cols.launcher.domain.agent.AgentAction
import com.cols.launcher.domain.agent.AgentOutcome
import com.cols.launcher.domain.agent.AgentRequest
import com.cols.launcher.domain.agent.ConfirmationPolicy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class InMemoryAgentAdapterTest {
    private val policy = ConfirmationPolicy()

    private fun adapter(): InMemoryAgentAdapter = InMemoryAgentAdapter(policy)

    @Test
    fun `non-action request answers deterministically`() =
        runTest {
            val outcome = adapter().respondTo(AgentRequest("what time is it"))

            assertEquals(AgentOutcome.Answer(text = "stub: nothing to act on"), outcome)
        }

    @Test
    fun `action request proposes the canonical action with a confirmation`() =
        runTest {
            val outcome = adapter().respondTo(AgentRequest("call mom"))

            assertTrue("expected ActionProposal but was $outcome", outcome is AgentOutcome.ActionProposal)
            val proposal = outcome as AgentOutcome.ActionProposal
            assertEquals(AgentAction(kind = "stub_action", target = "stub_target"), proposal.action)
            assertTrue(proposal.confirmation.id.isNotBlank())
        }

    @Test
    fun `repeated calls are deterministic`() =
        runTest {
            val adapter = adapter()

            val first = adapter.respondTo(AgentRequest("call mom"))
            val second = adapter.respondTo(AgentRequest("call mom"))

            assertEquals(
                (first as AgentOutcome.ActionProposal).action,
                (second as AgentOutcome.ActionProposal).action,
            )
        }
}
