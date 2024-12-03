package com.superbgoal.caritasrig.data.model.buildmanager

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SharedBuild(
    val profileImageUrl: String = "",
    val username: String = "",
    val buildImages: List<String> = emptyList(),
    val userId: String = "",
    val buildId: String = "",
    val title: String = "",
    val imageUrls: List<String> = emptyList(),
    val components: BuildComponents? = null
) : Parcelable