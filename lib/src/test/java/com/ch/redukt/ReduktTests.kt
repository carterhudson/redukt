package com.ch.redukt

import com.ch.redukt.Event
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
sealed class CounterEvent : Event {
  object IncrementRequested : CounterEvent()
  object DecrementRequested : CounterEvent()
  object IncrementAsyncRquested: CounterEvent()
}

sealed class CounterCommand: Command {
  object IncrementAsync : CounterCommand()
}

class ReduktTests : StringSpec({

  data class TestState(val count: Int = 0)

  fun createStore(): Store<TestState> {
    return Store(
      TestState(), listOf { action, state ->
        when (action) {
          CounterEvent.IncrementRequested -> Store.Transition(state.copy(count = state.count + 1))
          CounterEvent.DecrementRequested -> Store.Transition(state.copy(count = state.count - 1))
          CounterEvent.IncrementAsyncRquested -> Store.Transition(state, CounterCommand.IncrementAsync)
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

    store.dispatch(CounterEvent.IncrementRequested)
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
            store.dispatch(CounterEvent.IncrementRequested)
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
})