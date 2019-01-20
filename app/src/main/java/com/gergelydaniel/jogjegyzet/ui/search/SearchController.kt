package com.gergelydaniel.jogjegyzet.ui.search

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
import com.gergelydaniel.jogjegyzet.domain.SearchResult
import com.gergelydaniel.jogjegyzet.ui.BaseController
import com.gergelydaniel.jogjegyzet.ui.adapter.BrowserAdapter
import com.gergelydaniel.jogjegyzet.ui.category.CategoryController
import com.gergelydaniel.jogjegyzet.ui.document.DocumentController
import com.gergelydaniel.jogjegyzet.util.Either
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

    override fun onAttach(view: View) {
        super.onAttach(view)

        Log.i("ASD", "onAttach")


        linearLayoutManager = LinearLayoutManager(view.context)
        view.recycler_view.layoutManager = linearLayoutManager
        adapter = BrowserAdapter()
        view.recycler_view.adapter = adapter

        adapter.onClickListener = {
            router.popController(this)
            when(it) {
                is Either.Left -> {
                    router.pushController(
                            RouterTransaction.with(CategoryController(it.value.id))
                                    .popChangeHandler(HorizontalChangeHandler())
                                    .pushChangeHandler(HorizontalChangeHandler())
                    )
                }
                is Either.Right -> {
                    router.pushController(
                            RouterTransaction.with(DocumentController(it.value))
                                    .popChangeHandler(HorizontalChangeHandler())
                                    .pushChangeHandler(HorizontalChangeHandler())
                    )
                }
            }
        }

        _query?.let (this::subscribe)
    }

    private fun render(vm: ViewModel) {
        val view = view!!
        when(vm) {
            is ViewModel.Loading -> {
                view.recycler_view.visibility = View.GONE
                view.category_progress.visibility = View.VISIBLE
                view.text.visibility = View.GONE
            }
            is ViewModel.Data -> {

                view.recycler_view.visibility = View.VISIBLE
                view.category_progress.visibility = View.GONE
                view.text.visibility = View.GONE

                adapter.data = vm.data.map {
                    when(it) {
                        is SearchResult.DocumentResult -> Either.Right(it.document)
                        is SearchResult.CategoryResult -> Either.Left(it.category)
                    }
                }
            }
            is ViewModel.Empty -> {
                view.recycler_view.visibility = View.GONE
                view.category_progress.visibility = View.GONE
                view.text.visibility = View.VISIBLE

                view.text.text = view.context.getString(R.string.noresult, _query)
            }
        }
    }
}