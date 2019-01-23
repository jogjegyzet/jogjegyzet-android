package com.gergelydaniel.jogjegyzet.ui.home

import android.annotation.SuppressLint
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.christianbahl.conductor.ConductorInjection
import com.gergelydaniel.jogjegyzet.R
import com.gergelydaniel.jogjegyzet.domain.NoInternetException
import com.gergelydaniel.jogjegyzet.ui.AdapterClickListener
import com.gergelydaniel.jogjegyzet.ui.BaseController
import com.gergelydaniel.jogjegyzet.ui.adapter.BrowserAdapter
import com.gergelydaniel.jogjegyzet.util.Either
import com.gergelydaniel.jogjegyzet.util.vis
import kotlinx.android.synthetic.main.controller_home.view.*
import javax.inject.Inject

class HomeController : BaseController() {
    @Inject
    lateinit var presenter: HomePresenter

    private lateinit var adapter: BrowserAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.controller_home, container, false)
    }

    override fun onFirstAttach() {
        ConductorInjection.inject(this)
    }

    @SuppressLint("CheckResult")
    override fun onAttach(view: View) {
        super.onAttach(view)

        adapter = BrowserAdapter()
        view.list_categories.adapter = adapter

        adapter.onClickListener = AdapterClickListener(router)::onAdapterClick

        view.error_retry.setOnClickListener {
            presenter.retry()
        }

        presenter.getViewModel()
                .compose(bindToLifecycle())
                .subscribe(::render)
    }

    private fun render(vm: ViewModel) {
        val view = view!!

        view.category_progress.vis = vm.categories is CategoriesViewModel.Loading
        view.list_categories.vis = vm.categories is CategoriesViewModel.Data && vm.categories.categories.isNotEmpty()
        view.error.vis = vm.categories is CategoriesViewModel.Error
        view.empty.vis = vm.categories is CategoriesViewModel.Data && vm.categories.categories.isEmpty()

        when (vm.categories) {
            is CategoriesViewModel.Loading -> { }

            is CategoriesViewModel.Data -> {
                adapter.data = vm.categories.categories.map { Either.Left(it) }
            }
            is CategoriesViewModel.Error -> {
                view.error_text.setText(
                        when (vm.categories.error) {
                            is NoInternetException -> R.string.nointernet
                            else -> R.string.cat_error
                        }
                )
            }
        }

        var favs = vm.favorites.joinToString("\n") { it.name }
        if (favs.isEmpty()) favs = "Nincs kedvenc"
        view.text_favorites.text = favs
    }

    override fun onDetach(view: View) {
        super.onDetach(view)

        adapter.onClickListener = null
        view.error_retry.setOnClickListener(null)
    }

}