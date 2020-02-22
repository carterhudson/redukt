package com.ch.redukt

/**
 * Represents a pure function responsible for calculating state changes and
 * producing a [Store.Transition] for subscribers to consume.
 */
typealias Reduce<StateT> = (action: Action, state: StateT) -> Transition<StateT>