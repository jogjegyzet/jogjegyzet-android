package com.danielgergely.jogjegyzet.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.christianbahl.conductor.ConductorInjection
import com.danielgergely.jogjegyzet.R
import com.danielgergely.jogjegyzet.domain.NoInternetException
import com.danielgergely.jogjegyzet.service.DocumentData
import com.danielgergely.jogjegyzet.ui.AdapterClickListener
import com.danielgergely.jogjegyzet.ui.BaseController
import com.danielgergely.jogjegyzet.ui.adapter.BrowserAdapter
import com.danielgergely.jogjegyzet.ui.adapter.ViewHolder
import com.danielgergely.jogjegyzet.util.Either
import com.danielgergely.jogjegyzet.util.vis
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.BehaviorSubject
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

    override val title: BehaviorSubject<String> = BehaviorSubject.create()

    @SuppressLint("CheckResult")
    override fun onAttach(view: View) {
        super.onAttach(view)

        title.onNext(view.context.getString(R.string.app_name))

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
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(::render)
    }

    private fun render(vm: ViewModel) {
        val view = view!!

        view.card_favorites.vis = vm.favorites.isNotEmpty()

        view.category_progress.vis = vm.categories is CategoriesViewModel.Loading
        view.list_categories.vis = vm.categories is CategoriesViewModel.Data

        view.error.vis = vm.categories is CategoriesViewModel.Error
        favoritesAdapter.data = vm.favorites.map { Either.Right(DocumentData(it, true)) }

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