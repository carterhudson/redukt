package com.ch.redukt

/**
 * The single source of truth for application state. Contains a list of [subscribers]
 * and receives [dispatch]ed events.
 */
data class Store<StateT>(
  private val initialState: StateT,
  private val reducers: MutableSet<Reduce<StateT>>
) : Dispatcher, Observable<StateT>, ReducerContainer<StateT> {

  var state: StateT = initialState
    private set

  private val subscribers: MutableList<Subscribe<StateT>> = mutableListOf()

  /**
   * Adds [onTransition] to the list of [subscribers], emits the latest state as a [Transition],
   * and returns a [Subscription] that can be used to [Subscription.unsubscribe]
   */
  override fun subscribe(onTransition: (transition: Transition<StateT>) -> Unit): Subscription =
    subscribers
      .add(onTransition)
      .let {
        object : Subscription {
          override fun unsubscribe() {
            subscribers.remove(onTransition)
          }
        }
      }

  /**
   * Receives events. Feeds each event to [reducers] and notifies
   * subscribers of new [Transition]s
   */
  override val dispatch: Dispatch = { action: Action ->
    reducers.forEach { reduce ->
      with(reduce(action, state)) {
        state = toState
        subscribers.forEach { it(this) }
      }
    }
  }

  override fun addReducer(reducer: Reduce<StateT>): Boolean = reducers.add(reducer)

  override fun removeReducer(reducer: Reduce<StateT>): Boolean = reducers.remove(reducer)
}
