package com.engency.blackjack

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.engency.blackjack.Models.TeamScore


class ScoreAdapter(private val context: Context, private var dataSource: List<TeamScore>) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    fun setData(items: List<TeamScore>) {
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
        val rowView = inflater.inflate(R.layout.list_item_team_score, parent, false)

        // Get title element
        val titleTeamName = rowView.findViewById(R.id.lv_team_score_name) as TextView
        val titleGroup = rowView.findViewById(R.id.lv_team_score_group) as TextView
        val titleTeamScore = rowView.findViewById(R.id.lv_team_score_score) as TextView

        val team = getItem(position) as TeamScore

        titleTeamName.text = team.index.toString() + " - " + team.name
        titleTeamScore.text = team.score.toString()
        titleGroup.text = team.group

        return rowView
    }
}