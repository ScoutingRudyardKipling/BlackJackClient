package com.engency.blackjack

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.engency.blackjack.network.NetworkHelper
import com.engency.blackjack.network.OnNetworkResponseInterface
import com.engency.blackjack.stores.ProductStore
import org.json.JSONObject

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ProductOverview.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ProductOverview.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ProductOverview : Fragment(), SwipeRefreshLayout.OnRefreshListener, OnNetworkResponseInterface {

    private lateinit var properties: GroupPropertyManager
    private lateinit var productStore: ProductStore
    private lateinit var lvProducts: ListView
    private lateinit var srlProducts: SwipeRefreshLayout
    private var productAdapter: ProductAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v: View = inflater.inflate(R.layout.fragment_product_overview, container, false)


        srlProducts = v.findViewById(R.id.srlProducts)
        srlProducts.setOnRefreshListener(this)
        lvProducts = v.findViewById(R.id.lvProducts)
        productAdapter = ProductAdapter(activity!!.applicationContext, productStore.getAll())
        lvProducts.adapter = productAdapter
        lvProducts.setOnItemClickListener { a, b, index, d ->
            val product = productStore.getAll()[index]
            startActivity(ProductDetails.newIntent(activity!!, product))
        }

        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.properties = GroupPropertyManager(activity!!.applicationContext)
        this.productStore = ProductStore(activity!!.applicationContext)
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onStart() {
        super.onStart()
        reloadListview()
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
        if (this.properties.has("token")) {
            productAdapter?.setData(productStore.getAll())
            productAdapter?.notifyDataSetChanged()
            this.srlProducts.isRefreshing = false
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment ProductOverview.
         */
        @JvmStatic
        fun newInstance() = ProductOverview()
    }
}
