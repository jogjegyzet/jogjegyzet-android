package com.gergelydaniel.jogjegyzet.ui

import android.view.View
import com.bluelinelabs.conductor.rxlifecycle2.RxController
import com.christianbahl.conductor.ConductorInjection
import com.gergelydaniel.jogjegyzet.ui.appbar.MenuItem
import io.reactivex.Observable


abstract class BaseController : RxController() {
    private var firstAttachrun = false

    abstract val title: Observable<String>
    open val icons: Observable<List<MenuItem>> = Observable.just(emptyList())

    init {
        retainViewMode = RetainViewMode.RELEASE_DETACH
    }

    override fun onAttach(view: View) {
        if(!firstAttachrun) {
            onFirstAttach()
            firstAttachrun = true
        }
    }

    protected open fun onFirstAttach() {}

    open fun onMenuItemClick(index: Int) {}
}