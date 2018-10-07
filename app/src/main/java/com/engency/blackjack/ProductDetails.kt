package com.engency.blackjack

import android.os.Bundle
import android.content.Context
import android.content.Intent
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.engency.blackjack.Models.Product
import com.engency.blackjack.network.NetworkHelper
import com.engency.blackjack.network.OnNetworkResponseInterface
import com.engency.blackjack.stores.ProductStore
import org.json.JSONObject

class ProductDetails : AppCompatActivity(), OnNetworkResponseInterface {


    private lateinit var productStore: ProductStore
    private lateinit var product: Product

    private lateinit var ivImage: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var tvCosts: TextView
    private lateinit var btnUnlock: Button

    private lateinit var properties: GroupPropertyManager

    private var actionPointCounter: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)

        this.properties = GroupPropertyManager(applicationContext)
        this.actionPointCounter = this.properties.get("credits")!!.toInt()

        productStore = ProductStore(applicationContext)
        product = productStore.getById(intent.extras!!.getInt("product"))!!

        ivImage = findViewById(R.id.iv_image)
        tvTitle = findViewById(R.id.tv_title)
        tvCosts = findViewById(R.id.tv_costs)
        btnUnlock = findViewById(R.id.btn_unlock)

        loadData()
    }

    private fun loadData() {
        Glide.with(applicationContext)
                .asBitmap()
                .load("http://blackjack.engency.com:3000/images/" + product.image)
                .into(ivImage)
        tvTitle.text = product.name

        val unlockEnabled: Boolean = product.costs <= this.actionPointCounter
        val detailsEnabled: Boolean = product.bought

        if(detailsEnabled) {
            btnUnlock.isEnabled = false
            tvCosts.text = "Hier komt een beschrijving van hoe je op deze post komt"
            btnUnlock.visibility = View.INVISIBLE
        } else {
            val stringResource: Int = if (unlockEnabled) R.string.costs_unlock_product else R.string.costs_unlock_product_insufficient
            tvCosts.text = String.format(resources.getString(stringResource), product.costs, this.actionPointCounter)
            btnUnlock.isEnabled = unlockEnabled

            if (unlockEnabled && !product.bought) {
                btnUnlock.setOnClickListener { performUnlock() }
            }
        }
    }

    fun performUnlock() {
        NetworkHelper.unlock(this.properties.get("token")!!, this.product.id, this)

    }

    override fun success(data: JSONObject) {
        properties.updateWithGroupInstance(data)
        product = productStore.getById(product.id)!!

        loadData()
    }

    override fun failure(message: String) {
        Snackbar.make(this.btnUnlock, message, Snackbar.LENGTH_LONG).show()
    }

    companion object {
        fun newIntent(context: Context, product: Product): Intent {
            val intent = Intent(context, ProductDetails::class.java)
            intent.putExtra("product", product.id)
            return intent
        }
    }

}
