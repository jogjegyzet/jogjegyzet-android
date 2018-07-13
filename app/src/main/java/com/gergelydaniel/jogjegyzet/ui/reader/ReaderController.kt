package com.gergelydaniel.jogjegyzet.ui.reader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import com.gergelydaniel.jogjegyzet.ui.TitleProvider
import com.github.barteksc.pdfviewer.PDFView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.net.URL


private const val KEY_URL = "url"
private const val KEY_TITLE = "title"

class ReaderController(private val url: String, private val docTitle: String) : Controller(), TitleProvider {
    private lateinit var pdfView: PDFView

    constructor(args: Bundle) : this(args.getString(KEY_URL), args.getString(KEY_TITLE))

    override val title: Observable<String>
        get() = Observable.just(docTitle)


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        pdfView = PDFView(inflater.context, null)
        pdfView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        return pdfView
    }

    override fun onAttach(view: View) {
        super.onAttach(view)

        //val temp = url.replace("https://api.jogjegyzet.hu/", "http://192.168.0.23:8080/")

        Observable.fromCallable { URL(url).openStream() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    pdfView.fromStream(it)
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

    override fun onSaveViewState(view: View, outState: Bundle) {
        super.onSaveViewState(view, outState)
    }

}
