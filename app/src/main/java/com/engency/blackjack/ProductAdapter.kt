package com.engency.blackjack

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.engency.blackjack.Models.Product
import android.text.method.TextKeyListener.clear



class ProductAdapter(private val context: Context, private var dataSource: List<Product>) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    fun setData(items: List<Product>) {
//        val alItems = ArrayList<Product>()
//        alItems.addAll(items)
        this.dataSource = items
    }

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Get view for row item
        val rowView = inflater.inflate(R.layout.list_item_product, parent, false)

        // Get title element
        val titleTextView = rowView.findViewById(R.id.lvProduct_name) as TextView
        val imageView = rowView.findViewById(R.id.lvProduct_image) as ImageView

        val product = getItem(position) as Product

        Glide.with(this.context)
                .asBitmap()
                .load("http://blackjack.engency.com:3000/images/" + product.image)
                .into(imageView)

        titleTextView.text = product.name


        return rowView
    }
}