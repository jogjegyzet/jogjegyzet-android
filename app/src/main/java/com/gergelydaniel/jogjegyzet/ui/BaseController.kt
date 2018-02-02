package com.gergelydaniel.jogjegyzet.ui

import android.view.View
import com.bluelinelabs.conductor.rxlifecycle2.RxController
import com.christianbahl.conductor.ConductorInjection


abstract class BaseController : RxController() {
    private var injected = false

    init {
        retainViewMode = RetainViewMode.RELEASE_DETACH
    }

    override fun onAttach(view: View) {
        if(! injected) {
            ConductorInjection.inject(this)
            injected = true
        }
    }

}