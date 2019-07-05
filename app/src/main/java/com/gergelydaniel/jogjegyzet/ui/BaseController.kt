package com.gergelydaniel.jogjegyzet.ui

import android.view.View
import com.bluelinelabs.conductor.rxlifecycle2.RxController
import com.christianbahl.conductor.ConductorInjection
import io.reactivex.Observable


abstract class BaseController : RxController() {
    private var firstAttachrun = false

    abstract val title: Observable<String>

    init {
        retainViewMode = RetainViewMode.RELEASE_DETACH
    }

    override fun onAttach(view: View) {
        if(!firstAttachrun) {
            onFirstAttach()
            firstAttachrun = true
        }
    }

    open fun onFirstAttach() {}

}