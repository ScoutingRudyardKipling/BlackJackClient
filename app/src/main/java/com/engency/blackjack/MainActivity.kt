package com.engency.blackjack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.engency.blackjack.network.NetworkHelper
import com.engency.blackjack.network.OnNetworkResponseInterface
import com.engency.blackjack.stores.ProductStore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import org.json.JSONObject

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener, OnNetworkResponseInterface {



    private lateinit var properties: GroupPropertyManager
    private lateinit var productStore: ProductStore
    private lateinit var lvProducts: ListView
    private lateinit var srlProducts: SwipeRefreshLayout

    private lateinit var productAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.properties = GroupPropertyManager(applicationContext)
        this.productStore = ProductStore(applicationContext)

        if (this.properties.has("token")) {
            openView()
        } else {
            startActivity(LoginActivity.newIntent(this))
        }

    }

    override fun onResume() {
        super.onResume()

        reloadListview()
    }

    private fun openView() {
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        openListView()

        fab.setOnClickListener { view ->
            val intent = BarcodeScannerActivity.newIntent(this)
            startActivity(intent)
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    private fun openListView() {
        srlProducts = findViewById(R.id.srlProducts)
        srlProducts.setOnRefreshListener(this)
        lvProducts = findViewById(R.id.lvProducts)
        productAdapter = ProductAdapter(applicationContext, productStore.getAll())
        lvProducts.adapter = productAdapter
        lvProducts.setOnItemClickListener { a,b,index,d ->
            val product = productStore.getAll()[index]
            startActivity(ProductDetails.newIntent(this, product))
        }
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
                // Handle the camera action
            }
            R.id.nav_scores -> {

            }
            R.id.nav_logout -> {
                properties.clear()
                startActivity(LoginActivity.newIntent(this))
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onRefresh() {
        NetworkHelper.getGroupInfo(properties.get("token")!!, this)
    }

    override fun success(data: JSONObject) {
        properties.updateWithGroupInstance(data)
        reloadListview()
    }

    override fun failure(message: String) {
        this.srlProducts.isRefreshing = false
    }

    private fun reloadListview() {
        productAdapter.setData(productStore.getAll())
        productAdapter.notifyDataSetChanged()
        this.srlProducts.isRefreshing = false
    }

    companion object {

        fun newIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }

}
