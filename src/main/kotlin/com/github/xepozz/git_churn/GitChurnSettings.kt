package com.github.xepozz.git_churn

import com.intellij.openapi.components.BaseState
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@Service(Service.Level.PROJECT)
@State(name = "GitChurnSettings", storages = [Storage("git_churn.xml")])
class GitChurnSettings : BaseState(), PersistentStateComponent<GitChurnSettings> {
    var enabled by property(true)

    override fun getState() = this
    override fun loadState(state: GitChurnSettings) = copyFrom(state)
}