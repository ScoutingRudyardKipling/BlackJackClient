package com.engency.jotarudyardkipling

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.engency.jotarudyardkipling.Models.Product

class ProductAdapter(private val context: Context, private var dataSource: List<Product>) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    fun setData(items: List<Product>) {
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
        val detailTextView = rowView.findViewById(R.id.lvProduct_detail) as TextView
        val imageView = rowView.findViewById(R.id.lvProduct_image) as ImageView

        val product = getItem(position) as Product

        Glide.with(this.context)
                .asBitmap()
                .load("https://blackjack.engency.com:4000/images/" + product.image)
                .into(imageView)

        titleTextView.text = product.name

        if (product.rewarded) {
            detailTextView.text = "Voltooid"
            rowView.setBackgroundColor(Color.parseColor("#c8f9cb"))
        } else {
            detailTextView.text = "Unlocked"
            rowView.setBackgroundColor(Color.parseColor("#c8d2f9"))
        }


        return rowView
    }
}