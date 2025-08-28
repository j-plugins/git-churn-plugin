package com.github.xepozz.git_churn.actions

import com.github.xepozz.git_churn.GitChurnSettings
import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ToggleOptionAction
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import kotlin.reflect.KMutableProperty0

abstract class AbstractToggleAction(action: AnActionEvent) : ToggleOptionAction.Option {
    val project: Project = action.project!!
    val settings = project.service<GitChurnSettings>()

    override fun isEnabled() = settings.enabled

    override fun isSelected() = option.get()

    override fun setSelected(selected: Boolean) {
        val updated = selected != isSelected

        option.set(selected)

        if (updated) {
            project.let { project ->
                ProjectView
                    .getInstance(project)
                    .currentProjectViewPane
                    ?.updateFromRoot(true)
            }
        }
    }

    abstract val option: KMutableProperty0<Boolean>
}