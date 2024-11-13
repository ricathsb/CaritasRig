package com.superbgoal.caritasrig.data.model.test

object BuildManager {
    // This will hold the current build title globally
    var currentBuildTitle: String? = null

    // Function to set the build title
    fun setBuildTitle(title: String) {
        currentBuildTitle = title
    }

    // Function to get the current build title
    fun getBuildTitle(): String? {
        return currentBuildTitle
    }
}

