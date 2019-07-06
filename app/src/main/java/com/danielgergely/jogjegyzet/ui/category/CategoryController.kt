package com.danielgergely.jogjegyzet.ui.category

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.christianbahl.conductor.ConductorInjection
import com.danielgergely.jogjegyzet.R
import com.danielgergely.jogjegyzet.domain.NoInternetException
import com.danielgergely.jogjegyzet.ui.AdapterClickListener
import com.danielgergely.jogjegyzet.ui.BaseController
import com.danielgergely.jogjegyzet.ui.adapter.BrowserAdapter
import com.danielgergely.jogjegyzet.util.Either
import com.danielgergely.jogjegyzet.util.vis
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.controller_main.view.*
import javax.inject.Inject

const val KEY_SCROLL = "SCROLL"

class CategoryController(val catId: String? = null) : BaseController() {
    @Inject
    internal lateinit var presenter: CategoryPresenter

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: BrowserAdapter

    private val titleSubject: BehaviorSubject<String> = BehaviorSubject.create()

    private var scrollState: Parcelable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.controller_main, container, false)
    }

    override fun onFirstAttach() {
        ConductorInjection.inject(this)
    }

    override val title: Observable<String>
        get() = titleSubject

    @SuppressLint("CheckResult")
    override fun onAttach(view: View) {
        super.onAttach(view)

        linearLayoutManager = LinearLayoutManager(view.context)
        view.recycler_view.layoutManager = linearLayoutManager
        adapter = BrowserAdapter()
        view.recycler_view.adapter = adapter

        view.empty.text = view.context.getString(R.string.emptycat)

        adapter.onClickListener = AdapterClickListener(router)::onAdapterClick

        RxView.clicks(view.error_retry)
                .compose(bindToLifecycle())
                .subscribe { presenter.retry() }

        presenter.getViewModel(catId)
                .compose(bindToLifecycle())
                .subscribe(::render)

        presenter.title
                .compose(bindToLifecycle())
                .subscribe { titleSubject.onNext(it) }
    }

    override fun onDetach(view: View) {
        super.onDetach(view)

        adapter.onClickListener = null
    }

    override fun onSaveViewState(view: View, outState: Bundle) {
        super.onSaveViewState(view, outState)
        val mListState = linearLayoutManager.onSaveInstanceState();
        outState.putParcelable(KEY_SCROLL, mListState)

    }

    override fun onRestoreViewState(view: View, savedViewState: Bundle) {
        super.onRestoreViewState(view, savedViewState)
        scrollState = savedViewState.getParcelable(KEY_SCROLL)
    }

    private fun render(vm: ViewModel) {
        val view = view!!

        view.category_progress.vis = vm is ViewModel.Loading
        view.recycler_view.vis = vm is ViewModel.NonEmpty
        view.error.vis = vm is ViewModel.Error
        view.empty.vis = vm is ViewModel.Empty

        when (vm) {
            is ViewModel.Loading -> { }

            is ViewModel.NonEmpty -> {
                adapter.data = vm.categories.map { Either.Left(it) }.plus(vm.documents.map { Either.Right(it) })
                restoreScrollState()
            }
            is ViewModel.Empty -> { }
            is ViewModel.Error -> {
                view.error_text.setText(
                        when (vm.error) {
                            is NoInternetException -> R.string.nointernet
                            else -> R.string.cat_error
                        }
                )
            }
        }
    }

    private fun restoreScrollState() {
        val scrollState = scrollState
        if (scrollState != null) {
            linearLayoutManager.onRestoreInstanceState(scrollState)
            this.scrollState = null
        }
    }
}