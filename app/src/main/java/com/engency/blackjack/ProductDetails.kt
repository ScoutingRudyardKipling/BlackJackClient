package com.engency.blackjack

import android.os.Bundle
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.engency.blackjack.Models.Product
import com.engency.blackjack.stores.ProductStore

class ProductDetails : AppCompatActivity() {

    private lateinit var productStore: ProductStore
    private lateinit var product: Product

    private lateinit var ivImage: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var tvDescription: TextView
    private lateinit var btnUnlock: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)

        productStore = ProductStore(applicationContext)
        product = productStore.getById(intent.extras!!.getInt("product"))!!

        ivImage = findViewById(R.id.iv_image)
        tvTitle = findViewById(R.id.tv_title)
        tvDescription = findViewById(R.id.tv_description)
        btnUnlock = findViewById(R.id.btn_unlock)

        loadData()
    }

    private fun loadData() {
        Glide.with(applicationContext)
                .asBitmap()
                .load("http://blackjack.engency.com:3000/images/" + product.image)
                .into(ivImage)
        tvTitle.text = product.name
    }

    companion object {
        fun newIntent(context: Context, product: Product): Intent {
            val intent = Intent(context, ProductDetails::class.java)
            intent.putExtra("product", product.id)
            return intent
        }
    }

}
