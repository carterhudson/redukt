package com.ch.redukt

/**
 * Represents a dispatch function
 */
typealias Dispatch = (Event) -> Unit

interface Dispatcher {
  val dispatch: Dispatch
}