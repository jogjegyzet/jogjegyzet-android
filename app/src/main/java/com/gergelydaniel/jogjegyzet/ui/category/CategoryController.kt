package com.gergelydaniel.jogjegyzet.ui.category

import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.christianbahl.conductor.ConductorInjection
import com.gergelydaniel.jogjegyzet.R
import com.gergelydaniel.jogjegyzet.ui.BaseController
import com.gergelydaniel.jogjegyzet.ui.adapter.BrowserAdapter
import com.gergelydaniel.jogjegyzet.ui.document.DocumentController
import com.gergelydaniel.jogjegyzet.util.Either
import kotlinx.android.synthetic.main.controller_main.view.*
import javax.inject.Inject

class CategoryController(private val catId: String? = null) : BaseController() {
    @Inject
    internal lateinit var presenter: CategoryPresenter

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: BrowserAdapter

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

        presenter.getViewModel(catId)
                .compose(bindToLifecycle())
                .subscribe(::render)
    }

    private fun render(vm: ViewModel) {
        when(vm) {
            is ViewModel.Loading -> {
                view!!.recycler_view.visibility = View.GONE
                view!!.category_progress.visibility = View.VISIBLE
                view!!.text.visibility = View.GONE
            }
            is ViewModel.Data -> {

                view!!.recycler_view.visibility = View.VISIBLE
                view!!.category_progress.visibility = View.GONE
                view!!.text.visibility = View.GONE

                adapter.data = vm.categories.map { Either.Left(it) }.plus(vm.documents.map { Either.Right(it) })
            }
        }
    }
}