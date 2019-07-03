package com.gergelydaniel.jogjegyzet.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import com.bluelinelabs.conductor.*
import com.gergelydaniel.jogjegyzet.R
import com.gergelydaniel.jogjegyzet.ui.home.HomeController
import dagger.android.AndroidInjection
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), ControllerChangeHandler.ControllerChangeListener {
    private lateinit var router: Router

    var titleSub: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AndroidInjection.inject(this)

        router = Conductor.attachRouter(this, outlet, savedInstanceState)
        if (!router.hasRootController()) {
            router.setRoot(RouterTransaction.with(HomeController()))
        }
        if(titleSub == null) {
            val current = router.backstack.last().controller()
            if (current is TitleProvider) {
                titleSub = subscribeToTitle(current)
            }
        }

        router.addChangeListener(this)

        toolbar.backVisible = false
        toolbar.onBackPressed = ::onBackPressed

        resetTitle()
    }

    private fun resetTitle() {
        toolbar.title = getString(R.string.app_name)
    }

    // TODO create an Observable from this
    override fun onChangeStarted(to: Controller?, from: Controller?, isPush: Boolean, container: ViewGroup, handler: ControllerChangeHandler) {
        titleSub?.dispose()
        if (to is TitleProvider) {
            titleSub = subscribeToTitle(to)
        } else {
            resetTitle()
        }

        toolbar.searchEnabled = to is HomeController
        toolbar.backVisible = to !is HomeController
    }

    override fun onChangeCompleted(to: Controller?, from: Controller?, isPush: Boolean, container: ViewGroup, handler: ControllerChangeHandler) {

    }

    private fun subscribeToTitle(provider: TitleProvider) = provider.title.subscribe {
        toolbar.title = it
    }


    override fun onBackPressed() {
        if (!router.handleBack()) {
            super.onBackPressed()
        }
    }
}
