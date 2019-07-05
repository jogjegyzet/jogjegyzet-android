package com.gergelydaniel.jogjegyzet.ui.reader

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.gergelydaniel.jogjegyzet.R
import com.gergelydaniel.jogjegyzet.ui.BaseController
import com.gergelydaniel.jogjegyzet.ui.appbar.MenuItem
import com.gergelydaniel.jogjegyzet.ui.category.CategoryController
import com.gergelydaniel.jogjegyzet.ui.document.DocumentController
import com.github.barteksc.pdfviewer.PDFView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.view_title.view.*
import java.net.URL


private const val KEY_URL = "url"
private const val KEY_TITLE = "title"
private const val KEY_ID = "id"

class ReaderController(private val url: String,
                       private val docTitle: String,
                       private val docId: String) : BaseController() {
    private lateinit var pdfView: PDFView

    constructor(args: Bundle) : this(args.getString(KEY_URL), args.getString(KEY_TITLE), args.getString(KEY_ID))

    override val title: Observable<String> = Observable.just(docTitle)
    override val icons: Observable<List<MenuItem>> = Observable.just(listOf(MenuItem(R.drawable.ic_info, R.string.info)))

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        pdfView = PDFView(inflater.context, null)
        pdfView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        pdfView.setBackgroundColor(0xFF666666.toInt())
        return pdfView
    }

    @SuppressLint("CheckResult")
    override fun onAttach(view: View) {
        super.onAttach(view)

        Observable.fromCallable { URL(url).openStream() }
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_URL, url)
        outState.putString(KEY_TITLE, docTitle)
    }

    override fun onMenuItemClick(index: Int) {
        router.pushController(
                RouterTransaction.with(DocumentController(docId))
                        .popChangeHandler(HorizontalChangeHandler())
                        .pushChangeHandler(HorizontalChangeHandler())
        )
    }
}
