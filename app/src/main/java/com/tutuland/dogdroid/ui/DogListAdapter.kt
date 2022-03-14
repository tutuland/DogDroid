package com.tutuland.dogdroid.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tutuland.dogdroid.R
import com.tutuland.dogdroid.data.Dog
import com.tutuland.dogdroid.databinding.DogItemBinding

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
            val favoriteDrawable = if (model.isFavorite) R.drawable.ic_favorite_true else R.drawable.ic_favorite_false
            binding.dogBreed.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, favoriteDrawable, 0)
            binding.dogBreed.text = model.breed
            binding.dogImage.load(model.imageUrl)
            binding.dogItem.setOnClickListener { clickAction(model) }
        }
    }

    private class DogDiff : DiffUtil.ItemCallback<Dog>() {
        override fun areItemsTheSame(oldItem: Dog, newItem: Dog) = oldItem.breed == newItem.breed
        override fun areContentsTheSame(oldItem: Dog, newItem: Dog) = oldItem == newItem
        override fun getChangePayload(oldItem: Dog, newItem: Dog): Any {
            val diff = Bundle()
            if (oldItem.breed != newItem.breed) diff.putString("breed", newItem.breed)
            if (oldItem.imageUrl != newItem.imageUrl) diff.putString("imageUrl", newItem.imageUrl)
            if (oldItem.isFavorite != newItem.isFavorite) diff.putBoolean("isFavorite", newItem.isFavorite)
            return diff
        }
    }
}
