package com.ch.redukt

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
sealed class CounterAction : Action {
  object Increment : CounterAction()
  object Decrement : CounterAction()
  object IncrementAsync : CounterAction()
}

sealed class CounterCommand : Command {
  object IncrementAsync : CounterCommand()
}

class ReduktTests : StringSpec(
  {

    data class TestState(val count: Int = 0)

    fun createStore(): Store<TestState> {
      return Store(
        TestState(), listOf { action, state ->
          when (action) {
            CounterAction.Increment -> Store.Transition(state.copy(count = state.count + 1))
            CounterAction.Decrement -> Store.Transition(state.copy(count = state.count - 1))
            CounterAction.IncrementAsync -> Store.Transition(
              state,
              CounterCommand.IncrementAsync
            )
            else -> Store.Transition(state)
          }
        }
      )
    }

    "increment increases count by one" {
      val store = createStore()

      var count = 0
      store.subscribe {
        (count == 0).ifTrue {
          it.toState.count shouldBe 0
        }.ifFalse {
          it.toState.count shouldBe 1
        }
        count++
      }

      store.dispatch(CounterAction.Increment)
    }

    "side effect increments count by one" {
      val store = createStore()

      var iteration = 0
      store.subscribe {
        (iteration == 0)
          .ifTrue {
            it.toState.count shouldBe 0
            iteration++
          }
          .ifFalse {
            it.toState.count shouldBe 1
          }

        it.commands.forEach { command ->
          when (command) {
            CounterCommand.IncrementAsync -> {
              store.dispatch(CounterAction.Increment)
            }
          }
        }
      }
    }

    "subscriber receives latest state on subscription" {
      val store = createStore()
      var count = 0
      store.subscribe {
        it.toState.count shouldBe 0
        count++
      }
      count shouldBe 1
    }

    "many subscribers can unsusbcribe" {
      val store = createStore()
      (1..10)
        .map {
          store.subscribe {
            //no-op
          }
        }
        .forEach {
          it.unsubscribe()
        }
    }
  }
)