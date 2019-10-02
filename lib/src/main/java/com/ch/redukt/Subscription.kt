package com.ch.redukt

/**
 * Represents a subscription to the [Store] and contains logic necessary
 * to [unsubscribe]
 */
interface Subscription {
  fun unsubscribe()
}