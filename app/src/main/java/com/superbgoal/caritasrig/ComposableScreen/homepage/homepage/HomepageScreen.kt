package com.superbgoal.caritasrig.ComposableScreen.homepage.homepage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kwabenaberko.newsapilib.NewsApiClient
import com.kwabenaberko.newsapilib.models.Article
import com.kwabenaberko.newsapilib.models.request.EverythingRequest
import com.kwabenaberko.newsapilib.models.request.TopHeadlinesRequest
import com.kwabenaberko.newsapilib.models.response.ArticleResponse

class HomepageScreen {

    private val _articles = MutableLiveData<List<Article>>()
    val articles: LiveData<List<Article>> = _articles

    init {
        fetchNewsTopHeadlines()
    }

    fun fetchNewsTopHeadlines() {
        val newsApiClient = NewsApiClient(constant.apiKey)
        val request = TopHeadlinesRequest.Builder().category("TECHNOLOGY").build()

        newsApiClient.getTopHeadlines(request, object : NewsApiClient.ArticlesResponseCallback {
            override fun onSuccess(response: ArticleResponse?) {
                response?.articles?.let {
                    Log.d("HomeViewModel", "Success: $it")
                    _articles.postValue(it)
                }
            }

            override fun onFailure(throwable: Throwable?) {
                throwable?.let {
                    Log.d("HomeViewModel", "Failed: ${it.message}")
                }
            }
        })
    }

    fun fetchEverythingWithQuery(query: String) {
        val newsApiClient = NewsApiClient(constant.apiKey)
        val request = EverythingRequest.Builder().language("en").q(query).build()

        newsApiClient.getEverything(request, object : NewsApiClient.ArticlesResponseCallback {
            override fun onSuccess(response: ArticleResponse?) {
                response?.articles?.let {
                    Log.d("HomeViewModel", "Success: $it")
                    _articles.postValue(it)
                }
            }

            override fun onFailure(throwable: Throwable?) {
                throwable?.let {
                    Log.d("HomeViewModel", "Failed: ${it.message}")
                }
            }
        })
    }
}

