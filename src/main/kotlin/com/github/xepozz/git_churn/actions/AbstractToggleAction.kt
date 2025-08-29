package com.github.xepozz.git_churn.actions

import com.github.xepozz.git_churn.config.GitChurnConfigSettings

import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ToggleOptionAction
import com.intellij.openapi.project.Project
import kotlin.reflect.KMutableProperty0

abstract class AbstractToggleAction(action: AnActionEvent) : ToggleOptionAction.Option {
    val project: Project = action.project!!
    protected val settings by lazy { GitChurnConfigSettings.getInstance() }
    private val projectView by lazy { ProjectView.getInstance(project) }

    override fun isEnabled() = settings.enabled

    override fun isSelected() = option.get()

    override fun setSelected(selected: Boolean) {
        val updated = selected != isSelected

        option.set(selected)

        if (updated) {
            projectView.refresh()
        }
    }

    abstract val option: KMutableProperty0<Boolean>
}