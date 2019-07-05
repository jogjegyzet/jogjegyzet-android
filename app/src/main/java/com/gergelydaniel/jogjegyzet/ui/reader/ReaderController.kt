package com.gergelydaniel.jogjegyzet.ui.reader

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gergelydaniel.jogjegyzet.ui.BaseController
import com.github.barteksc.pdfviewer.PDFView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.net.URL


private const val KEY_URL = "url"
private const val KEY_TITLE = "title"

class ReaderController(private val url: String, private val docTitle: String) : BaseController() {
    private lateinit var pdfView: PDFView

    constructor(args: Bundle) : this(args.getString(KEY_URL), args.getString(KEY_TITLE))

    override val title: Observable<String>
        get() = Observable.just(docTitle)


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
}
