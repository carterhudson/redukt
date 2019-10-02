package com.ch.redukt

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
sealed class CounterEvent : Event {
  object ResetRequested : CounterEvent()
  object IncrementRequested : CounterEvent()
  object DecrementRequested : CounterEvent()
  object IncrementAsCommandRequested : CounterEvent()
}

sealed class CounterCommand : Command {
  object Increment : CounterCommand()
}

class ReduktTests : StringSpec({

  data class CounterState(val count: Int = 0)

  fun createStore(): Store<CounterState> {
    return Store(
      CounterState(), mutableSetOf({ action, state ->
        when (action) {
          CounterEvent.IncrementRequested -> Transition(state.copy(count = state.count + 1))
          CounterEvent.DecrementRequested -> Transition(state.copy(count = state.count - 1))
          CounterEvent.IncrementAsCommandRequested -> Transition(
            state,
            CounterCommand.Increment
          )
          else -> Transition(state)
        }
      })
    )
  }

  "increment increases count by one" {
    with(createStore()) {
      state.count shouldBe 0
      dispatch(CounterEvent.IncrementRequested)
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
              dispatch(CounterEvent.IncrementRequested)
            }
          }
        }
      }

      state.count shouldBe 0
      dispatch(CounterEvent.IncrementRequested)
      state.count shouldBe 1
      dispatch(CounterEvent.IncrementAsCommandRequested)
      state.count shouldBe 2
      commandReceived shouldBe true
    }
  }

  "can add and remove reducer to change behavior" {
    with(createStore()) {
      val resetReducer: Reduce<CounterState> = { event, state ->
        when (event) {
          is CounterEvent.ResetRequested -> Transition(state.copy(count = 0))
          else -> Transition(state)
        }
      }

      removeReducer(resetReducer) shouldBe false
      state.count shouldBe 0
      dispatch(CounterEvent.IncrementRequested)
      state.count shouldBe 1
      dispatch(CounterEvent.ResetRequested)
      state.count shouldBe 1
      addReducer(resetReducer) shouldBe true
      dispatch(CounterEvent.ResetRequested)
      state.count shouldBe 0
      dispatch(CounterEvent.IncrementRequested)
      state.count shouldBe 1
      removeReducer(resetReducer) shouldBe true
      dispatch(CounterEvent.ResetRequested)
      state.count shouldBe 1
      removeReducer(resetReducer) shouldBe false
    }
  }
})