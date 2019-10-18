package com.ch.redukt

sealed class TemporalEvent : Event {
  object UndefinedEvent : TemporalEvent()
  object Undo : TemporalEvent()
  object Redo : TemporalEvent()
}

class TemporalReducer<StateT>(
  private val reduce: Reduce<StateT>,
  initialState: StateT
) : Reduce<StateT> {

  private var past: MutableList<StateT> = mutableListOf()
  private var present: StateT = reduce(TemporalEvent.UndefinedEvent, initialState).toState
  private var future: MutableList<StateT> = mutableListOf()

  override fun invoke(event: Event, state: StateT): Transition<StateT> {
    return when (event) {
      is TemporalEvent.Undo -> {
        future.add(present)
        present = past.last()
        past.isEmpty().not().ifTrue { past.dropLast(1) }

        return Transition(present)
      }

      is TemporalEvent.Redo -> {
        past.add(present)
        present = future.last()
        future.isEmpty().not().ifTrue { future.dropLast(1) }

        return Transition(present)
      }

      else -> {
        reduce(event, state).also {
          future = mutableListOf()
          past.add(present)
          present = it.toState
        }
      }
    }
  }
}