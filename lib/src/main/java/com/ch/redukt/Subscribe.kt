package com.ch.redukt

/**
 * Represents an active receiver of [Store.Transition]s
 */
typealias Subscribe<StateT> = (Transition<StateT>) -> Unit