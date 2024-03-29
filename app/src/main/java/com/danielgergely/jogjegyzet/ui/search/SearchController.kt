package com.danielgergely.jogjegyzet.ui.search

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.Log
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
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.controller_main.view.*
import javax.inject.Inject

private const val KEY_QUERY = "QUERY"
class SearchController : BaseController() {
    @Inject
    internal lateinit var presenter : SearchPresenter

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: BrowserAdapter

    override val title: Observable<String> = Observable.just("")

    private var viewModelSub : Disposable? = null

    private var _query: String? = null

    var query : String
        get() = _query ?: throw IllegalStateException()
        set(value) {
            _query = value
            subscribe(value)
        }

    private fun subscribe(query: String) {
        viewModelSub?.dispose()

        viewModelSub = presenter.getViewModel(query)
                .compose(bindToLifecycle())
                .subscribe(::render)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.controller_main, container, false)
    }

    override fun onFirstAttach() {
        ConductorInjection.inject(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_QUERY, query)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        query = savedInstanceState.getString(KEY_QUERY)
    }

    @SuppressLint("CheckResult")
    override fun onAttach(view: View) {
        super.onAttach(view)

        linearLayoutManager = LinearLayoutManager(view.context)
        view.recycler_view.layoutManager = linearLayoutManager
        adapter = BrowserAdapter()
        view.recycler_view.adapter = adapter

        adapter.onClickListener = AdapterClickListener(router)::onAdapterClick

        RxView.clicks(view.error_retry)
                .compose(bindToLifecycle())
                .subscribe { presenter.retry() }

        _query?.let (this::subscribe)
    }

    override fun onDetach(view: View) {
        super.onDetach(view)
        adapter.onClickListener = null
    }

    private fun render(vm: ViewModel) {
        val view = view!!

        view.category_progress.vis = vm is ViewModel.Loading
        view.recycler_view.vis = vm is ViewModel.Data
        view.error.vis = vm is ViewModel.Error

        view.empty.vis = (vm is ViewModel.Empty) || (vm is ViewModel.EmtpySearch)

        when(vm) {
            is ViewModel.Loading -> { }

            is ViewModel.Data -> {
                adapter.data = vm.data.map {
                    when(it) {
                        is SearchResultViewModel.DocumentResult -> Either.Right(it.document)
                        is SearchResultViewModel.CategoryResult -> Either.Left(it.category)
                    }
                }
            }
            is ViewModel.Empty -> {
                view.empty.text = view.context.getString(R.string.noresult, _query)
            }
            is ViewModel.EmtpySearch -> {
                view.empty.text = view.context.getString(R.string.search_empty_query)
            }
            is ViewModel.Error -> {
                view.error_text.setText(
                        when (vm.error) {
                            is NoInternetException -> R.string.nointernet
                            else -> R.string.search_error
                        }
                )
                Log.e("Search", "ERROR", vm.error)
            }
        }
    }
}