package com.engency.blackjack

import android.os.Bundle
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.engency.blackjack.Models.Product
import com.engency.blackjack.stores.ProductStore

class ProductPreviewActivity : AppCompatActivity() {


    private lateinit var productStore: ProductStore
    private lateinit var product: Product

    private lateinit var ivImage: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var btnOkDoei: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_preview)

        productStore = ProductStore(applicationContext)
        product = productStore.getById(intent.extras!!.getInt("product"))!!

        ivImage = findViewById(R.id.iv_image)
        tvTitle = findViewById(R.id.tv_title)
        btnOkDoei = findViewById(R.id.btn_ok_doei)

        btnOkDoei.setOnClickListener{
            finish()
        }

        loadData()
    }

    private fun loadData() {
        Glide.with(applicationContext)
                .asBitmap()
                .load("https://blackjack.engency.com:3000/images/" + product.image)
                .into(ivImage)
        tvTitle.text = String.format(resources.getString(R.string.title_wild_product_appears), product.name.toUpperCase())
    }

    companion object {
        fun newIntent(context: Context, product: Product): Intent {
            val intent = Intent(context, ProductPreviewActivity::class.java)
            intent.putExtra("product", product.id)
            return intent
        }
    }

}
