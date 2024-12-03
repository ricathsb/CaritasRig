package com.superbgoal.caritasrig.data.model.buildmanager

import android.os.Parcelable
import com.google.firebase.components.Component
import kotlinx.parcelize.Parcelize
import java.util.UUID
@Parcelize
data class Build(
    val imageuris : List<String> = emptyList(),
    val buildId: String = "",
    val title: String = "",
    val components: BuildComponents? = null // Komponen bisa null karena opsional
): Parcelable

