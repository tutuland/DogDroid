package com.tutuland.dogdroid.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.tutuland.dogdroid.R
import com.tutuland.dogdroid.databinding.DogItemBinding
import com.tutuland.dogdroid.domain.Dog

class DogListAdapter(
    private val clickAction: (Dog) -> Unit,
) : ListAdapter<Dog, DogListAdapter.DogHolder>(DogDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogHolder {
        val binding = DogItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DogHolder(binding)
    }

    override fun onBindViewHolder(holder: DogHolder, position: Int) = holder bind getItem(position)

    inner class DogHolder(
        private val binding: DogItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        infix fun bind(model: Dog) {
            val favoriteDrawable = if (model.preferences.isFavorite) R.drawable.ic_favorite_true else R.drawable.ic_favorite_false
            binding.dogBreed.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, favoriteDrawable, 0)
            binding.dogBreed.text = model.info.breed
            binding.dogImage.load(model.info.imageUrl)
            binding.dogItem.setOnClickListener { clickAction(model) }
            binding.dogItem.tag = favoriteDrawable
        }
    }

    private class DogDiff : DiffUtil.ItemCallback<Dog>() {
        override fun areItemsTheSame(oldItem: Dog, newItem: Dog) = oldItem.info.breed == newItem.info.breed
        override fun areContentsTheSame(oldItem: Dog, newItem: Dog) = oldItem == newItem
        override fun getChangePayload(oldItem: Dog, newItem: Dog): Any {
            val diff = Bundle()
            if (oldItem.info.breed != newItem.info.breed) diff.putString("breed", newItem.info.breed)
            if (oldItem.info.imageUrl != newItem.info.imageUrl) diff.putString("imageUrl", newItem.info.imageUrl)
            if (oldItem.preferences.isFavorite != newItem.preferences.isFavorite)
                diff.putBoolean("isFavorite", newItem.preferences.isFavorite)
            return diff
        }
    }
}
