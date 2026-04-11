package com.practicum.myapp

import android.content.Intent
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class TrackAdapter(
    private val onItemClick: ((Track) -> Unit)? = null
) : ListAdapter<Track, TrackAdapter.TrackViewHolder>(TrackDiffCallback()) {

    // Для debounce обработки кликов
    private var lastClickTime: Long = 0
    private val CLICK_DEBOUNCE_DELAY: Long = 1000L // 1 секунда

    inner class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val trackNameTextView: TextView = itemView.findViewById(R.id.trackNameTextView)
        private val artistNameTextView: TextView = itemView.findViewById(R.id.artistNameTextView)
        private val trackTimeTextView: TextView = itemView.findViewById(R.id.trackTimeTextView)
        private val artworkImageView: ImageView = itemView.findViewById(R.id.artworkImageView)

        fun bind(item: Track) {
            // Обработка nullable полей с безопасными значениями по умолчанию
            trackNameTextView.text = item.trackName ?: ""
            artistNameTextView.text = item.artistName ?: ""
            trackTimeTextView.text = item.trackTime

            // Загрузка изображения с использованием Glide
            Glide.with(itemView)
                .load(item.artworkUrl100)
                .placeholder(R.drawable.rounded_corner_background)
                .error(R.drawable.rounded_corner_background)
                .fitCenter()
                .into(artworkImageView)

            // Обработка клика с debounce
            itemView.setOnClickListener {
                val currentTime = SystemClock.elapsedRealtime()
                if (currentTime - lastClickTime < CLICK_DEBOUNCE_DELAY) {
                    return@setOnClickListener
                }
                lastClickTime = currentTime

                onItemClick?.invoke(item)
                // Открытие PlayerActivity с передачей данных трека
                val intent = Intent(itemView.context, PlayerActivity::class.java)
                intent.putExtra("track", item)
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun updateTracks(newTracks: List<Track>) {
        submitList(newTracks)
    }
}

class TrackDiffCallback : DiffUtil.ItemCallback<Track>() {
    override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
        return oldItem.trackName == newItem.trackName && oldItem.artistName == newItem.artistName
    }

    override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
        return oldItem == newItem
    }
}