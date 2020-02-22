package com.ch.redukt

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

sealed class CounterAction : Action {
  object ResetState : CounterAction()
  object IncrementCount : CounterAction()
  object DecrementCount : CounterAction()
  object IncrementWithCommand : CounterAction()
}

sealed class CounterCommand : Command {
  object Increment : CounterCommand()
}

class ReduktTests : StringSpec(
  {

    data class CounterState(val count: Int = 0)

    fun createStore(): Store<CounterState> =
      Store(
        CounterState(),
        mutableSetOf(
          { action, state ->
            when (action) {
              CounterAction.IncrementCount -> {
                Transition(state.copy(count = state.count + 1))
              }

              CounterAction.DecrementCount -> {
                Transition(state.copy(count = state.count - 1))
              }

              CounterAction.IncrementWithCommand -> {
                CounterCommand
                  .Increment
                  .singletonList()
                  .let {
                    Transition(state, it)
                  }
              }

              else -> Transition(state)
            }
          })
      )

    "increment increases count by one" {
      with(createStore()) {
        state.count shouldBe 0
        dispatch(CounterAction.IncrementCount)
        state.count shouldBe 1
      }
    }

    "side effect should increment count by one" {
      var commandReceived = false
      with(createStore()) {
        subscribe {
          it.commands.forEach { command ->
            when (command) {
              CounterCommand.Increment -> {
                commandReceived = true
                dispatch(CounterAction.IncrementCount)
              }
            }
          }
        }

        state.count shouldBe 0
        dispatch(CounterAction.IncrementCount)
        state.count shouldBe 1
        dispatch(CounterAction.IncrementWithCommand)
        state.count shouldBe 2
        commandReceived shouldBe true
      }
    }

    "can add and remove reducer to change behavior" {
      with(createStore()) {
        val resetReducer: Reduce<CounterState> = { event, state ->
          when (event) {
            is CounterAction.ResetState -> Transition(state.copy(count = 0))
            else -> Transition(state)
          }
        }

        removeReducer(resetReducer) shouldBe false
        state.count shouldBe 0
        dispatch(CounterAction.IncrementCount)
        state.count shouldBe 1
        dispatch(CounterAction.ResetState)
        state.count shouldBe 1
        addReducer(resetReducer) shouldBe true
        dispatch(CounterAction.ResetState)
        state.count shouldBe 0
        dispatch(CounterAction.IncrementCount)
        state.count shouldBe 1
        removeReducer(resetReducer) shouldBe true
        dispatch(CounterAction.ResetState)
        state.count shouldBe 1
        removeReducer(resetReducer) shouldBe false
      }
    }

    "temporal reducer" {
      with(createStore()) {
        val resetReducer: Reduce<CounterState> = { event, state ->
          when (event) {
            is CounterAction.ResetState -> Transition(state.copy(count = 0))
            else -> Transition(state)
          }
        }

        addReducer(TemporalReducer(resetReducer, state))
        state.count shouldBe 0
        dispatch(CounterAction.IncrementCount)
        state.count shouldBe 1
        dispatch(CounterAction.ResetState)
        state.count shouldBe 0
        dispatch(TemporalAction.Undo)
        state.count shouldBe 1
        dispatch(TemporalAction.Redo)
        state.count shouldBe 0
      }
    }
  })