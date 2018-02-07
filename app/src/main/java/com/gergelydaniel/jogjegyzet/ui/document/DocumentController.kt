package com.gergelydaniel.jogjegyzet.ui.document

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.christianbahl.conductor.ConductorInjection
import com.gergelydaniel.jogjegyzet.R
import com.gergelydaniel.jogjegyzet.ui.BaseController
import com.gergelydaniel.jogjegyzet.util.hide
import com.gergelydaniel.jogjegyzet.util.show
import kotlinx.android.synthetic.main.controller_document.view.*
import javax.inject.Inject

private const val KEY_ID = "docId"

class DocumentController(private val docId : String) : BaseController() {
    @Inject
    internal lateinit var presenter : DocumentPresenter

    constructor(args: Bundle) : this(args.getString(KEY_ID))

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.controller_document, container, false)
    }

    override fun onFirstAttach() {
        ConductorInjection.inject(this)
    }

    override fun onAttach(view: View) {
        super.onAttach(view)

        presenter.getViewModel(docId)
                .compose(bindToLifecycle())
                .subscribe(::render)
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

                view.document_details_name.text = vm.document.name
                if(vm.document.desc.isNullOrEmpty()) {
                    view.label_document_description.hide()
                    view.document_details_description.hide()
                } else {
                    view.label_document_description.show()
                    view.document_details_description.show()
                    view.document_details_description.text = vm.document.desc
                }
                view.document_details_downloads.text = vm.document.downloads.toString()
            }
        }
    }

}