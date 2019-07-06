package com.danielgergely.jogjegyzet.domain

import org.threeten.bp.LocalDateTime

data class Document(val id: String,
                    val categoryId: String?,
                    val fileUrl: String,
                    val name: String,
                    val date: LocalDateTime,
                    val size: Long,
                    val downloads: Int,
                    val desc: String?,
                    val posRatings: Int,
                    val negRatings: Int)