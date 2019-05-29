package com.gergelydaniel.jogjegyzet.ui.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.christianbahl.conductor.ConductorInjection
import com.gergelydaniel.jogjegyzet.R
import com.gergelydaniel.jogjegyzet.domain.NoInternetException
import com.gergelydaniel.jogjegyzet.domain.SearchResult
import com.gergelydaniel.jogjegyzet.ui.AdapterClickListener
import com.gergelydaniel.jogjegyzet.ui.BaseController
import com.gergelydaniel.jogjegyzet.ui.adapter.BrowserAdapter
import com.gergelydaniel.jogjegyzet.ui.category.CategoryController
import com.gergelydaniel.jogjegyzet.ui.document.DocumentController
import com.gergelydaniel.jogjegyzet.util.Either
import com.gergelydaniel.jogjegyzet.util.vis
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.controller_main.view.*
import javax.inject.Inject

private const val KEY_QUERY = "QUERY"
class SearchController : BaseController() {
    @Inject
    internal lateinit var presenter : SearchPresenter

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: BrowserAdapter

    private var viewModelSub : Disposable? = null

    private var _query: String? = null

    var query : String
        get() = _query ?: throw IllegalStateException()
        set(value) {
            _query = value
            subscribe(value)
        }

    init {
        Log.i("ASD", "init")
    }

    private fun subscribe(query: String) {
        viewModelSub?.dispose()

        viewModelSub = presenter.getViewModel(query)
                .compose(bindToLifecycle())
                .subscribe(::render)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        Log.i("ASD", "onCreateView")
        return inflater.inflate(R.layout.controller_main, container, false)
    }

    override fun onFirstAttach() {
        ConductorInjection.inject(this)
        Log.i("ASD", "onFirstAttach")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.i("ASD", "onSave")
        outState.putString(KEY_QUERY, query)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.i("ASD", "onRestore")

        query = savedInstanceState.getString(KEY_QUERY)
    }

    @SuppressLint("CheckResult")
    override fun onAttach(view: View) {
        super.onAttach(view)

        Log.i("ASD", "onAttach")


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
        view.empty.vis = vm is ViewModel.Empty

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
            is ViewModel.Error -> {
                view.error_text.setText(
                        when (vm.error) {
                            is NoInternetException -> R.string.nointernet
                            else -> R.string.search_error
                        }
                )
            }
        }
    }
}