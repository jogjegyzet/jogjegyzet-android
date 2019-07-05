package com.gergelydaniel.jogjegyzet.ui.reader

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.christianbahl.conductor.ConductorInjection
import com.gergelydaniel.jogjegyzet.R
import com.gergelydaniel.jogjegyzet.service.DocumentData
import com.gergelydaniel.jogjegyzet.ui.BaseController
import com.gergelydaniel.jogjegyzet.ui.appbar.MenuItem
import com.gergelydaniel.jogjegyzet.ui.document.DocumentController
import com.github.barteksc.pdfviewer.PDFView
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.net.URL
import javax.inject.Inject


private const val KEY_ID = "id"

class ReaderController(private val id: String) : BaseController() {
    private lateinit var pdfView: PDFView

    @Inject
    internal lateinit var presenter: ReaderPresenter

    private var isInFavourites: Boolean? = null
    private var url: String? = null

    constructor(args: Bundle) : this(args.getString(KEY_ID)!!)

    override val title: BehaviorSubject<String> = BehaviorSubject.create()
    override val icons: BehaviorSubject<List<MenuItem>> = BehaviorSubject.create()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        pdfView = PDFView(inflater.context, null)
        pdfView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        pdfView.setBackgroundColor(0xFF666666.toInt())
        return pdfView
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
    private fun render(data: DocumentData) {
        if (url != data.document.fileUrl) {
            Observable.fromCallable { URL(data.document.fileUrl).openStream() }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(bindToLifecycle())
                    .subscribe {
                        pdfView.fromStream(it)
                                .spacing(8)
                                .onPageError { i, t ->
                                    t.printStackTrace()
                                }
                                .load()
                    }
        }

        title.onNext(data.document.name)

        isInFavourites = data.isInFavorites
        url = data.document.fileUrl

        val favoritesMenuItem = if (data.isInFavorites) {
            MenuItem(R.drawable.ic_star_border, R.string.remove_from_favorites)
        } else {
            MenuItem(R.drawable.ic_star, R.string.add_to_favorites)
        }

        val menuItems = listOf(favoritesMenuItem, MenuItem(R.drawable.ic_info, R.string.info))

        icons.onNext(menuItems)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_ID, id)
    }

    override fun onMenuItemClick(index: Int) {
        when(index) {
            0 -> {
                val inFavs = isInFavourites
                if (inFavs != null) {
                    val undoAction: Completable
                    val actionText: Int

                    val obs = if (inFavs) {
                        actionText = R.string.removed_from_favs
                        undoAction = presenter.undoRemoveFromFavourites()
                        presenter.removeFromFavorites()
                    } else {
                        actionText = R.string.added_to_favs
                        undoAction = presenter.undoAddToFavourites()
                        presenter.addToFavorites()
                    }

                    obs
                            .toObservable<Any>()
                            .compose(bindToLifecycle())
                            .doOnComplete {
                                Snackbar.make(view!!, actionText, 5000)
                                        .setAction(R.string.undo) {
                                            undoAction.subscribe()
                                        }
                                        .show()
                            }
                            .subscribe()
                }
            }
            1 -> router.pushController(
                        RouterTransaction.with(DocumentController(id))
                                .popChangeHandler(HorizontalChangeHandler())
                                .pushChangeHandler(HorizontalChangeHandler()))

        }
    }
}
