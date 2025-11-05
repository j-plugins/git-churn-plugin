package com.github.xepozz.git_churn.config

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.BaseState
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(
    name = "GitChurnConfigSettings",
    storages = [Storage("git_churn.xml")]
)
@Service(Service.Level.APP)
class GitChurnConfigSettings :  BaseState(), PersistentStateComponent<GitChurnConfigSettings> {
    var enabled by property(true)
    var duration1Month by property(true)
    var duration3Months by property(false)
    var duration6Months by property(false)
    var duration1Year by property(false)
    var durationFull by property(false)

    var coloring by property(false)

    var excludePatterns: MutableList<String> by property(excludedDefaults) { it == excludedDefaults }
    var maxHistoryDays: Int = 30

    override fun getState() = this
    override fun loadState(state: GitChurnConfigSettings) = copyFrom(state)

    companion object {
        val excludedDefaults = mutableListOf(
            ".*/**",
        )
        fun getInstance(): GitChurnConfigSettings =
            ApplicationManager.getApplication().getService(GitChurnConfigSettings::class.java)
    }
}
