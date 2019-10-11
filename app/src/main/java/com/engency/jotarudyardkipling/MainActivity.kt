package com.engency.jotarudyardkipling

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import com.engency.jotarudyardkipling.stores.ProductStore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import com.engency.jotarudyardkipling.network.FCMRegistrationManager
import android.content.IntentFilter
import android.util.Log
import android.view.View


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, OnRequestDataUpdate {

    private var loggedIn: Boolean = false

    private lateinit var properties: GroupPropertyManager
    private lateinit var productStore: ProductStore
    private lateinit var tvPoints: TextView
    private lateinit var llMainContainer: LinearLayout

    private val fragmentProducts: ProductOverview = ProductOverview()
    private val fragmentScores: ScoreOverview = ScoreOverview()

    private var fragmentActive: Fragment? = null

    private var fcmRegistrationManager: FCMRegistrationManager = FCMRegistrationManager()
    private val dataUpdateReceiver: DataUpdateReceiver = DataUpdateReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.properties = GroupPropertyManager(applicationContext)
        this.productStore = ProductStore(applicationContext)

        if (this.properties.has("token")) {
            loggedIn = true
            openView()
            if (this.properties.get("registered") != "1") {
                fcmRegistrationManager.register(this.properties.get("token")!!, this.properties)
            }

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
        setSupportActionBar(toolbar)

        tvPoints = findViewById(R.id.tv_points)
        llMainContainer = findViewById(R.id.ll_main_container)

        // open correct fragment

        openFragment(fragmentActive ?: fragmentProducts)


        fab.setOnClickListener { view ->
            val intent = BarcodeScannerActivity.newIntent(this)
            startActivity(intent)
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

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
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
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

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    fun openFragment(fragment: Fragment) {
        if (fragment !== this.fragmentActive) {
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            this.fragmentActive?.let { fragmentTransaction.remove(this.fragmentActive!!) }
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
