package com.danielgergely.jogjegyzet.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.ViewGroup
import com.bluelinelabs.conductor.*
import com.danielgergely.jogjegyzet.R
import com.danielgergely.jogjegyzet.ui.home.HomeController
import com.danielgergely.jogjegyzet.ui.search.SearchController
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
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
        }

        router.addChangeListener(this)

        toolbar.backVisible = false
        toolbar.onBackPressed = ::onBackPressed
        toolbar.onTextChanged = ::onQueryTextChange
        toolbar.onSearchCancelled = ::onSearchCancelled
        toolbar.onMenuItemClicked = ::onMenuItemClicked
    }

    private fun onSearchCancelled() {
        if (currentController() is SearchController) {
            router.popCurrentController()
        }
    }

    private fun onMenuItemClicked(index: Int) {
        val current = currentController()
        (current as? BaseController)?.onMenuItemClick(index)
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

        val iconSub = currentController
                .switchMap { it.icons }
                .subscribe {
                    toolbar.menuItems = it
                }

        subscriptions = CompositeDisposable()
        subscriptions.add(titleSub)
        subscriptions.add(iconSub)

        val current = currentController()
        if (current is BaseController) {
            currentController.onNext(current)
        }
        toolbar.searchEnabled = current is HomeController
        toolbar.backVisible = current !is HomeController
        if (current is SearchController) {
            toolbar.setSearchState(current.query)
        }
    }

    override fun onPause() {
        super.onPause()

        if (! subscriptions.isDisposed)
            subscriptions.dispose()
    }
}
