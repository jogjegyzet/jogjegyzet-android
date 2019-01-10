package com.gergelydaniel.jogjegyzet.domain

import org.threeten.bp.LocalDateTime

data class Comment(val id: String,
                   val topic: String,
                   val user: String?,
                   val date: LocalDateTime,
                   val message: String)