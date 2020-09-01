package com.engency.blackjack

import android.os.Bundle
import android.content.Context
import android.content.Intent
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.engency.blackjack.Models.Product
import com.engency.blackjack.network.GroupInfo
import com.engency.blackjack.network.OnNetworkResponseInterface
import com.engency.blackjack.stores.ProductStore

class ProductDetails : AppCompatActivity(), OnNetworkResponseInterface<GroupInfo> {


    private lateinit var productStore: ProductStore
    private lateinit var product: Product

    private lateinit var ivImage: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var tvStatus: TextView
    private lateinit var tvCosts: TextView
    private lateinit var btnUnlock: Button

    private lateinit var properties: GroupPropertyManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)

        this.properties = GroupPropertyManager(applicationContext)

        productStore = ProductStore(applicationContext)
        product = productStore.getById(intent.extras!!.getInt("product"))!!

        ivImage = findViewById(R.id.iv_image)
        tvTitle = findViewById(R.id.tv_title)
        tvStatus = findViewById(R.id.tv_status)
        tvCosts = findViewById(R.id.tv_costs)
        btnUnlock = findViewById(R.id.btn_unlock)

        loadData()
    }

    private fun loadData() {
        Glide.with(applicationContext)
                .asBitmap()
                .load("${BuildConfig.SERVER_URL}/images/" + product.image)
                .into(ivImage)
        tvTitle.text = product.name

        if (product.rewarded) {
            btnUnlock.isEnabled = false
            tvStatus.text = String.format(resources.getString(R.string.product_details_status_completed), product.reward.toString())
            tvCosts.text = ""
            btnUnlock.visibility = View.INVISIBLE
        } else {
            btnUnlock.isEnabled = false
            tvStatus.text = resources.getString(R.string.product_details_nav_description)

            tvCosts.movementMethod = LinkMovementMethod.getInstance()
            tvCosts.text = Html.fromHtml(product.description, 0)

            btnUnlock.visibility = View.INVISIBLE
        }
    }

    override fun success(data: GroupInfo) {
        properties.updateWithGroupInstance(data)
        product = productStore.getById(product.id)!!

        loadData()

        Snackbar.make(this.btnUnlock, resources.getString(R.string.product_details_notice_start_navigation), Snackbar.LENGTH_LONG).show()
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
