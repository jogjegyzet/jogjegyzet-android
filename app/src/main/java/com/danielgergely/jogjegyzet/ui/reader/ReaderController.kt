package com.danielgergely.jogjegyzet.ui.reader

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.christianbahl.conductor.ConductorInjection
import com.danielgergely.jogjegyzet.R
import com.danielgergely.jogjegyzet.ui.BaseController
import com.danielgergely.jogjegyzet.ui.appbar.MenuItem
import com.danielgergely.jogjegyzet.ui.document.DocumentController
import com.danielgergely.jogjegyzet.util.hide
import com.danielgergely.jogjegyzet.util.show
import com.google.android.material.snackbar.Snackbar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.controller_reader.view.*
import javax.inject.Inject


private const val KEY_ID = "id"

class ReaderController(private val id: String) : BaseController() {
    @Inject
    internal lateinit var presenter: ReaderPresenter

    private var isInFavourites: Boolean? = null
    private var url: String? = null

    constructor(args: Bundle) : this(args.getString(KEY_ID)!!)

    override val title: BehaviorSubject<String> = BehaviorSubject.create()
    override val icons: BehaviorSubject<List<MenuItem>> = BehaviorSubject.create()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.controller_reader, container, false)
    }

    override fun onFirstAttach() {
        ConductorInjection.inject(this)
    }

    @SuppressLint("CheckResult")
    override fun onAttach(view: View) {
        super.onAttach(view)

        url = null

        presenter.getViewModel(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(::render)
    }

    @SuppressLint("CheckResult")
    private fun render(vm: ViewModel) {
        val view = view!!
        when (vm) {
            is ViewModel.Loading -> {
                view.reader_error.hide()
                view.pdf_view.hide()

                view.reader_progress.show()
            }
            is ViewModel.Error -> {
                view.reader_progress.hide()
                view.pdf_view.hide()

                view.reader_error.show()
            }
            is ViewModel.Data -> {
                view.reader_progress.hide()
                view.reader_error.hide()

                view.pdf_view.show()

                if (url != vm.document.document.fileUrl) {
                    view.pdf_view.fromStream(vm.stream)
                            .spacing(8)
                            .onPageError { i, t ->
                                t.printStackTrace()
                            }
                            .load()
                }

                title.onNext(vm.document.document.name)

                isInFavourites = vm.document.isInFavorites
                url = vm.document.document.fileUrl

                val favoritesMenuItem = if (vm.document.isInFavorites) {
                    MenuItem(R.drawable.ic_star_border, R.string.remove_from_favorites)
                } else {
                    MenuItem(R.drawable.ic_star, R.string.add_to_favorites)
                }

                val menuItems = listOf(favoritesMenuItem, MenuItem(R.drawable.ic_info, R.string.info))

                icons.onNext(menuItems)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_ID, id)
    }

    override fun onMenuItemClick(index: Int) {
        when (index) {
            0 -> {
                val inFavs = isInFavourites
                if (inFavs != null) {
                    val actionText: Int

                    val obs = if (inFavs) {
                        actionText = R.string.removed_from_favs
                        presenter.removeFromFavorites()
                    } else {
                        actionText = R.string.added_to_favs
                        presenter.addToFavorites()
                    }

                    obs.subscribe { undoAction ->
                        Snackbar.make(view!!, actionText, 5000)
                                .setAction(R.string.undo) {
                                    undoAction.subscribe()
                                }
                                .show()
                    }
                }
            }
            1 -> router.pushController(
                    RouterTransaction.with(DocumentController(id))
                            .popChangeHandler(HorizontalChangeHandler())
                            .pushChangeHandler(HorizontalChangeHandler()))

        }
    }
}
