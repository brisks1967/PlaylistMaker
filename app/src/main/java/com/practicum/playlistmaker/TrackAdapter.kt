package com.practicum.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter(private val onItemClick: (Track) -> Unit) : RecyclerView.Adapter<TrackViewHolder> () {

    var data: List<Track> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = data[position]
        holder.bind(track)

        holder.itemView.setOnClickListener {
            onItemClick(track)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
    fun submitList(newData: List<Track>) {
        data = newData
        notifyDataSetChanged()
    }
}