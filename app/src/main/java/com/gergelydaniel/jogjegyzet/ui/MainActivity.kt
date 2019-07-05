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
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), ControllerChangeHandler.ControllerChangeListener {
    private lateinit var router: Router

    private lateinit var subscriptions: CompositeDisposable

    private val currentController: Subject<BaseController> = BehaviorSubject.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AndroidInjection.inject(this)

        router = Conductor.attachRouter(this, outlet, savedInstanceState)
        if (!router.hasRootController()) {
            val homeController = HomeController()
            router.setRoot(RouterTransaction.with(homeController))
            currentController.onNext(homeController)
        }
        //if (titleSub == null) {
        //    val current = router.backstack.last().controller()
        //    if (current is BaseController) {
        //        titleSub = subscribeToTitle(current)
        //    }
        //}

        router.addChangeListener(this)

        toolbar.backVisible = false
        toolbar.onBackPressed = ::onBackPressed
        toolbar.onTextChanged = ::onQueryTextChange
        toolbar.onSearchCancelled = this::onSearchCancelled
    }

    private fun onSearchCancelled() {
        if (currentController() is SearchController) {
            router.popCurrentController()
        }
    }

    override fun onChangeStarted(to: Controller?, from: Controller?, isPush: Boolean, container: ViewGroup, handler: ControllerChangeHandler) {
        if (to is BaseController) currentController.onNext(to)

        if (to !is SearchController) {
            toolbar.searchEnabled = to is HomeController
            toolbar.backVisible = to !is HomeController
        } else if (from !is HomeController) {
            toolbar.setSearchState(to.query)
        }
    }

    override fun onChangeCompleted(to: Controller?, from: Controller?, isPush: Boolean, container: ViewGroup, handler: ControllerChangeHandler) {
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

    override fun onResume() {
        super.onResume()

        val titleSub = currentController
                .switchMap { it.title }
                .subscribe {
                    toolbar.title = it
                }

        subscriptions = CompositeDisposable()
        subscriptions.add(titleSub)
    }

    override fun onPause() {
        super.onPause()

        if (! subscriptions.isDisposed)
            subscriptions.dispose()
    }
}
