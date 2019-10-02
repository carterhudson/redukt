package com.ch.redukt

/**
 * Represents the state machine's output. Reducers remain pure by packaging
 * [toState] and [commands] into the object.
 */
class Transition<StateT>(
  val toState: StateT,
  vararg val commands: Command
)