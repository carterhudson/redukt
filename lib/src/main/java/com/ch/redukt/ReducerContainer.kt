package com.ch.redukt

/**
 * Behaviors for an object that holds many reducers
 */
interface ReducerContainer<StateT> {
  fun addReducer(reducer: Reduce<StateT>) : Boolean
  fun removeReducer(reducer: Reduce<StateT>) : Boolean
}