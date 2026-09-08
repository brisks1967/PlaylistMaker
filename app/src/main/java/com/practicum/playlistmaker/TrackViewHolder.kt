package com.practicum.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners


class TrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)  {
    private val imageTrack: ImageView = itemView.findViewById(R.id.imageTrack)
    private val nameTrack: TextView = itemView.findViewById(R.id.nameTrack)
    private val artistTrack: TextView = itemView.findViewById(R.id.artistTrack)
    private val durationTrack: TextView = itemView.findViewById(R.id.durationTrack)

    val directRight: ImageView = itemView.findViewById(R.id.directRight)

    fun bind(item: Track){

        val radiusDp = 2
        val density2 = imageTrack.context.resources.displayMetrics.density
        val radiusPx = (radiusDp * density2).toInt()

                Glide.with(itemView)
            .load(item.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(radiusPx)) // скругления углов
            .into(imageTrack)

        nameTrack.text=item.trackName
        artistTrack.text=item.artistName
        durationTrack.text=formatDuration(item.trackTimeMillis)
    }

      fun formatDuration(millis: Long): String{
        val minutes = (millis/1000) / 60
        val seconds = (millis/1000) % 60
        return String.format("%d:%02d", minutes, seconds)
    }
}