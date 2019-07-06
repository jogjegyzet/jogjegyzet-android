package com.danielgergely.jogjegyzet.ui

import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.danielgergely.jogjegyzet.domain.Category
import com.danielgergely.jogjegyzet.service.DocumentData
import com.danielgergely.jogjegyzet.ui.category.CategoryController
import com.danielgergely.jogjegyzet.ui.reader.ReaderController
import com.danielgergely.jogjegyzet.util.Either

class AdapterClickListener(private val router: Router) {
    fun onAdapterClick(item: Either<Category, DocumentData>) {
        when (item) {
            is Either.Left ->
                router.pushController(
                        RouterTransaction.with(CategoryController(item.value.id))
                                .popChangeHandler(HorizontalChangeHandler())
                                .pushChangeHandler(HorizontalChangeHandler())
                )

            is Either.Right ->
                router.pushController(
                        RouterTransaction.with(ReaderController(item.value.document.id))
                                .popChangeHandler(HorizontalChangeHandler())
                                .pushChangeHandler(HorizontalChangeHandler())
                )
        }
    }
}