package com.github.xepozz.git_churn.actions

import com.github.xepozz.git_churn.GitChurnService
import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.progress.currentThreadCoroutineScope
import kotlinx.coroutines.launch

abstract class AbstractDurationAction(action: AnActionEvent) : AbstractToggleAction(action) {
    val properties = listOf(
        settings::duration1Month,
        settings::duration3Months,
        settings::duration6Months,
        settings::duration1Year,
        settings::durationFull,
    )

    override fun setSelected(selected: Boolean) {
        if (isSelected) {
            return
        }

        properties
            .filter { it != option }
            .forEach { it.set(false) }
        option.set(true)

        val service = project.getService(GitChurnService::class.java)
        currentThreadCoroutineScope().launch {
            service.refresh()

            ProjectView
                .getInstance(project)
                .currentProjectViewPane
                ?.updateFromRoot(true)
        }
    }
}