package com.danielgergely.jogjegyzet.ui.update

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.christianbahl.conductor.ConductorInjection
import com.danielgergely.jogjegyzet.R
import com.danielgergely.jogjegyzet.ui.BaseController
import com.danielgergely.jogjegyzet.util.vis
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.controller_update.view.*
import javax.inject.Inject

class UpdateController : BaseController() {
    override val title: BehaviorSubject<String> = BehaviorSubject.create()

    @Inject
    lateinit var presenter: UpdatePresenter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.controller_update, container, false)
    }

    override fun onFirstAttach() {
        ConductorInjection.inject(this)
    }

    @SuppressLint("CheckResult")
    override fun onAttach(view: View) {
        super.onAttach(view)

        title.onNext(view.context.getString(R.string.app_name))

        presenter.viewModel()
                .compose(bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(::render)

        RxView.clicks(view.button_update)
                .compose(bindToLifecycle())
                .subscribe {
                    presenter.update()
                }

        RxView.clicks(view.button_later)
                .compose(bindToLifecycle())
                .subscribe {
                    router.popCurrentController()
                }
    }

    private fun render(viewModel: ViewModel) {
        val view = view!!

        when(viewModel) {
            is ViewModel.Update -> {
                view.update_message.text = viewModel.message

                view.button_later.vis = ! viewModel.obligatory
            }
            else -> {
                router.popCurrentController()
            }
        }
    }
}