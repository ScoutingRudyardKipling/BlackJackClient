package com.engency.blackjack

import android.os.Bundle
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import com.engency.blackjack.Models.Product
import com.engency.blackjack.stores.ProductStore

class SimpleTextActivity : AppCompatActivity() {


    private lateinit var productStore: ProductStore
    private lateinit var product: Product

    private lateinit var tvTitle: TextView
    private lateinit var btnOkDoei: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_popup_simple_text)

        productStore = ProductStore(applicationContext)

        tvTitle = findViewById(R.id.tv_title)
        btnOkDoei = findViewById(R.id.btn_ok_doei)

        btnOkDoei.setOnClickListener {
            finish()
        }

        tvTitle.text = intent.extras!!.getString("text")
    }

    private fun loadData() {
        tvTitle.text = String.format(resources.getString(R.string.title_wild_product_appears), product.name.toUpperCase())
    }

    companion object {
        fun newIntent(context: Context, text: String): Intent {
            val intent = Intent(context, SimpleTextActivity::class.java)
            intent.putExtra("text", text)
            return intent
        }
    }

}
