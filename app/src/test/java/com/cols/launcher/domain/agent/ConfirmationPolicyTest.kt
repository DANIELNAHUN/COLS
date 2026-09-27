package com.cols.launcher.domain.agent

// RED tests (task 2.4 / AC2): the confirmation-before-action rule is written
// and observed failing BEFORE the gate implementation exists. These tests are
// the acceptance criteria for AgentActionGateway + ConfirmationPolicy (D5).

import org.junit.Assert.assertTrue
import org.junit.Test

class ConfirmationPolicyTest {
    private val policy = ConfirmationPolicy()

    @Test
    fun `issueFor returns a confirmation token`() {
        val action = AgentAction("make_call", "mom")
        val confirmation = policy.issueFor(action)

        assertTrue("issued confirmation id must not be blank", confirmation.id.isNotBlank())
    }

    @Test
    fun `issued confirmation is valid for its action`() {
        val action = AgentAction("make_call", "mom")
        val confirmation = policy.issueFor(action)

        assertTrue(policy.isValid(confirmation, action))
    }

    @Test
    fun `null confirmation is never valid`() {
        val action = AgentAction("make_call", "mom")

        assertTrue("no confirmation must not validate", !policy.isValid(null, action))
    }
}
