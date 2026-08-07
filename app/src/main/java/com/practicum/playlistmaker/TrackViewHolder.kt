package com.practicum.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class TrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)  {


    private val imageTrack: ImageView = itemView.findViewById(R.id.imageTrack)
    private val nameTrack: TextView = itemView.findViewById(R.id.nameTrack)
    private val artistTrack: TextView = itemView.findViewById(R.id.artistTrack)
    private val durationTrack: TextView = itemView.findViewById(R.id.durationTrack)

    val directRight: ImageView = itemView.findViewById(R.id.directRight)

    fun bind(item: Track){



        Glide.with(itemView)
            .load(item.artworkUrl100)
            .placeholder(R.drawable.barsik)
            .into(imageTrack)

        nameTrack.text=item.trackName
        artistTrack.text=item.artistName
        durationTrack.text=item.trackTime


    }




}