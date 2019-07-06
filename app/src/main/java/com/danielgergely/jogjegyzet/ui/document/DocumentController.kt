package com.danielgergely.jogjegyzet.ui.document

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.christianbahl.conductor.ConductorInjection
import com.danielgergely.jogjegyzet.R
import com.danielgergely.jogjegyzet.service.DocumentData
import com.danielgergely.jogjegyzet.ui.BaseController
import com.danielgergely.jogjegyzet.util.hide
import com.danielgergely.jogjegyzet.util.show
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.controller_document.view.*
import javax.inject.Inject

private const val KEY_ID = "docId"

class DocumentController : BaseController {
    @Inject
    internal lateinit var presenter : DocumentPresenter

    private lateinit var adapter: CommentAdapter

    private val docId : String
    private val document: DocumentData?

    constructor(docId: String) {
        this.docId = docId
        this.document = null
    }

    constructor(document: DocumentData) {
        this.docId = document.document.id
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

    @SuppressLint("CheckResult")
    override fun onAttach(view: View) {
        super.onAttach(view)

        adapter = CommentAdapter()
        view.comments.layoutManager = LinearLayoutManager(view.context)
        view.comments.adapter = adapter

        val obs = if(document != null)
            presenter.getViewModel(document)
        else
            presenter.getViewModel(docId)

        obs
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(::render)

        presenter.title
                .observeOn(AndroidSchedulers.mainThread())
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

                view.document_details_name.text = document.document.name
                if(document.document.desc.isNullOrEmpty()) {
                    view.label_document_description.hide()
                    view.document_details_description.hide()
                } else {
                    view.label_document_description.show()
                    view.document_details_description.show()
                    view.document_details_description.text = document.document.desc
                }
                view.document_details_downloads.text = document.document.downloads.toString()

                view.num_likes.text = document.document.posRatings.toString()
                view.num_dislikes.text = document.document.negRatings.toString()
                view.rating_bar.setData(document.document.posRatings, document.document.negRatings)

                when (vm.comments) {
                    is CommentsViewModel.Loading -> {
                        view.comments_empty.hide()
                        view.comments.hide()
                    }

                    is CommentsViewModel.Data -> {
                        if (vm.comments.comments.isEmpty()) {
                            view.comments_empty.show()
                            view.comments.hide()
                        } else {
                            view.comments_empty.hide()
                            view.comments.show()
                        }
                        adapter.data = vm.comments.comments
                    }

                    is CommentsViewModel.Error -> {
                        // TODO error message about comments

                        view.comments_empty.hide()
                        view.comments.hide()
                    }
                }
            }
        }
    }

}