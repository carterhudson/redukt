package com.ch.redukt

/**
 * Represents the state machine's output. Reducers remain pure by packaging
 * [toState] and [commands] into the object.
 */
data class Transition<StateT>(
  val toState: StateT,
  val commands: List<Command> = emptyList()
)