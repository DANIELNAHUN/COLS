package com.cols.launcher.domain.agent

// RED tests (task 2.4 / AC2): the consequential-action boundary is written and
// observed failing BEFORE the gateway implementation exists.RULE under test:
// no valid confirmation, no execution.

import com.cols.launcher.data.agent.InMemoryAgentActionGateway
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AgentActionGatewayTest {

    private val policy = ConfirmationPolicy()
    private val gateway: AgentActionGateway = InMemoryAgentActionGateway(policy)

    @Test
    fun `perform without confirmation reports ConfirmationRequired and does not execute`() = runTest {
        val action = AgentAction("make_call", "mom")

        val result = gateway.perform(action, confirmation = null)

        assertEquals(ActionExecutionResult.ConfirmationRequired, result)
    }

    @Test
    fun `perform with valid confirmation executes and returns a receipt`() = runTest {
        val action = AgentAction("make_call", "mom")
        val confirmation = policy.issueFor(action)

        val result = gateway.perform(action, confirmation)

        assertTrue("expected Executed but was $result", result is ActionExecutionResult.Executed)
    }

    @Test
    fun `perform with a reused confirmation is refused`() = runTest {
        val action = AgentAction("make_call", "mom")
        val confirmation = policy.issueFor(action)

        gateway.perform(action, confirmation) // first use burns the token

        val second = gateway.perform(action, confirmation)
        assertEquals(ActionExecutionResult.ConfirmationRequired, second)
    }

    @Test
    fun `confirmation issued for another action is refused`() = runTest {
        val otherAction = AgentAction("send_message", "dad")
        val confirmation = policy.issueFor(otherAction)

        val action = AgentAction("make_call", "mom")
        val result = gateway.perform(action, confirmation)

        assertEquals(ActionExecutionResult.ConfirmationRequired, result)
    }
}
