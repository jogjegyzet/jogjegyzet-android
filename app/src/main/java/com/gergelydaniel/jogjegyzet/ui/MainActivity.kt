package com.gergelydaniel.jogjegyzet.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.TransitionChangeHandler
import com.gergelydaniel.jogjegyzet.R
import com.gergelydaniel.jogjegyzet.ui.category.CategoryController
import com.gergelydaniel.jogjegyzet.ui.search.SearchController
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private lateinit var router :Router

    private var searchController = SearchController()
    private var searching = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        AndroidInjection.inject(this)

        router = Conductor.attachRouter(this, outlet, savedInstanceState)
        if (!router.hasRootController()) {
            router.setRoot(RouterTransaction.with(CategoryController()))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val item = menu!!.findItem(R.id.menuSearch)
        val searchView = item.actionView as SearchView

        searchView.maxWidth = Integer.MAX_VALUE
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(q: String): Boolean {
                Log.i("SEARCH", "submit: $q")


                return false
            }

            override fun onQueryTextChange(q: String): Boolean {
                Log.i("SEARCH", "change: $q")

                if(q.isNotEmpty()) {
                    if(searchController.isDestroyed || searchController.isBeingDestroyed) {
                        searchController = SearchController()
                    }

                    if(! searching) {
                        router.pushController(RouterTransaction.with(searchController))
                        searching = true
                    }

                    searchController.query = q
                } else {
                    if(searching) {
                        router.popCurrentController()
                        searching = false
                    }
                }

                return false
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        if(! router.handleBack()){
            super.onBackPressed()
        }
    }
}
