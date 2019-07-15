package com.danielgergely.jogjegyzet.domain

sealed class UpdateMessage {
    object None: UpdateMessage()
    class OptionalUpdate(val message: String, val maxVersion: Int): UpdateMessage()
    class MustUpdate(val message: String, val maxVersion: Int): UpdateMessage()
}