package com.superbgoal.caritasrig.activity.homepage.newsApi

object ArticleUrlSingleton {
    private var _articleUrl: String? = null

    // Getter untuk mendapatkan URL artikel
    fun getArticleUrl(): String? {
        return _articleUrl
    }

    // Setter untuk mengatur URL artikel
    fun setArticleUrl(url: String) {
        _articleUrl = url
    }
}
