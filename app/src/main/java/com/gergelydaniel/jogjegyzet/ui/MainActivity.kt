package com.gergelydaniel.jogjegyzet.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import com.gergelydaniel.app.routing.Router
import com.gergelydaniel.jogjegyzet.R
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var router : Router

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        AndroidInjection.inject(this)

        router.outlet = outlet
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val item = menu!!.findItem(R.id.menuSearch)
        val searchView = item.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                Log.i("SEARCH", "submit: $p0")

                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                Log.i("SEARCH", "change: $p0")

                return false
            }
        })

        return super.onCreateOptionsMenu(menu)
    }
}
