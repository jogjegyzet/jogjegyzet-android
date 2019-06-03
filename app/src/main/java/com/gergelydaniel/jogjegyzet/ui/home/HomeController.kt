package com.gergelydaniel.jogjegyzet.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.christianbahl.conductor.ConductorInjection
import com.gergelydaniel.jogjegyzet.R
import com.gergelydaniel.jogjegyzet.domain.NoInternetException
import com.gergelydaniel.jogjegyzet.service.DocumentData
import com.gergelydaniel.jogjegyzet.ui.AdapterClickListener
import com.gergelydaniel.jogjegyzet.ui.BaseController
import com.gergelydaniel.jogjegyzet.ui.adapter.BrowserAdapter
import com.gergelydaniel.jogjegyzet.ui.adapter.ViewHolder
import com.gergelydaniel.jogjegyzet.util.Either
import com.gergelydaniel.jogjegyzet.util.vis
import kotlinx.android.synthetic.main.controller_home.view.*
import javax.inject.Inject

class HomeController : BaseController() {
    @Inject
    lateinit var presenter: HomePresenter

    private lateinit var categoriesAdapter: CardBrowserAdapter
    private lateinit var favoritesAdapter: CardBrowserAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.controller_home, container, false)
    }

    override fun onFirstAttach() {
        ConductorInjection.inject(this)
    }

    @SuppressLint("CheckResult")
    override fun onAttach(view: View) {
        super.onAttach(view)

        categoriesAdapter = CardBrowserAdapter()
        view.list_categories.adapter = categoriesAdapter

        val adapterClickListener = AdapterClickListener(router)::onAdapterClick
        categoriesAdapter.onClickListener = adapterClickListener

        favoritesAdapter = CardBrowserAdapter()
        view.list_favorites.adapter = favoritesAdapter

        favoritesAdapter.onClickListener = adapterClickListener

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
        view.card_favorites.vis = vm.categories is CategoriesViewModel.Data && vm.favorites.isNotEmpty()

        view.card_categories.vis = vm.categories is CategoriesViewModel.Data

        view.error.vis = vm.categories is CategoriesViewModel.Error
        view.empty.vis = vm.categories is CategoriesViewModel.Data && vm.categories.categories.isEmpty()

        when (vm.categories) {
            is CategoriesViewModel.Loading -> { }

            is CategoriesViewModel.Data -> {
                categoriesAdapter.data = vm.categories.categories.map { Either.Left(it) }
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

        favoritesAdapter.data = vm.favorites.map { Either.Right(DocumentData(it, true)) }
    }

    override fun onDetach(view: View) {
        super.onDetach(view)

        categoriesAdapter.onClickListener = null
        favoritesAdapter.onClickListener = null
        view.error_retry.setOnClickListener(null)
    }

}

private class CardBrowserAdapter: BrowserAdapter() {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.itemView.setBackgroundColor(0xFFFFFFFF.toInt())
    }
}