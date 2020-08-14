package com.engency.blackjack

import android.os.Bundle
import android.content.Context
import android.content.Intent
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.text.method.LinkMovementMethod
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
                .load("https://blackjack.engency.com:3000/images/" + product.image)
                .into(ivImage)
        tvTitle.text = product.name

        if (product.rewarded) {
            btnUnlock.isEnabled = false
            tvStatus.text = "Je hebt hier al " + product.reward.toString() + " punten voor gekregen."
            tvCosts.text = ""
            btnUnlock.visibility = View.INVISIBLE
        } else {
            btnUnlock.isEnabled = false
            tvStatus.text = "Met onderstaande beschrijving kan je naar de bijbehorende post rijden:"

            tvCosts.movementMethod = LinkMovementMethod.getInstance()
            tvCosts.text = Html.fromHtml(product.description)

            btnUnlock.visibility = View.INVISIBLE
        }
    }

    override fun success(data: JSONObject) {
        properties.updateWithGroupInstance(data)
        product = productStore.getById(product.id)!!

        loadData()

        Snackbar.make(this.btnUnlock, "Yes! Je kan nu naar dit product rijden!", Snackbar.LENGTH_LONG).show()

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
