package com.gergelydaniel.jogjegyzet.ui.home

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class HomeFragment : Fragment() {
    @Inject
    lateinit var presenter: HomePresenter

    private val disposable = CompositeDisposable()

    init {
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return TextView(context)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        disposable.add(presenter.data().subscribe({ (view as TextView).text = it }))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable.dispose()
    }
}