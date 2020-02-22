package com.ch.redukt

/**
 * Represents a dispatch function
 */
typealias Dispatch = (Action) -> Unit

interface Dispatcher {
  val dispatch: Dispatch
}