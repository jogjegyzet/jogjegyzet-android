package com.gergelydaniel.jogjegyzet.ui.document

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.christianbahl.conductor.ConductorInjection
import com.gergelydaniel.jogjegyzet.R
import com.gergelydaniel.jogjegyzet.domain.Document
import com.gergelydaniel.jogjegyzet.ui.BaseController
import com.gergelydaniel.jogjegyzet.ui.TitleProvider
import com.gergelydaniel.jogjegyzet.ui.reader.ReaderController
import com.gergelydaniel.jogjegyzet.util.hide
import com.gergelydaniel.jogjegyzet.util.show
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.controller_document.view.*
import javax.inject.Inject

private const val KEY_ID = "docId"

class DocumentController : BaseController, TitleProvider {
    @Inject
    internal lateinit var presenter : DocumentPresenter

    private val docId : String
    private val document: Document?

    constructor(docId: String) {
        this.docId = docId
        this.document = null
    }

    constructor(document: Document) {
        this.docId = document.id
        this.document = document
    }

    constructor(args: Bundle) : this(args.getString(KEY_ID))


    private val titleSubject: BehaviorSubject<String> = BehaviorSubject.create()
    override val title: Observable<String>
        get() = titleSubject

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.controller_document, container, false)
    }

    override fun onFirstAttach() {
        ConductorInjection.inject(this)
    }

    override fun onAttach(view: View) {
        super.onAttach(view)


        val obs = if(document != null)
            presenter.getViewModel(document)
        else
            presenter.getViewModel(docId)

        obs
                .compose(bindToLifecycle())
                .subscribe(::render)

        presenter.title
                .compose(bindToLifecycle())
                .subscribe { titleSubject.onNext(it) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_ID, docId)
    }

    private fun render(vm: ViewModel) {
        val view = view!!
        when(vm) {
            is ViewModel.Loading -> {
                view.document_container.hide()
                view.document_error.hide()
                view.document_progress.show()
            }
            is ViewModel.Error -> {
                view.document_container.hide()
                view.document_error.show()
                view.document_progress.hide()
            }
            is ViewModel.Data -> {
                view.document_container.show()
                view.document_error.hide()
                view.document_progress.hide()

                val document = vm.document

                view.document_details_name.text = document.name
                if(document.desc.isNullOrEmpty()) {
                    view.label_document_description.hide()
                    view.document_details_description.hide()
                } else {
                    view.label_document_description.show()
                    view.document_details_description.show()
                    view.document_details_description.text = document.desc
                }
                view.document_details_downloads.text = document.downloads.toString()


                RxView.clicks(view.button_read)
                        .compose(bindToLifecycle())
                        .subscribe {
                            router.pushController(
                                    RouterTransaction.with(ReaderController(document.fileUrl, document.name))
                                            .popChangeHandler(HorizontalChangeHandler())
                                            .pushChangeHandler(HorizontalChangeHandler())
                            )
                        }
            }
        }
    }

}