package com.gergelydaniel.jogjegyzet.ui

import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.gergelydaniel.jogjegyzet.domain.Category
import com.gergelydaniel.jogjegyzet.domain.Document
import com.gergelydaniel.jogjegyzet.ui.category.CategoryController
import com.gergelydaniel.jogjegyzet.ui.document.DocumentController
import com.gergelydaniel.jogjegyzet.util.Either

class AdapterClickListener(private val router: Router) {
    fun onAdapterClick(item: Either<Category, Document>) {
        when (item) {
            is Either.Left -> {
                router.pushController(
                        RouterTransaction.with(CategoryController(item.value.id))
                                .popChangeHandler(HorizontalChangeHandler())
                                .pushChangeHandler(HorizontalChangeHandler())
                )
            }
            is Either.Right -> {
                router.pushController(
                        RouterTransaction.with(DocumentController(item.value))
                                .popChangeHandler(HorizontalChangeHandler())
                                .pushChangeHandler(HorizontalChangeHandler())
                )
            }
        }
    }
}