package com.gergelydaniel.jogjegyzet.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.ViewGroup
import com.bluelinelabs.conductor.*
import com.gergelydaniel.jogjegyzet.R
import com.gergelydaniel.jogjegyzet.ui.home.HomeController
import com.gergelydaniel.jogjegyzet.ui.search.SearchController
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
        if (titleSub == null) {
            val current = router.backstack.last().controller()
            if (current is BaseController) {
                titleSub = subscribeToTitle(current)
            }
        }

        router.addChangeListener(this)

        toolbar.backVisible = false
        toolbar.onBackPressed = ::onBackPressed
        toolbar.onTextChanged = ::onQueryTextChange
        toolbar.onSearchCancelled = this::onSearchCancelled

        resetTitle()
    }

    private fun onSearchCancelled() {
        if (currentController() is SearchController) {
            router.popCurrentController()
        }
    }

    private fun resetTitle() {
        toolbar.title = getString(R.string.app_name)
    }

    override fun onChangeStarted(to: Controller?, from: Controller?, isPush: Boolean, container: ViewGroup, handler: ControllerChangeHandler) {
        titleSub?.dispose()
        if (to is BaseController) {
            titleSub = subscribeToTitle(to)
        } else {
            resetTitle()
        }

        if (to !is SearchController) {
            toolbar.searchEnabled = to is HomeController
            toolbar.backVisible = to !is HomeController
        } else if (from !is HomeController) {
            toolbar.setSearchState(to.query)
        }
    }

    override fun onChangeCompleted(to: Controller?, from: Controller?, isPush: Boolean, container: ViewGroup, handler: ControllerChangeHandler) {
    }

    private fun subscribeToTitle(provider: BaseController) = provider.title.subscribe {
        toolbar.title = it
    }


    override fun onBackPressed() {
        if (!router.handleBack()) {
            super.onBackPressed()
        }
    }

    private fun onQueryTextChange(q: CharSequence) {
        val currentController = currentController()

        val searchController = if (currentController !is SearchController) {
            val newController = SearchController()
            router.pushController(RouterTransaction.with(newController))
            newController
        } else {
            currentController
        }

        searchController.query = q.toString()

    }

    private fun currentController() = router.backstack.last().controller()

}
