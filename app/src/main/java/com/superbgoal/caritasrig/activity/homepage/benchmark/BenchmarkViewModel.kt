package com.superbgoal.caritasrig.activity.homepage.benchmark

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.data.model.component.Processor
import com.superbgoal.caritasrig.data.model.component.VideoCard
import com.superbgoal.caritasrig.functions.loadItemsFromResources
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BenchmarkViewModel : ViewModel() {

    private val _processors = MutableStateFlow<List<Processor>>(emptyList())
    val processors: StateFlow<List<Processor>> get() = _processors

    private val _videoCards = MutableStateFlow<List<VideoCard>>(emptyList())
    val videoCards: StateFlow<List<VideoCard>> get() = _videoCards

    private val _favorites = MutableStateFlow<Set<String>>(emptySet())
    val favorites: StateFlow<Set<String>> get() = _favorites

    /**
     * Load data from resources
     */
    fun loadBenchmarkData(context: Context) {
        viewModelScope.launch {
            val loadedProcessors = loadItemsFromResources<Processor>(
                context = context,
                resourceId = R.raw.processor
            )
            val loadedVideoCards = loadItemsFromResources<VideoCard>(
                context = context,
                resourceId = R.raw.videocard
            )

            _processors.value = listOf(loadedProcessors)
            _videoCards.value = listOf(loadedVideoCards)
        }
    }

    /**
     * Toggle favorite status for a given item.
     */
    fun toggleFavorite(itemId: String) {
        viewModelScope.launch {
            _favorites.value = if (_favorites.value.contains(itemId)) {
                _favorites.value - itemId
            } else {
                _favorites.value + itemId
            }
        }
    }
}
