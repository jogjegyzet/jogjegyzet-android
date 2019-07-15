package com.danielgergely.jogjegyzet.ui

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.bluelinelabs.conductor.*
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.danielgergely.jogjegyzet.R
import com.danielgergely.jogjegyzet.domain.UpdateMessage
import com.danielgergely.jogjegyzet.service.UpdateMessageService
import com.danielgergely.jogjegyzet.ui.home.HomeController
import com.danielgergely.jogjegyzet.ui.search.SearchController
import com.danielgergely.jogjegyzet.ui.update.UpdateController
import dagger.android.AndroidInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class MainActivity : AppCompatActivity(), ControllerChangeHandler.ControllerChangeListener {
    private lateinit var router: Router

    private lateinit var subscriptions: CompositeDisposable
    private lateinit var updateSub: Disposable

    private val currentController: Subject<BaseController> = BehaviorSubject.create()

    @Inject
    lateinit var updateService: UpdateMessageService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AndroidInjection.inject(this)

        router = Conductor.attachRouter(this, outlet, savedInstanceState)

        updateSub = updateService.getUpdateMessageStatus()
                .take(1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (!router.hasRootController()) {
                        when (it) {
                            is UpdateMessage.None -> {
                                val homeController = HomeController()
                                router.setRoot(RouterTransaction.with(homeController))
                            }
                            is UpdateMessage.MustUpdate -> {
                                val updateController = UpdateController()
                                router.setRoot(RouterTransaction.with(updateController))
                            }
                            is UpdateMessage.OptionalUpdate -> {
                                val homeController = HomeController()
                                val updateController = UpdateController()

                                router.setRoot(RouterTransaction.with(homeController))
                                router.pushController(
                                        RouterTransaction.with(updateController)
                                                .popChangeHandler(HorizontalChangeHandler())
                                )
                            }
                        }
                    }
                    setToolbar()
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
            toolbar.backVisible = to != null && to !is HomeController
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

    private fun currentController() = router.backstack.lastOrNull()?.controller()

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

        setToolbar()
    }

    private fun setToolbar() {
        val current = currentController()
        if (current is BaseController) {
            currentController.onNext(current)
        }
        toolbar.searchEnabled = current is HomeController
        toolbar.backVisible = router.backstackSize > 1
        if (current is SearchController) {
            toolbar.setSearchState(current.query)
        }
    }

    override fun onPause() {
        super.onPause()

        if (!subscriptions.isDisposed)
            subscriptions.dispose()
    }

    override fun onDestroy() {
        super.onDestroy()

        updateSub.dispose()
    }
}
