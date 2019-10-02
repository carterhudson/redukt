package com.ch.redukt


interface Observable<StateT> {
  fun subscribe(onTransition: (transition: Transition<StateT>) -> Unit) : Subscription
}