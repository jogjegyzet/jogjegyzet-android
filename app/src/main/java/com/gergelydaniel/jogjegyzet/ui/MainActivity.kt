package com.gergelydaniel.jogjegyzet.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.ViewGroup
import com.bluelinelabs.conductor.*
import com.gergelydaniel.jogjegyzet.R
import com.gergelydaniel.jogjegyzet.ui.category.CategoryController
import com.gergelydaniel.jogjegyzet.ui.search.SearchController
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private lateinit var router: Router
    var searchView: SearchView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        AndroidInjection.inject(this)

        router = Conductor.attachRouter(this, outlet, savedInstanceState)
        if (!router.hasRootController()) {
            router.setRoot(RouterTransaction.with(CategoryController()))
        }
        router.addChangeListener(object : ControllerChangeHandler.ControllerChangeListener {
            override fun onChangeStarted(to: Controller?, from: Controller?, isPush: Boolean, container: ViewGroup, handler: ControllerChangeHandler) {

            }

            override fun onChangeCompleted(to: Controller?, from: Controller?, isPush: Boolean, container: ViewGroup, handler: ControllerChangeHandler) {
                if(to!! !is SearchController) {
                    searchView?.setQuery("", false)
                    searchView?.clearFocus()
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val item = menu!!.findItem(R.id.menuSearch)
        searchView = item.actionView as SearchView
        val searchView = searchView!!

        searchView.maxWidth = Integer.MAX_VALUE
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(q: String): Boolean {


                return false
            }

            override fun onQueryTextChange(q: String): Boolean {
                if (q.isNotEmpty()) {

                    val currentController = router.backstack.last().controller()

                    val searchController = if (currentController !is SearchController) {
                        val newController = SearchController()
                        router.pushController(RouterTransaction.with(newController))
                        newController
                    } else {
                        currentController
                    }

                    searchController.query = q
                } else {
                    if (router.backstack[0].controller() is SearchController) {
                        router.popCurrentController()
                    }
                }

                return false
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        if (!router.handleBack()) {
            super.onBackPressed()
        }
    }
}
