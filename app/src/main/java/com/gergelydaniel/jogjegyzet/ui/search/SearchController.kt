package com.gergelydaniel.jogjegyzet.ui.search

import android.support.v7.widget.LinearLayoutManager
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
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.controller_main.view.*
import javax.inject.Inject

class SearchController : BaseController() {
    @Inject
    internal lateinit var presenter : SearchPresenter

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: BrowserAdapter

    var viewModelSub : Disposable? = null

    var query : String = ""
        set(value) {
            field = value

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


    override fun onAttach(view: View) {
        super.onAttach(view)

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
                            RouterTransaction.with(DocumentController(it.value.id))
                                    .popChangeHandler(HorizontalChangeHandler())
                                    .pushChangeHandler(HorizontalChangeHandler())
                    )
                }
            }
        }
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

                view.text.text = view.context.getString(R.string.noresult, query)
            }
        }
    }
}