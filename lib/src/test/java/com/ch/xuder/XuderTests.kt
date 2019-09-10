package com.ch.xuder

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class XuderTests : StringSpec({

  val INCREMENT = "increment"
  val INCREMENT_ASYNC = "increment_async"
  val DECREMENT = "decrement"
  val INCREMENT_SIDE_EFFECT = "increment_side_effect"

  data class TestState(val count: Int = 0)

  fun createStore(): Store<TestState> {
    return Store(
      TestState(), listOf { action, state ->
        when (action) {
          INCREMENT -> Store.Transition(state.copy(count = state.count + 1))
          DECREMENT -> Store.Transition(state.copy(count = state.count - 1))
          INCREMENT_ASYNC -> Store.Transition(state, INCREMENT_SIDE_EFFECT)
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

    store.dispatch(INCREMENT)
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

      it.commands.forEach { sideEffect ->
        when (sideEffect) {
          INCREMENT_SIDE_EFFECT -> {
            store.dispatch(INCREMENT)
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