package com.gergelydaniel.jogjegyzet.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gergelydaniel.jogjegyzet.R
import com.gergelydaniel.jogjegyzet.ui.BaseController
import kotlinx.android.synthetic.main.controller_main.view.*
import javax.inject.Inject

class HomeController : BaseController() {
    @Inject
    internal lateinit var presenter : HomePresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.controller_main, container, false)
    }

    override fun onAttach(view: View) {
        super.onAttach(view)

        presenter.data()
                .compose(bindToLifecycle())
                .subscribe(::render)
    }

    fun render(vm: String) {
        view!!.textView.text = vm
    }

}