package com.tutuland.dogdroid.ui

import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.tutuland.dogdroid.databinding.DogListActivityBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class DogListActivity : AppCompatActivity() {
    private val viewModel: DogListViewModel by viewModel()
    private lateinit var binding: DogListActivityBinding
    private lateinit var adapter: DogListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViews()
        lifecycleScope.launch {
            viewModel.flowState()
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect(::renderState)
        }
    }

    private fun setupViews() {
        binding = DogListActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapter = DogListAdapter { viewModel.toggleFavorite(it) }
        binding.dogList.layoutManager = GridLayoutManager(this, getSpanCount())
        binding.dogList.adapter = adapter
        binding.swipeRefresh.setOnRefreshListener { forceRefresh() }
    }

    private fun getSpanCount(): Int =
        if (resources.configuration.orientation == ORIENTATION_PORTRAIT) 2 else 4

    private fun forceRefresh() {
        ForceRefreshDialogFragment().show(supportFragmentManager, "forceRefresh")
        binding.swipeRefresh.isRefreshing = viewModel.loadingState
    }

    private fun renderState(state: DogListViewState) {
        binding.swipeRefresh.isRefreshing = state.isLoading
        binding.errorState.visibility = if (state.showError) View.VISIBLE else View.GONE
        adapter.submitList(state.dogList)
    }
}
