package com.engency.blackjack

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.engency.blackjack.network.GroupsResponse
import com.engency.blackjack.network.NetworkHelper
import com.engency.blackjack.network.ServerTeamScore
import com.engency.blackjack.stores.ScoreStore

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ScoreOverview.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ScoreOverview.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ScoreOverview : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var properties: GroupPropertyManager
    private lateinit var scoreStore: ScoreStore
    private lateinit var lvScores: ListView
    private lateinit var srlScores: SwipeRefreshLayout
    private var scoreAdapter: ScoreAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v: View = inflater.inflate(R.layout.fragment_score_overview, container, false)


        srlScores = v.findViewById(R.id.srl_scores)
        srlScores.setOnRefreshListener(this)
        lvScores = v.findViewById(R.id.lv_scores)
        scoreAdapter = ScoreAdapter(activity!!.applicationContext, scoreStore.getAllSorted())
        lvScores.adapter = scoreAdapter

        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        this.properties = GroupPropertyManager(context)
        this.scoreStore = ScoreStore(context)
    }

    override fun onStart() {
        super.onStart()
        reloadListview()
        onRefresh()
    }

    override fun onRefresh() {
        NetworkHelper.listScores(
                properties.get("token")!!,
                success = { response: GroupsResponse -> this.scoreStore.clear(); this.scoreStore.addAllFromServer(response.scores); reloadListview() },
                failure = { this.srlScores.isRefreshing = false }
        )
    }

    private fun reloadListview() {
        if (this.properties.has("token")) {
            scoreAdapter?.setData(scoreStore.getAllSorted())
            scoreAdapter?.notifyDataSetChanged()
            this.srlScores.isRefreshing = false
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment ScoreOverview.
         */
        @JvmStatic
        fun newInstance() = ScoreOverview()
    }
}
