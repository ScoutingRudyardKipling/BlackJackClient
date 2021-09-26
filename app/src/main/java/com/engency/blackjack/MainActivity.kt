package com.engency.blackjack

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.engency.blackjack.stores.ProductStore
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, OnRequestDataUpdate {

    private var loggedIn: Boolean = false

    private lateinit var properties: GroupPropertyManager
    private lateinit var productStore: ProductStore
    private lateinit var tvPoints: TextView
    private lateinit var llMainContainer: LinearLayout

    private val fragmentProducts: ProductOverview = ProductOverview()
    private val fragmentScores: ScoreOverview = ScoreOverview()

    private var fragmentActive: Fragment? = null
    private var drawerLayout: DrawerLayout? = null

    private val dataUpdateReceiver: DataUpdateReceiver = DataUpdateReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.properties = GroupPropertyManager(applicationContext)
        this.productStore = ProductStore(applicationContext)

        if (this.properties.has("token")) {
            loggedIn = true
            openView()

            fragmentProducts.setOnRefreshData(this)
        } else {
            loggedIn = false
            startActivity(LoginActivity.newIntent(this))
        }
    }

    override fun onResume() {
        super.onResume()

        if (loggedIn) {
            updatePoints()
            val intentFilter = IntentFilter("refresh")
            registerReceiver(dataUpdateReceiver, intentFilter)
        }
    }

    override fun onPause() {
        super.onPause()
        if (loggedIn) {
            unregisterReceiver(dataUpdateReceiver)
        }
    }

    private fun openView() {
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        setSupportActionBar(toolbar)

        tvPoints = findViewById(R.id.tv_points)
        llMainContainer = findViewById(R.id.ll_main_container)

        // open correct fragment

        openFragment(fragmentActive ?: fragmentProducts)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { _ ->
            val intent = BarcodeScannerActivity.newIntent(this)
            startActivity(intent)
        }

        val toggle = ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout!!.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

        // set navigation header props
        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        val headerView = navigationView.getHeaderView(0)
        val tvGroupName = headerView.findViewById(R.id.tv_group_name) as TextView
        tvGroupName.text = properties.get("name")
    }

    fun updatePoints() {
        tvPoints.text = String.format(resources.getString(R.string.points_amount), properties.get("points"))
    }

    override fun onBackPressed() {
        if (drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            drawerLayout!!.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_products -> {
                openFragment(fragmentProducts)
            }
            R.id.nav_scores -> {
                openFragment(fragmentScores)
            }
            R.id.nav_logout -> {
                properties.clear()
                startActivity(LoginActivity.newIntent(this))
            }
        }

        drawerLayout!!.closeDrawer(GravityCompat.START)
        return true
    }

    fun openFragment(fragment: Fragment) {
        if (fragment !== this.fragmentActive) {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            supportFragmentManager.fragments.forEach { cFragment -> fragmentTransaction.remove(cFragment) }
            fragmentTransaction.add(R.id.ll_main_container, fragment)
            fragmentTransaction.commit()

            this.fragmentActive = fragment
        }
    }

    override fun onUpdateRequested(cascade: Boolean) {
        Log.d("FCM", "update yeah")

        properties.reload()
        tvPoints.text = String.format(resources.getString(R.string.points_amount), properties.get("points"))

        if (cascade) {
            fragmentProducts.onUpdateRequested()
        }
    }

    companion object {

        fun newIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }


}
