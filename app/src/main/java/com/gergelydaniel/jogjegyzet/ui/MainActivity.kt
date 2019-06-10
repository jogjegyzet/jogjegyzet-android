package com.gergelydaniel.jogjegyzet.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.gergelydaniel.jogjegyzet.R
import com.gergelydaniel.jogjegyzet.ui.home.HomeController
import dagger.android.AndroidInjection
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
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

        toolbar.title = "Jogjegyzet"
    }

    private fun subscribeToTitle(provider: TitleProvider) = provider.title.subscribe {

    }


    override fun onBackPressed() {
        if (!router.handleBack()) {
            super.onBackPressed()
        }
    }
}
