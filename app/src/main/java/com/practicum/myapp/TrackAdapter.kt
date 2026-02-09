package com.practicum.myapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class TrackAdapter : ListAdapter<Track, TrackAdapter.TrackViewHolder>(TrackDiffCallback()) {

    inner class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val trackNameTextView: TextView = itemView.findViewById(R.id.trackNameTextView)
        private val artistNameTextView: TextView = itemView.findViewById(R.id.artistNameTextView)
        private val trackTimeTextView: TextView = itemView.findViewById(R.id.trackTimeTextView)
        private val artworkImageView: ImageView = itemView.findViewById(R.id.artworkImageView)

        fun bind(item: Track) {
            trackNameTextView.text = item.trackName
            artistNameTextView.text = item.artistName
            trackTimeTextView.text = item.trackTime

            // Загрузка изображения с использованием Glide
            Glide.with(itemView)
                .load(item.artworkUrl100)
                .placeholder(R.drawable.rounded_corner_background) // Плейсхолдер при загрузке
                .error(R.drawable.rounded_corner_background) // Плейсхолдер при ошибке (например, нет интернета)
                .fitCenter() // Используем fitCenter для масштабирования
                .into(artworkImageView)
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