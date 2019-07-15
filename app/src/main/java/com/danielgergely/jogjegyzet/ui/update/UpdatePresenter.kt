package com.danielgergely.jogjegyzet.ui.update

import com.danielgergely.jogjegyzet.domain.UpdateMessage
import com.danielgergely.jogjegyzet.service.UpdateMessageService
import io.reactivex.Observable
import javax.inject.Inject

class UpdatePresenter @Inject constructor(private val service: UpdateMessageService) {
    fun viewModel(): Observable<ViewModel> {
        return service.getUpdateMessageStatus()
                .map {
                    when(it) {
                        is UpdateMessage.MustUpdate -> ViewModel.Update(it.message, true)
                        is UpdateMessage.OptionalUpdate -> ViewModel.Update(it.message, false)
                        else -> ViewModel.Empty
                    }
                }
    }

    fun update() {

    }
}

sealed class ViewModel {
    object Empty: ViewModel()
    class Update(val message: String, val obligatory: Boolean): ViewModel()
}