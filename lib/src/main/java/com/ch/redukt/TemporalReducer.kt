package com.ch.redukt

sealed class TemporalAction : Action {
  object Undo : TemporalAction()
  object Redo : TemporalAction()
}

class TemporalReducer<StateT>(
  private val reduce: Reduce<StateT>,
  initialState: StateT
) : Reduce<StateT> {

  private var past: MutableList<StateT> = mutableListOf()
  private var present: StateT = initialState
  private var future: MutableList<StateT> = mutableListOf()

  override fun invoke(action: Action, state: StateT): Transition<StateT> {
    return when (action) {
      is TemporalAction.Undo -> {
        future.add(present)
        present = past.last()
        past.isEmpty().not().ifTrue { past.dropLast(1) }

        return Transition(present)
      }

      is TemporalAction.Redo -> {
        past.add(present)
        present = future.last()
        future.isEmpty().not().ifTrue { future.dropLast(1) }

        return Transition(present)
      }

      else -> {
        reduce(action, state).also {
          future = mutableListOf()
          past.add(present)
          present = it.toState
        }
      }
    }
  }
}