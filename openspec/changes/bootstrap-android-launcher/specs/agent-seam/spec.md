# Agent Seam Specification

## Purpose

Define the architectural seam for COLS's future provider-agnostic AI assistant: a Kotlin interface (port) in the domain layer, an in-memory stub adapter, and a confirmation-before-action domain rule. This bootstrap creates the seam ONLY — no model, API-key, network, RAG, or web-agent code exists anywhere in this change. The seam guarantees that when real agent work starts (future change informed by RQ6), module extraction and provider replacement stay mechanical.

## Requirements

### Requirement: Provider-agnostic agent port

The `:app` domain layer SHALL expose an agent port — a Kotlin interface that abstracts every AI capability the launcher may eventually consume. The interface MUST be provider-agnostic: nothing in its name, signatures, or surrounding code may reference a concrete model vendor, on-device model, API key mechanism, or network transport.

#### Scenario: Port definition contains no provider specifics

- GIVEN the scaffolded agent seam
- WHEN the port interface is inspected
- THEN it exposes only domain-level operations expressible without any provider knowledge
- AND no model vendor, API-key, or transport concept appears in its declaration

#### Scenario: Domain code depends only on the port

- GIVEN any domain-layer consumer of agent functionality
- WHEN its dependencies are inspected
- THEN it depends only on the port interface, never on an adapter implementation

### Requirement: In-memory stub adapter

The seam SHALL include one adapter implementing the port with deterministic in-memory behavior. The stub exists to keep the app buildable and testable without any AI dependency; it MUST NOT contain network calls, model invocations, or credential handling.

#### Scenario: Stub satisfies the port without external effects

- GIVEN the in-memory stub adapter
- WHEN it is exercised through the port by a test or the smoke path
- THEN it returns deterministic results with no network, file, or credential access

#### Scenario: Stub is substitutable

- GIVEN the port interface
- WHEN the stub adapter is replaced by a test double or a future real adapter
- THEN consumers continue to compile and behave against the port without modification

### Requirement: Confirmation-before-action rule

The domain SHALL enforce a confirmation-before-action rule: any consequential action originating from agent output MUST NOT be executed without an explicit confirmation step in the domain flow. The rule lives in domain logic, not in UI code, so every future agent implementation inherits it. In the bootstrap the rule is demonstrated by the port/stub contract itself (requests that would act are gated behind an explicit confirmation boundary).

#### Scenario: Action request without confirmation is not executed

- GIVEN an agent-driven request that implies a consequential action
- WHEN the request reaches the domain layer without an explicit confirmation
- THEN the domain does not execute the action
- AND the flow reports that confirmation is required

#### Scenario: Action request with confirmation proceeds

- GIVEN the same request
- WHEN an explicit confirmation is present in the domain flow
- THEN the domain proceeds with the action path defined by the port contract

### Requirement: No AI feature code in the bootstrap

The change SHALL NOT include: model binaries or downloads, on-device inference code, API-key storage or handling, network clients for AI services, RAG/index code, web-agent code, or any launcher feature calling agent functionality. Enforcing this requirement is a scope guard verified at review and verify time.

#### Scenario: Audit for prohibited agent code

- GIVEN the complete change diff
- WHEN the diff is audited for AI/network/credential code
- THEN no model, API-key, network, RAG, or web-agent implementation exists
- AND the only agent-related artifacts are the port interface, the stub adapter, and the confirmation rule

#### Scenario: Build proves no network dependency in the seam

- GIVEN the `:app` module build configuration
- WHEN its dependencies are inspected
- THEN no AI SDK, model runtime, or agent networking library is declared
