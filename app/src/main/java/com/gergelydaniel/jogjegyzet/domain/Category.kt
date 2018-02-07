package com.gergelydaniel.jogjegyzet.domain

data class Category(val id: String,
                    val name: String,
                    val parentId: String?)