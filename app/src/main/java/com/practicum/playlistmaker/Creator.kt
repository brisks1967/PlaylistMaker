package com.practicum.playlistmaker

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.network.SearchHistory
import com.practicum.playlistmaker.domain.models.TrackConverter

object Creator {


    fun createClient(): RetrofitNetworkClient {
        return RetrofitNetworkClient()
    }
    fun createTrackConverter(): TrackConverter {
        return TrackConverter()
    }

    fun createSearchHistory(context: Context): SearchHistory {
          return SearchHistory(context)
    }




        // Логика расчета радиуса вынесена сюда
        fun calculateRadiusPx(context: Context, radiusDp: Int): Int {
            val density = context.resources.displayMetrics.density
            return (radiusDp * density).toInt()
        }

    fun loadCoverImageGlideAP(context: Context, imageView: ImageView, url: String?) {
        val radiusPx = calculateRadiusPx(context, 8)

             Glide.with(context)
            .load(url)
            .placeholder(R.drawable.image_hint_ap) // Что показать, пока грузится
            .transform(RoundedCorners(radiusPx)) // скругления углов
            .into(imageView)    // Куда вставить картинку
    }


}