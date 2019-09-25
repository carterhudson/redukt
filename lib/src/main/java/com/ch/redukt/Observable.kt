package com.ch.redukt


interface Observable<StateT> {
  fun subscribe(onTransition: (transition: Store.Transition<StateT>) -> Unit) : Subscription
}