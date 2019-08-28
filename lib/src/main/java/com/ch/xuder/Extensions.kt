package com.ch.xuder


fun Boolean.ifTrue(block: () -> Unit): Boolean {
  if (this) {
    block()
    return this
  }

  return this
}

fun Boolean.ifFalse(block: () -> Unit): Boolean {
  if (!this) {
    block()
    return this
  }

  return this
}